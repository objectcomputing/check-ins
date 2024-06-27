package com.objectcomputing.checkins.services.file;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class FileServicesImpl implements FileServices {

    private static final Logger LOG = LoggerFactory.getLogger(FileServicesImpl.class);

    private final GoogleApiAccess googleApiAccess;
    private final CheckInServices checkInServices;
    private final CheckinDocumentServices checkinDocumentServices;
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;
    private final GoogleServiceConfiguration googleServiceConfiguration;

    public FileServicesImpl(GoogleApiAccess googleApiAccess,
                            CheckInServices checkInServices,
                            CheckinDocumentServices checkinDocumentServices,
                            MemberProfileServices memberProfileServices,
                            CurrentUserServices currentUserServices,
                            GoogleServiceConfiguration googleServiceConfiguration) {
        this.googleApiAccess = googleApiAccess;
        this.checkInServices = checkInServices;
        this.checkinDocumentServices = checkinDocumentServices;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
        this.googleServiceConfiguration = googleServiceConfiguration;
    }

    @Override
    public Set<FileInfoDTO> findFiles(@Nullable UUID checkInID) {

        boolean isAdmin = currentUserServices.isAdmin();
        validate(checkInID == null && !isAdmin, NOT_AUTHORIZED_MSG);

        try {
            Set<FileInfoDTO> result = new HashSet<>();
            Drive drive = googleApiAccess.getDrive();
            validate(drive == null, "Unable to access Google Drive");

            String rootDirId = googleServiceConfiguration.getDirectoryId();
            validate(rootDirId == null, "No destination folder has been configured. Contact your administrator for assistance.");

            if (checkInID == null && isAdmin) {
                FileList driveIndex = getFoldersInRoot(drive, rootDirId);
                driveIndex.getFiles().stream().forEach(folder -> {
                    try {
                        //find all
                        FileList fileList = drive.files().list().setSupportsAllDrives(true)
                                .setIncludeItemsFromAllDrives(true)
                                .setQ(String.format("'%s' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed != true", folder.getId()))
                                .setSpaces("drive")
                                .setFields("files(id, name, parents, size)")
                                .execute();
                        fileList.getFiles().stream()
                                .forEach(file -> result.add(setFileInfo(file, null)));
                    } catch (IOException ioe) {
                        LOG.error("Error occurred while retrieving files from Google Drive.", ioe);
                        throw new FileRetrievalException(ioe.getMessage());
                    }
                });
            } else if (checkInID != null) {
                validate(!checkInServices.accessGranted(checkInID, currentUserServices.getCurrentUser().getId()),
                        "You are not authorized to perform this operation");

                Set<CheckinDocument> checkinDocuments = checkinDocumentServices.read(checkInID);
                for (CheckinDocument cd : checkinDocuments) {
                    File file = drive.files().get(cd.getUploadDocId()).setSupportsAllDrives(true).execute();
                    result.add(setFileInfo(file, cd));
                }
            }

            return result;
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    @Override
    public java.io.File downloadFiles(@NotNull String uploadDocId) {

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        CheckinDocument cd = checkinDocumentServices.getFindByUploadDocId(uploadDocId);
        validate(cd == null, String.format("Unable to find record with id %s", uploadDocId));

        CheckIn associatedCheckin = checkInServices.read(cd.getCheckinsId());

        if(!isAdmin) {
            validate((!currentUser.getId().equals(associatedCheckin.getTeamMemberId()) && !currentUser.getId().equals(associatedCheckin.getPdlId())), NOT_AUTHORIZED_MSG);
        }
        try {
            java.io.File file = java.io.File.createTempFile("tmp", ".txt");
            file.deleteOnExit();
            try(
                FileOutputStream myWriter = new FileOutputStream(file)
            ) {
                Drive drive = googleApiAccess.getDrive();
                validate(drive == null, "Unable to access Google Drive");

                drive.files().get(uploadDocId).setSupportsAllDrives(true).executeMediaAndDownloadTo(myWriter);
                myWriter.close();

                return file;
            } catch (IOException e) {
                LOG.error("Error occurred while retrieving files from Google Drive.", e);
                throw new FileRetrievalException(e.getMessage());
            }
        } catch(IOException e) {
            LOG.error("Error occurred while attempting to create a temporary file.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    @Override
    public FileInfoDTO uploadFile(@NotNull UUID checkInID, @NotNull CompletedFileUpload file) {

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        validate((file.getFilename() == null || file.getFilename().equals("")), "Please select a valid file before uploading.");

        CheckIn checkIn = checkInServices.read(checkInID);
        validate(checkIn == null, "Unable to find checkin record with id %s", checkInID);
        if(!isAdmin) {
            validate((!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())), "You are not authorized to perform this operation");
            validate(checkIn.isCompleted(), NOT_AUTHORIZED_MSG);
        }

        // create folder for each team member
        final String directoryName = MemberProfileUtils.getFullName(memberProfileServices.getById(checkIn.getTeamMemberId()));

        try {
            Drive drive = googleApiAccess.getDrive();
            validate(drive == null, "Unable to access Google Drive");

            String rootDirId = googleServiceConfiguration.getDirectoryId();
            validate(rootDirId == null, "No destination folder has been configured. Contact your administrator for assistance.");

            // Check if folder already exists on google drive. If exists, return folderId and name
            FileList driveIndex = getFoldersInRoot(drive, rootDirId);
            File folderOnDrive = driveIndex.getFiles().stream()
                    .filter(s -> directoryName.equalsIgnoreCase(s.getName()))
                    .findFirst()
                    .orElse(null);

            // If folder does not exist on Drive, create a new folder in the format name-date
            if(folderOnDrive == null) {
                folderOnDrive = createNewDirectoryOnDrive(drive, directoryName, rootDirId);
            }

            // set file metadata
            File fileMetadata = new File();
            fileMetadata.setName(file.getFilename());
            fileMetadata.setMimeType(file.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE).toString());
            fileMetadata.setParents(Collections.singletonList(folderOnDrive.getId()));

            //upload file to google drive
            InputStreamContent content = new InputStreamContent(file.getContentType().toString(), file.getInputStream());

            File uploadedFile = drive.files().create(fileMetadata, content)
                                .setSupportsAllDrives(true)
                                .setFields("id, size, name")
                                .execute();

            //create record in checkin-document service
            CheckinDocument cd = new CheckinDocument(checkInID, uploadedFile.getId());
            checkinDocumentServices.save(cd);

            return setFileInfo(uploadedFile, cd);
        } catch (GoogleJsonResponseException e) {
            LOG.error("Error occurred while accessing Google Drive.", e);
            throw new FileRetrievalException(e.getMessage());
        } catch (IOException e) {
            LOG.error("Unexpected error processing file upload.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    private FileList getFoldersInRoot(Drive drive, String rootDirId) throws IOException {
        return drive.files().list().setSupportsAllDrives(true)
                .setIncludeItemsFromAllDrives(true)
                .setQ(String.format("'%s' in parents and mimeType = 'application/vnd.google-apps.folder'", rootDirId))
                .setSpaces("drive")
                .setFields("files(id, name, parents, size)")
                .execute();
    }

    @Override
    public boolean deleteFile(@NotNull String uploadDocId) {

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        CheckinDocument cd = checkinDocumentServices.getFindByUploadDocId(uploadDocId);
        validate(cd == null, String.format("Unable to find record with id %s", uploadDocId));

        CheckIn associatedCheckin = checkInServices.read(cd.getCheckinsId());
        if(!isAdmin) {
            validate((!currentUser.getId().equals(associatedCheckin.getTeamMemberId()) && !currentUser.getId().equals(associatedCheckin.getPdlId())), NOT_AUTHORIZED_MSG);
        }

        try {
            Drive drive = googleApiAccess.getDrive();
            validate(drive == null, "Unable to access Google Drive");
            File file = new File().setTrashed(true);
            drive.files().update(uploadDocId, file).setSupportsAllDrives(true).execute();
            checkinDocumentServices.deleteByUploadDocId(uploadDocId);
            return true;
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    private void validate(boolean isError, String message, Object... args) {
        if(isError) {
            throw new FileRetrievalException(String.format(message, args));
        }
    }

    private File createNewDirectoryOnDrive(Drive drive, String directoryName, String parentId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(directoryName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(parentId));
        return drive.files().create(fileMetadata).setSupportsAllDrives(true).execute();
    }

    private FileInfoDTO setFileInfo(File file, CheckinDocument cd) {
        FileInfoDTO dto = new FileInfoDTO();
        dto.setFileId(file.getId());
        dto.setName(file.getName());
        dto.setSize(file.getSize());
        if(cd != null) {
            dto.setCheckInId(cd.getCheckinsId());
        }

        return dto;
    }
}

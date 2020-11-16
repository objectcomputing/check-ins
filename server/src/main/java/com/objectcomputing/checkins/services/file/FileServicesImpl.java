package com.objectcomputing.checkins.services.file;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.utils.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class FileServicesImpl implements FileServices {

    private static final Logger LOG = LoggerFactory.getLogger(FileServicesImpl.class);

    private final GoogleDriveAccessor googleDriveAccessor;
    private final SecurityService securityService;
    private final CheckInServices checkInServices;
    private final CheckinDocumentServices checkinDocumentServices;
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;

    public FileServicesImpl(GoogleDriveAccessor googleDriveAccessor, SecurityService securityService,
                            CheckInServices checkInServices, CheckinDocumentServices checkinDocumentServices,
                            MemberProfileServices memberProfileServices, CurrentUserServices currentUserServices) {
        this.googleDriveAccessor = googleDriveAccessor;
        this.securityService = securityService;
        this.checkInServices = checkInServices;
        this.checkinDocumentServices = checkinDocumentServices;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Set<FileInfoDTO> findFiles(@Nullable UUID checkInID) {

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        validate(checkInID == null && !isAdmin, "You are not authorized to perform this operation");

        try {
            Set<FileInfoDTO> result = new HashSet<>();
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");

            if (checkInID == null && isAdmin) {
                //find all
                FileList fileList = drive.files().list().execute();
                for (File file : fileList.getFiles()) {
                    result.add(setFileInfo(file, null));
                }
            } else if (checkInID != null) {
                //find by CheckIn ID
                CheckIn checkIn = checkInServices.read(checkInID);
                validate(checkIn == null, String.format("Unable to find checkin record with id %s", checkInID));

                if(!isAdmin) {
                    validate((!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())), "You are not authorized to perform this operation");
                }

                Set<CheckinDocument> checkinDocuments = checkinDocumentServices.read(checkInID);
                for (CheckinDocument cd : checkinDocuments) {
                    File file = drive.files().get(cd.getUploadDocId()).execute();
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

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        CheckinDocument cd = checkinDocumentServices.getFindByUploadDocId(uploadDocId);
        validate(cd == null, String.format("Unable to find record with id %s", uploadDocId));

        CheckIn associatedCheckin = checkInServices.read(cd.getCheckinsId());

        if(!isAdmin) {
            validate((!currentUser.getId().equals(associatedCheckin.getTeamMemberId()) && !currentUser.getId().equals(associatedCheckin.getPdlId())), "You are not authorized to perform this operation");
        }

        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            java.io.File file = java.io.File.createTempFile("tmp", ".txt");
            file.deleteOnExit();
            FileWriter myWriter = new FileWriter(file);

            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");

            drive.files().get(uploadDocId).executeMediaAndDownloadTo(outputStream);
            myWriter.write(String.valueOf(outputStream));
            myWriter.close();

            return file;
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    @Override
    public FileInfoDTO uploadFile(@NotNull UUID checkInID, @NotNull CompletedFileUpload file) {

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        validate((file.getFilename() == null || file.getFilename().equals("")), "Please select a valid file before uploading.");

        CheckIn checkIn = checkInServices.read(checkInID);
        validate(checkIn == null, "Unable to find checkin record with id %s", checkInID);
        if(!isAdmin) {
            validate((!currentUser.getId().equals(checkIn.getTeamMemberId()) && !currentUser.getId().equals(checkIn.getPdlId())), "You are not authorized to perform this operation");
            validate(checkIn.isCompleted(), "You are not authorized to perform this operation");
        }

        // create folder name in the format name-date
        String subjectName = memberProfileServices.getById(checkIn.getTeamMemberId()).getName();
        String sb = subjectName.concat(LocalDate.now().toString());
        sb = sb.replaceAll("\\s", "");
        final String directoryName = sb;

        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");

            // Check if folder already exists on google drive. If exists, return folderId and name
            FileList driveIndex = drive.files().list().setFields("files(id, name)").execute();
            validate(driveIndex.isEmpty(), "Error occurred while accessing Google Drive");

            File folderOnDrive = driveIndex.getFiles().stream()
                                    .filter(s -> directoryName.contains(s.getName()))
                                    .findFirst()
                                    .orElse(null);

            // If folder does not exist on Drive, create a new folder in the format name-date
            if(folderOnDrive == null) {
                folderOnDrive = createNewDirectoryOnDrive(drive, directoryName);
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

//            emailSender.sendEmail("New Check-in Notes", "New check-in notes have been uploaded. Please check the Google Drive folder.");

            return setFileInfo(uploadedFile, cd);
        } catch (GoogleJsonResponseException e) {
            LOG.error("Error occurred while accessing Google Drive.", e);
            throw new FileRetrievalException(e.getMessage());
        } catch (IOException e) {
            LOG.error("Unexpected error processing file upload.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    @Override
    public void deleteFile(@NotNull String uploadDocId) {

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        CheckinDocument cd = checkinDocumentServices.getFindByUploadDocId(uploadDocId);
        validate(cd == null, String.format("Unable to find record with id %s", uploadDocId));

        CheckIn associatedCheckin = checkInServices.read(cd.getCheckinsId());
        if(!isAdmin) {
            validate((!currentUser.getId().equals(associatedCheckin.getTeamMemberId()) && !currentUser.getId().equals(associatedCheckin.getPdlId())), "You are not authorized to perform this operation");
        }

        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");
            drive.files().delete(uploadDocId).execute();
            checkinDocumentServices.deleteByUploadDocId(uploadDocId);
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            throw new FileRetrievalException(e.getMessage());
        }
    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if(isError) {
            throw new FileRetrievalException(String.format(message, args));
        }
    }

    private File createNewDirectoryOnDrive(Drive drive, String directoryName) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(directoryName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        return drive.files().create(fileMetadata).execute();
    }

    private FileInfoDTO setFileInfo(@NotNull File file, @Nullable CheckinDocument cd) {
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
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
import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.CompletedFileUpload;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

@Singleton
public class FileServicesImpl extends FileServicesBaseImpl {

    private static final Logger LOG = LoggerFactory.getLogger(FileServicesImpl.class);

    private final GoogleApiAccess googleApiAccess;
    private final GoogleServiceConfiguration googleServiceConfiguration;

    public FileServicesImpl(GoogleApiAccess googleApiAccess,
                            CheckInServices checkInServices,
                            CheckinDocumentServices checkinDocumentServices,
                            MemberProfileServices memberProfileServices,
                            CurrentUserServices currentUserServices,
                            GoogleServiceConfiguration googleServiceConfiguration) {
        super(checkInServices, checkinDocumentServices, memberProfileServices,
              currentUserServices);
        this.googleApiAccess = googleApiAccess;
        this.googleServiceConfiguration = googleServiceConfiguration;
    }

    @Override
    protected void getCheckinDocuments(
                     Set<FileInfoDTO> result,
                     Set<CheckinDocument> checkinDocuments) throws IOException {
        Drive drive = googleApiAccess.getDrive();
        validate(drive == null, "Unable to access Google Drive");

        String rootDirId = googleServiceConfiguration.getDirectoryId();
        validate(rootDirId == null, "No destination folder has been configured. Contact your administrator for assistance.");

        if (checkinDocuments.isEmpty()) {
            FileList driveIndex = getFoldersInRoot(drive, rootDirId);
            driveIndex.getFiles().forEach(folder -> {
                try {
                    //find all
                    FileList fileList = drive.files().list().setSupportsAllDrives(true)
                        .setIncludeItemsFromAllDrives(true)
                        .setQ(String.format("'%s' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed != true", folder.getId()))
                        .setSpaces("drive")
                        .setFields("files(id, name, parents, size)")
                        .execute();
                    fileList.getFiles()
                        .forEach(file -> result.add(setFileInfo(file, null)));
                } catch (IOException ioe) {
                    LOG.error("Error occurred while retrieving files from Google Drive.", ioe);
                    throw new FileRetrievalException(ioe.getMessage());
                }
            });
        } else {
            for (CheckinDocument cd : checkinDocuments) {
                File file = drive.files().get(cd.getUploadDocId()).setSupportsAllDrives(true).execute();
                result.add(setFileInfo(file, cd));
            }
        }
    }

    @Override
    protected void downloadSingleFile(String docId, FileOutputStream myWriter) throws IOException {
        Drive drive = googleApiAccess.getDrive();
        validate(drive == null, "Unable to access Google Drive");

        drive.files().get(docId)
             .setSupportsAllDrives(true).executeMediaAndDownloadTo(myWriter);
        myWriter.close();
    }

    @Override
    protected FileInfoDTO uploadSingleFile(CompletedFileUpload file, String directoryName, Function<String, CheckinDocument> consumer) throws IOException {
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

            CheckinDocument cd =
                consumer.apply(uploadedFile.getId());
            return setFileInfo(uploadedFile, cd);
        } catch (GoogleJsonResponseException e) {
            LOG.error("Error occurred while accessing Google Drive.", e);
            throw new IOException(e.getMessage());
        }
    }

    /// Upload a Markdown document to the specified directory and copy it to
    /// a Google document, which results in an automatic conversion from
    /// Markdown to the Google Doc format.
    public FileInfoDTO uploadDocument(String directoryName, String name, String text) {
        final String GOOGLE_DOC_TYPE = "application/vnd.google-apps.document";
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        validate(!isAdmin, "You are not authorized to perform this operation");

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
            if (folderOnDrive == null) {
                folderOnDrive = createNewDirectoryOnDrive(drive, directoryName, rootDirId);
            }

            // Set the file metadata
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setMimeType(MediaType.TEXT_MARKDOWN_TYPE.toString());
            fileMetadata.setParents(Collections.singletonList(folderOnDrive.getId()));

            // Upload file to google drive
            InputStream is = new ByteArrayInputStream(
                    StandardCharsets.UTF_8.encode(text).array());
            InputStreamContent content = new InputStreamContent(
                    MediaType.TEXT_MARKDOWN_TYPE.toString(), is);
            File uploadedFile = drive.files().create(fileMetadata, content)
                                .setSupportsAllDrives(true)
                                .setFields("id, size, name")
                                .execute();

            // See if the Google doc already exists.  If it does, trash it.
            FileList fileList = drive.files().list()
                                .setSupportsAllDrives(true)
                                .setIncludeItemsFromAllDrives(true)
                                .setQ(String.format("'%s' in parents and mimeType = '%s' and trashed != true", folderOnDrive.getId(), GOOGLE_DOC_TYPE))
                                .setSpaces("drive")
                                .setFields("files(id, name, parents, size)")
                                .execute();
            for (File file : fileList.getFiles()) {
              if (file.getName().equals(name)) {
                try {
                  File trash = new File();
                  trash.setTrashed(true);
                  drive.files().update(file.getId(), trash)
                                  .setSupportsAllDrives(true)
                                  .execute();
                } catch (GoogleJsonResponseException e) {
                  LOG.error("Error while trashing " + file.getName(), e);
                } catch (IOException e) {
                  LOG.error("Error while trashing " + file.getName(), e);
                }
              }
            }

            // Copy the file to a Google doc
            File docFile = new File();
            docFile.setName(name);
            docFile.setMimeType(GOOGLE_DOC_TYPE);
            docFile.setParents(Collections.singletonList(folderOnDrive.getId()));
            File copiedFile = drive.files().copy(uploadedFile.getId(), docFile)
                                .setSupportsAllDrives(true)
                                .setFields("id, size, name")
                                .execute();

            // Delete the original mark-down file after copying it.
            File trash = new File();
            trash.setTrashed(true);
            drive.files().update(uploadedFile.getId(), trash)
                                .setSupportsAllDrives(true)
                                .execute();

            return setFileInfo(copiedFile, null);
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
    protected void deleteSingleFile(String docId) throws IOException {
        Drive drive = googleApiAccess.getDrive();
        validate(drive == null, "Unable to access Google Drive");
        File file = new File().setTrashed(true);
        drive.files().update(docId, file).setSupportsAllDrives(true).execute();
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

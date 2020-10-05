package com.objectcomputing.checkins.services.file;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
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
import java.util.*;

@Singleton
public class FileServicesImpl implements FileServices {

    private static final Logger LOG = LoggerFactory.getLogger(FileServicesImpl.class);

    private final GoogleDriveAccessor googleDriveAccessor;
    private final EmailSender emailSender;
    private final SecurityService securityService;
    private final CheckInServices checkInServices;
    private final CheckinDocumentServices checkinDocumentServices;
    private final MemberProfileServices memberProfileServices;

    public FileServicesImpl(GoogleDriveAccessor googleDriveAccessor, EmailSender emailSender,
                            SecurityService securityService, CheckInServices checkInServices,
                            CheckinDocumentServices checkinDocumentServices,
                            MemberProfileServices memberProfileServices) {
        this.googleDriveAccessor = googleDriveAccessor;
        this.emailSender = emailSender;
        this.securityService = securityService;
        this.checkInServices = checkInServices;
        this.checkinDocumentServices = checkinDocumentServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public HttpResponse<?> findFiles(@Nullable UUID checkInID) {

        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        try {
            Set<FileInfoDTO> result = new HashSet<>();
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");
            validate(checkInID == null && !isAdmin, "You are not authorized to perform this operation");

            if (checkInID == null && isAdmin) {
                //find all
                FileList fileList = drive.files().list().execute();
                for (File file : fileList.getFiles()) {
                    result.add(setFileInfo(file, null));
                }
            } else if (checkInID != null) {
                //find by CheckIn ID
                CheckIn checkIn = checkInServices.read(checkInID);
                validate(checkIn == null, "Unable to find checkin record with id %s", checkInID);

                Set<CheckinDocument> checkinDocuments = checkinDocumentServices.read(checkInID);
                for (CheckinDocument cd : checkinDocuments) {
                    File file = drive.files().get(cd.getUploadDocId()).execute();
                    result.add(setFileInfo(file, cd));
                }
            }

            return HttpResponse.ok(result);
        } catch (GoogleJsonResponseException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse
                    .status(HttpStatus.valueOf(e.getStatusCode()))
                    .body(e.getContent());
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public HttpResponse<?> downloadFiles(@NotNull String uploadDocId) {

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

            return HttpResponse.status(HttpStatus.OK).body(file);
        }  catch (HttpResponseException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse
                    .status(HttpStatus.valueOf(e.getStatusCode()))
                    .body(e.getContent());
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public HttpResponse<FileInfoDTO> uploadFile(@NotNull UUID checkInID, @NotNull CompletedFileUpload file) {

        CheckIn checkIn = checkInServices.read(checkInID);
        validate(checkIn == null, "Unable to find checkin record with id %s", checkInID);
        validate((file.getFilename() == null || file.getFilename().equals("")), "Please select a file before uploading.");

        // create folder name in the format name-date
        String subjectName = memberProfileServices.getById(checkIn.getTeamMemberId()).getName();
        String sb = subjectName.concat(LocalDate.now().toString());
        sb = sb.replaceAll("\\s", "");
        final String directoryName = sb;

        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");

            /* check if folder already exists on google drive
            * if exists, return folderId and name
            * else, create a new folder in the format name-date */
            FileList driveIndex = drive.files().list().setFields("files(id, name)").execute();
            File folderOnDrive = driveIndex.getFiles().stream()
                                    .filter(s -> directoryName.contains(s.getName()))
                                    .findFirst()
                                    .orElse(createNewDirectoryOnDrive(drive, directoryName));

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

            emailSender.sendEmail("New Check-in Notes", "New check-in notes have been uploaded. Please check the Google Drive folder.");

            return HttpResponse
                    .status(HttpStatus.OK)
                    .body(setFileInfo(uploadedFile, cd));
        } catch (IOException e) {
            LOG.error("Unexpected error processing file upload.", e);
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public HttpResponse<?> deleteFile(@NotNull String uploadDocId) {

        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");
            drive.files().delete(uploadDocId).execute();
            checkinDocumentServices.deleteByUploadDocId(uploadDocId);
        } catch (GoogleJsonResponseException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse
                    .status(HttpStatus.valueOf(e.getStatusCode()))
                    .body(e.getContent());
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return HttpResponse.ok();
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
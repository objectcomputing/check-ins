package com.objectcomputing.checkins.services.file;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.Drive.Files;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.utils.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@Singleton
public class FileServicesImpl implements FileServices {

    private static final Logger LOG = LoggerFactory.getLogger(FileServicesImpl.class);

    private String googleCredentials;
    private GoogleDriveAccessor googleDriveAccessor;
    private EmailSender emailSender;
    private SecurityService securityService;
    private CurrentUserServices currentUserServices;
    private CheckInServices checkInServices;
    private CheckinDocumentServices checkinDocumentServices;
    private MemberProfileServices memberProfileServices;

    public FileServicesImpl(GoogleDriveAccessor googleDriveAccessor, EmailSender emailSender,
                            SecurityService securityService, CurrentUserServices currentUserServices,
                            CheckInServices checkInServices, CheckinDocumentServices checkinDocumentServices,
                            MemberProfileServices memberProfileServices,
                            @Property(name = "google.credentials") String googleCredentials) {
        this.googleDriveAccessor = googleDriveAccessor;
        this.emailSender = emailSender;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
        this.checkInServices = checkInServices;
        this.checkinDocumentServices = checkinDocumentServices;
        this.memberProfileServices = memberProfileServices;
        this.googleCredentials = googleCredentials;
    }

    @Override
    public HttpResponse<Set<FileInfoDTO>> findFiles(@Nullable UUID checkInID) {

        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        Set<FileInfoDTO> result = new HashSet<>();

        try {
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
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.serverError();
        }

        return HttpResponse.ok(result);
    }

    @Override
    public HttpResponse<OutputStream> downloadFiles(@NotNull String uploadDocId) {

        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");
            drive.files().export(uploadDocId.toString(), "application/pdf").executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.serverError();
        }

        System.out.println("outputstream = " + outputStream);
        return HttpResponse.ok(outputStream);
    }

    @Override
    public HttpResponse<FileInfoDTO> uploadFile(@NotNull UUID checkInID, @NotNull CompletedFileUpload file) {

        FileInfoDTO result;
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

            //check if folder already exists on google drive
            //if exists, return folderId and name
            FileList driveIndex = drive.files().list().setFields("files(id, name)").execute();
            File folderOnDrive = driveIndex.getFiles().stream()
                                    .filter(s -> directoryName.contains(s.getName()))
                                    .findFirst()
                                    .orElse(null);

            // set file metadata
            File fileMetadata = new File();
            fileMetadata.setName(file.getFilename());
            fileMetadata.setMimeType(file.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE).toString());

            if(folderOnDrive != null) {
                //Directory exists on Google Drive
                System.out.println("Folder exists on drive");
                fileMetadata.setParents(Arrays.asList(folderOnDrive.getId()));
            } else {
                //Directory does not exist on Google Drive - create a new directory
                System.out.println("Folder does not exist on drive");
                String folderId = createNewDirectoryOnDrive(drive, directoryName);
                fileMetadata.setParents(Collections.singletonList(folderId));
            }

            //upload file to google drive
            InputStreamContent content = new InputStreamContent(file.getContentType().toString(), file.getInputStream());
            File uploadedFile = drive.files().create(fileMetadata, content)
                                .setSupportsAllDrives(true)
                                .setFields("id, size, name")
                                .execute();

            //create record in checkin-document service
            CheckinDocument cd = new CheckinDocument(checkInID, uploadedFile.getId());
            checkinDocumentServices.save(cd);

            result = setFileInfo(uploadedFile, cd);

//            emailSender.sendEmail("New Check-in Notes", "New check-in notes have been uploaded. Please check the Google Drive folder.");
        } catch (final IOException e) {
            LOG.error("Unexpected error processing file upload.", e);
            return HttpResponse.badRequest();
        }

        return HttpResponse.ok(result);
    }

    @Override
    public HttpResponse deleteFile(@NotNull String uploadDocId) {

        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");
            drive.files().delete(uploadDocId).execute();
            checkinDocumentServices.deleteByUploadDocId(uploadDocId);
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.serverError();
        }

        return HttpResponse.ok();
    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if(isError) {
            throw new FileRetrievalException(String.format(message, args));
        }
    }

    private String createNewDirectoryOnDrive(Drive drive, String directoryName) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(directoryName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File folder = drive.files().create(fileMetadata).execute();
        System.out.println("created new directory on drive = " + folder.getId());
        System.out.println("created new directory on drive = " + folder.getName());
        return folder.getId();
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
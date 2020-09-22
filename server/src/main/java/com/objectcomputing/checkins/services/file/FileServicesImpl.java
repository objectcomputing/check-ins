package com.objectcomputing.checkins.services.file;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
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
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        Set<FileInfoDTO> result = new HashSet<>();

        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");
            validate(checkInID == null && !isAdmin, "You are not authorized to perform this operation");

            if (checkInID.equals(null) && isAdmin) {
                //find all
                FileList fileList = drive.files().list().execute();
                for (File file : fileList.getFiles()) {
                    FileInfoDTO dto = new FileInfoDTO();
                    dto.setFileId(file.getId());
                    dto.setName(file.getName());
                    dto.setSize(file.getSize());
                    result.add(dto);
                }
            } else if (checkInID != null) {
                CheckIn checkIn = checkInServices.read(checkInID);
                validate(checkIn == null, "Unable to find checkin record with id %s", checkInID);
                validate((!isAdmin &&
                        !currentUser.getId().equals(checkIn.getTeamMemberId()) &&
                        !currentUser.getId().equals(checkIn.getPdlId()) &&
                        !currentUser.getId().equals(memberProfileServices.getById(checkIn.getTeamMemberId()).getPdlId())),
                        "You are not authorized to perform this operation");

                //find by CheckIn ID
                Set<CheckinDocument> checkinDocuments = checkinDocumentServices.read(checkInID);
                for (CheckinDocument cd : checkinDocuments) {
                    File file = drive.files().get(cd.getUploadDocId()).execute();
                    FileInfoDTO dto = new FileInfoDTO();
                    dto.setFileId(file.getId());
                    dto.setCheckInId(cd.getCheckinsId());
                    dto.setName(file.getName());
                    dto.setSize(file.getSize());
                    result.add(dto);
                }
            }
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.serverError();
        }

        return HttpResponse.ok(result);
    }

    @Override
    public HttpResponse<OutputStream> downloadFiles(@NotNull UUID uploadDocId) {

        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();
            validate(drive == null, "Unable to access Google Drive");
            drive.files().export(uploadDocId.toString(), "application/pdf").executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.serverError();
        }

        return HttpResponse.ok(outputStream);
    }

    @Override
    public HttpResponse<?> uploadFile(@NotNull UUID checkInID, @NotNull CompletedFileUpload file) {

        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        CheckIn checkIn = checkInServices.read(checkInID);
        validate(checkIn == null, "Unable to find checkin record with id %s", checkInID);
        validate((file.getFilename() == null || file.getFilename().equals("")), "Please select a file before uploading.");

        MemberProfile teamMember = memberProfileServices.getById(checkIn.getTeamMemberId());
        String subjectName = teamMember.getName();
        String fileId = String.format(file.getFilename(), LocalDate.now());
        String folderId;
        String directoryName = String.format(subjectName, LocalDate.now());
        validate((!isAdmin &&
                        !currentUser.getId().equals(checkIn.getTeamMemberId()) &&
                        !currentUser.getId().equals(teamMember.getPdlId())),
                "You are not authorized to perform this operation");

        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();

            validate(drive == null, "Unable to access Google Drive");
            validate(googleCredentials == null, "Configuration error, please contact admin");

            final File fileMetadata = new File();
            fileMetadata.setName(file.getFilename());
            fileMetadata.setMimeType(file.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE).toString());
            fileMetadata.setId(fileId);

            Drive.Files.List driveIndex = drive.files().list();
            if(driveIndex.containsValue(directoryName) || driveIndex.containsKey(directoryName)) {
                //Directory exists on Google Drive
                fileMetadata.setParents(Arrays.asList(directoryName));
            } else {
                //Directory does not exist on Google Drive - create a new directory
                folderId = createNewDirectoryOnDrive(drive, directoryName);
                fileMetadata.setParents(Arrays.asList(folderId));
            }

            InputStreamContent content = new InputStreamContent(fileMetadata.getMimeType(), file.getInputStream());
            drive.files().create(fileMetadata, content).setSupportsAllDrives(true).setFields("parents").execute();

            CheckinDocument cd = new CheckinDocument(checkInID, fileId);
            checkinDocumentServices.save(cd);

            emailSender.sendEmail("New Check-in Notes",
                    "New check-in notes have been uploaded. Please check the Google Drive folder.");
        } catch (final IOException e) {
            LOG.error("Unexpected error processing file upload.", e);
            return HttpResponse.badRequest(String.format("Unexpected error processing %s", file.getFilename()));
        }

        return HttpResponse.ok(String.format("The file %s was uploaded", file.getFilename()));
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
        return folder.getId();
    }
}
package com.objectcomputing.checkins.services.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.objectcomputing.checkins.UploadController;
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
import io.micronaut.core.util.CollectionUtils;
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
import java.util.*;

@Singleton
public class FileServicesImpl implements FileServices {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

//    private static final String DIRECTORY_KEY = "upload-directory-id";
//    private static final String DIRECTORY_FILE_PATH = "/secrets/directory.json";
    private static final String RSP_SERVER_ERROR_KEY = "serverError";
    private static final String RSP_COMPLETE_MESSAGE_KEY = "completeMessage";


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

    // - fileId, checkinId and file name, file size - dto
    @Override
    public HttpResponse<Set<File>> findFiles(@Nullable UUID checkInID) {
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        CheckIn checkIn = checkInServices.read(checkInID);
        Set<File> result = new HashSet<File>();

        if(checkIn == null) {
            throw new FileRetrievalException(String.format("Unable to find checkin record with id %s", checkIn.getId()));
        } else if(checkInID.equals(null) && !isAdmin) {
            throw new FileRetrievalException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
        } else if(!isAdmin &&
                !currentUser.getId().equals(checkIn.getTeamMemberId()) &&
                !currentUser.getId().equals(checkIn.getPdlId()) &&
                !currentUser.getId().equals(memberProfileServices.getById(checkIn.getTeamMemberId()).getPdlId())) {
            //Current user is not associated with checkIn
            throw new FileRetrievalException(String.format("Member %s is unauthorized to do this operation", currentUser.getId()));
        }

        try {
            Drive drive = googleDriveAccessor.accessGoogleDrive();

            if(drive == null) {
                throw new FileRetrievalException("Unable to access Google Drive");
            } else if(checkInID.equals(null)) {
                //find all
                FileList files = drive.files().list().execute();
                result.addAll(files.getFiles());
            } else {
                //find by CheckIn ID
                Set<CheckinDocument> checkinDocuments = checkinDocumentServices.read(checkInID);
                for(CheckinDocument cd : checkinDocuments) {
                    result.add(drive.files().get(cd.getUploadDocId()).execute());
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

            if(drive == null) {
                throw new FileRetrievalException("Unable to access Google Drive");
            } else {
                drive.files().export(uploadDocId.toString(), "application/pdf")
                        .executeMediaAndDownloadTo(outputStream);
            }
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Drive.", e);
            return HttpResponse.serverError();
        }

        return HttpResponse.ok(outputStream);
    }

    @Override
    public HttpResponse<?> uploadFile(@NotNull CompletedFileUpload file) {

        Drive drive = null;

        try {
            drive = googleDriveAccessor.accessGoogleDrive();
        } catch (final IOException e) {
            LOG.error("Error occurred while initializing Google Drive.", e);
        }

        if (drive == null) {
            return HttpResponse
                    .serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY, "Unable to access Google Drive"));
        }

        if ((file.getFilename() == null || file.getFilename().equals(""))) {
            return HttpResponse
                    .badRequest(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY, "Please select a file before uploading."));
        }

        JsonNode dirNode = null;
        try {
            dirNode = new ObjectMapper().readTree(this.getClass().getResourceAsStream(DIRECTORY_FILE_PATH));
        } catch (final IOException e) {
            return HttpResponse.serverError(
                    CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY, "Configuration error, please contact admin"));
        }

        final String parentId = dirNode.get(DIRECTORY_KEY) != null ? dirNode.get(DIRECTORY_KEY).asText() : null;
        if (parentId == null) {
            return HttpResponse.serverError(
                    CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY, "Configuration error, please contact admin"));
        }

        final File fileMetadata = new File();
        fileMetadata.setName(file.getFilename());
        fileMetadata.setMimeType(file.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE).toString());
        fileMetadata.setParents(Arrays.asList(parentId));

        InputStreamContent content;
        try {
            content = new InputStreamContent(fileMetadata.getMimeType(), file.getInputStream());
        } catch (final IOException e) {
            LOG.error("Unexpected error processing file upload.", e);
            return HttpResponse.badRequest(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    String.format("Unexpected error processing %s", file.getFilename())));
        }

        try {
            drive.files().create(fileMetadata, content).setSupportsAllDrives(true).setFields("parents").execute();

            emailSender.sendEmail("New Check-in Notes",
                    "New check-in notes have been uploaded by a PDL. Please check the Google Drive folder.");
        } catch (final IOException e) {
            LOG.error("Unexpected error uploading file to Google Drive.", e);
            return HttpResponse
                    .serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY, "Unable to upload file to Google Drive"));
        }

        return HttpResponse.ok(CollectionUtils.mapOf(RSP_COMPLETE_MESSAGE_KEY,
                String.format("The file %s was uploaded", file.getFilename())));
    }
}

package com.objectcomputing.checkins;

import java.io.IOException;
import java.util.Arrays;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.objectcomputing.checkins.notifications.email.EmailSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.validation.Validated;
import io.micronaut.views.View;

@Validated
@Controller("upload")
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    private static final String DIRECTORY_KEY = "upload-directory-id";
    private static final String DIRECTORY_FILE_PATH = "/secrets/directory.json";
    private static final String RSP_SERVER_ERROR_KEY = "serverError";
    private static final String RSP_COMPLETE_MESSAGE_KEY = "completeMessage";

    @NotNull
    private final GoogleDriveAccessor googleDriveAccessor;

    @NotNull
    private final EmailSender emailSender;

    /**
     * UploadController
     * @param googleDriveAccessor
     * @throws IOException
     */
    public UploadController(final GoogleDriveAccessor googleDriveAccessor, final EmailSender emailSender)
            throws IOException {
        this.googleDriveAccessor = googleDriveAccessor;
        this.emailSender = emailSender;
    }

    /**
     * Simple form to upload a file to Google Drive
     * 
     * @return HttpResponse
     */
    @View("upload")
    @Get
    public HttpResponse<?> upload() {
        return HttpResponse.ok();
    }

    /**
     * Takes in a file to upload to Google Drive
     * 
     * @param file, the file to upload to Google Drive
     * @return HttpResponse
     */
    @View("upload")
    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> upload(@Body final CompletedFileUpload file) {
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
            return HttpResponse.badRequest(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY, "Please select a file before uploading."));
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

            emailSender.sendEmail("New Check-in Notes", "New check-in notes have been uploaded by a PDL. Please check the Google Drive folder.");
        } catch (final IOException e) {
            LOG.error("Unexpected error uploading file to Google Drive.", e);
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Unable to upload file to Google Drive"));
        }

        return HttpResponse.ok(CollectionUtils.mapOf(RSP_COMPLETE_MESSAGE_KEY,
                String.format("The file %s was uploaded", file.getFilename())));
    }
}

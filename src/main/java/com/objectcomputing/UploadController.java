package com.objectcomputing;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import io.micronaut.context.annotation.Property;
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

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Validated
@Controller("upload")
public class UploadController {
    private static final String DIRECTORY_FILE_PATH = "/secrets/directory.json";
    private static final String RSP_SERVER_ERROR_KEY = "serverError";
    private static final String RSP_ERROR_KEY = "error";
    private static final String RSP_COMPLETE_MESSAGE_KEY = "completeMessage";

    @Property(name = "oci-google-drive.application.dir-key")
    private String directoryKey;

    @NotNull
    private GoogleDriveAccessor googleDriveAccessor;

    /**
     * UploadController
     * @param googleDriveAccessor
     * @throws IOException
     */
    public UploadController(GoogleDriveAccessor googleDriveAccessor) throws IOException {
        this.googleDriveAccessor = googleDriveAccessor;
    }

    /**
     * Simple form to upload a file to Google Drive
     * @return HttpResponse
     */
    @View("upload")
    @Get
    public HttpResponse upload() {
        return HttpResponse.ok();
    }

    /**
     * Takes in a file to upload to Google Drive
     * @param file, the file to upload to Google Drive
     * @return HttpResponse
     */
    @View("upload")
    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse upload(@Body CompletedFileUpload file) {
        Drive drive = null;
        try {
            drive = googleDriveAccessor.accessGoogleDrive();
        } catch (IOException e) {
            // Catch down below
        }

        if(drive == null) {
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Unable to access Google Drive"));
        }

        if ((file.getFilename() == null || file.getFilename().equals(""))) {
            return HttpResponse.badRequest(CollectionUtils.mapOf(RSP_ERROR_KEY, "Required file"));
        }

        if(directoryKey == null) {
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Directory key error, please contact admin"));
        }

        JsonNode dirNode = null;
        try {
            dirNode = new ObjectMapper().readTree(this.getClass().getResourceAsStream(DIRECTORY_FILE_PATH));
        } catch (IOException e) {
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Configuration error, please contact admin"));
        }

        String parentId = dirNode.get(directoryKey) != null ? dirNode.get(directoryKey).asText() : null;
        if(parentId == null) {
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Configuration error, please contact admin"));
        }

        File fileMetadata = new File();
        fileMetadata.setName(file.getFilename());
        fileMetadata.setMimeType(file.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE).toString());
        fileMetadata.setParents(Arrays.asList(parentId));

        InputStreamContent content;
        try {
            content = new InputStreamContent(fileMetadata.getMimeType(), file.getInputStream());
        } catch (IOException e) {
            return HttpResponse.badRequest(CollectionUtils.mapOf(RSP_ERROR_KEY,
                    String.format("Unexpected error processing %s", file.getFilename())));
        }

        try {
            drive.files().create(fileMetadata, content).setFields("parents").execute();
        } catch (IOException e) {
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Unable to upload file to Google Drive"));
        }

        return HttpResponse.ok(CollectionUtils.mapOf(RSP_COMPLETE_MESSAGE_KEY,
                String.format("The file %s was uploaded", file.getFilename())));
    }
}

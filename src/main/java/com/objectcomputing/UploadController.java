package com.objectcomputing;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.views.View;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Controller("upload")
public class UploadController {

    private static final String APPLICATION_NAME = "OCI Google Drive Upload";

    private static final String RSP_SERVER_ERROR_KEY = "serverError";
    private static final String RSP_ERROR_KEY = "error";
    private static final String RSP_COMPLETE_MESSAGE_KEY = "completeMessage";

    protected static Drive drive;
    protected static GoogleDriveUtil googleDriveUtil;

    /**
     * Get access to the designated Google Drive set in your secrets/credentials.json
     * @return true if access was successful or false if it wasn't
     */
    private boolean accessGoogleDrive() {
        boolean isConnected = true;
        if(drive == null) {
            try {
                if(googleDriveUtil == null) {
                    googleDriveUtil = new GoogleDriveUtil(GoogleNetHttpTransport.newTrustedTransport(),
                            APPLICATION_NAME,
                            List.of(DriveScopes.DRIVE_FILE));

                }
                drive = googleDriveUtil.accessGoogleDrive();
            } catch (IOException | GeneralSecurityException e) {
                isConnected = false;
            }
        }
        return isConnected && drive != null;
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
    public HttpResponse upload(CompletedFileUpload file) {

        if ((file.getFilename() == null || file.getFilename().equals(""))) {
            return HttpResponse.badRequest(CollectionUtils.mapOf(RSP_ERROR_KEY, "Required file"));
        }

        if(!accessGoogleDrive()) {
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Unable to access Google Drive"));
        }

        File fileMetadata = new File();
        fileMetadata.setName(file.getFilename());
        fileMetadata.setMimeType(file.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE).toString());

        InputStreamContent content;
        try {
            content = new InputStreamContent(fileMetadata.getMimeType(), file.getInputStream());
        } catch (IOException e) {
            return HttpResponse.badRequest(CollectionUtils.mapOf(RSP_ERROR_KEY,
                    String.format("Unexpected error processing %s", file.getFilename())));
        }

        try {
            drive.files().create(fileMetadata, content).execute();
        } catch (IOException e) {
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Unable to upload file to Google Drive"));
        }

        return HttpResponse.ok(CollectionUtils.mapOf(RSP_COMPLETE_MESSAGE_KEY,
                String.format("The file %s was uploaded", file.getFilename())));
    }
}

package com.objectcomputing;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.validation.Validated;
import io.micronaut.views.View;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Validated
@Controller("upload")
public class UploadController {
    private static final String RSP_SERVER_ERROR_KEY = "serverError";
    private static final String RSP_ERROR_KEY = "error";
    private static final String RSP_COMPLETE_MESSAGE_KEY = "completeMessage";

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
    public HttpResponse upload(CompletedFileUpload file) throws IOException {
        Drive drive = googleDriveAccessor.accessGoogleDrive();
        if(drive == null) {
            return HttpResponse.serverError(CollectionUtils.mapOf(RSP_SERVER_ERROR_KEY,
                    "Unable to access Google Drive"));
        }

        if ((file.getFilename() == null || file.getFilename().equals(""))) {
            return HttpResponse.badRequest(CollectionUtils.mapOf(RSP_ERROR_KEY, "Required file"));
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

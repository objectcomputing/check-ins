package com.objectcomputing.checkins.services.fileupload;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.google.api.services.drive.model.File;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

@Validated
@Controller("/file")
@Tag(name = "file")
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
@Singleton
public class FileUploadController {

    private FileUploadServices fileUploadServices;

    public FileUploadController(FileUploadServices fileUploadServices) {
        this.fileUploadServices = fileUploadServices;
    }

    /**
     * Retrieve documents associated with CheckIn Id or find all
     * @param {id}
     * @return {@link HttpResponse<Set<CompletedFileUpload>>} Returns a set of files associated with CheckInId or all files
     */
    @Get("/{id}")
    public HttpResponse<Set<File>> findDocuments(@Nullable UUID checkInId) {
        return fileUploadServices.findFiles(checkInId);
    }

    /**
     * Retrieve documents contents associated with UploadDocId
     * @param {id}
     * @return {@link HttpResponse<Set<CompletedFileUpload>>} Returns a set of files associated with CheckInId or all files
     */
    @Get("/{id}")
    public HttpResponse<OutputStream> downloadDocument(@NotNull UUID uploadDocId) {
        return fileUploadServices.downloadFiles(uploadDocId);
    }

    /**
     * Takes in a file to upload to Google Drive
     *
     * @param file, the file to upload to Google Drive
     * @return HttpResponse
     */
    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> upload(@Body final CompletedFileUpload file) {
        return fileUploadServices.uploadFile(file);
    }
}

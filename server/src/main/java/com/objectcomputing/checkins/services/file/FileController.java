package com.objectcomputing.checkins.services.file;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.File;
import java.util.Set;
import java.util.UUID;

@Validated
@Controller("/services/file")
@Tag(name = "file")
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
public class FileController {

    private final FileServices fileServices;

    public FileController(FileServices fileServices) {
        this.fileServices = fileServices;
    }

    @Error(exception = FileRetrievalException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, FileRetrievalException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Retrieve metadata of documents associated with CheckIn Id or find all from Google Drive
     *
     * @param {id}
     * @return {@link HttpResponse<Set<FileInfoDTO>>} Returns a set of FileInfoDTO associated with CheckInId or all files
     */
    @Get("{?id}")
    public HttpResponse<Set<FileInfoDTO>> findDocuments(@Nullable UUID id) {
        Set<FileInfoDTO> filesFromDrive = fileServices.findFiles(id);
        return HttpResponse
                .status(HttpStatus.OK)
                .body(filesFromDrive);
    }

    /**
     * Download document associated with UploadDocId from Google Drive
     *
     * @param {uploadDocId}, the fileId of the file to be deleted
     * @return {@link HttpResponse<java.io.File>} Returns file
     */
    @Get("/{uploadDocId}/download")
    public HttpResponse<File> downloadDocument(@NotNull String uploadDocId) {
        File fileFromDrive = fileServices.downloadFiles(uploadDocId);
        return HttpResponse
                .status(HttpStatus.OK)
                .body(fileFromDrive);
    }

    /**
     * Takes in a file to upload to Google Drive
     *
     * @param file, the file to upload to Google Drive
     * @return {@link HttpResponse<FileInfoDTO>} Returns metadata of document uploaded to Google Drive
     */
    @Post(uri = "/{checkInId}", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<FileInfoDTO> upload(@NotNull UUID checkInId, @Body CompletedFileUpload file) {
        FileInfoDTO uploadedFile = fileServices.uploadFile(checkInId, file);
        return HttpResponse
                .status(HttpStatus.CREATED)
                .body(uploadedFile);
    }

    /**
     * Delete a document from Google Drive
     *
     * @param {uploadDocId}, the uploadDocId of the document
     * @return HttpResponse
     */
    @Delete("/{uploadDocId}")
    public HttpResponse delete(@NotNull String uploadDocId) {
        fileServices.deleteFile(uploadDocId);
        return HttpResponse
                .status(HttpStatus.OK);
    }
}

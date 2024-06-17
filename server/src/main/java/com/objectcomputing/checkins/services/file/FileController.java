package com.objectcomputing.checkins.services.file;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

import java.io.File;
import java.util.Set;
import java.util.UUID;

@Validated
@Controller("/services/files")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "files")
@Secured(SecurityRule.IS_AUTHENTICATED)
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
    public Set<FileInfoDTO> findDocuments(@Nullable UUID id) {
        return fileServices.findFiles(id);
    }

    /**
     * Download document associated with UploadDocId from Google Drive
     *
     * @param {uploadDocId}, the fileId of the file to be deleted
     * @return {@link HttpResponse<java.io.File>} Returns file
     */
    @Get("/{uploadDocId}/download")
    public File downloadDocument(@NotNull String uploadDocId) {
        return fileServices.downloadFiles(uploadDocId);
    }

    /**
     * Takes in a file to upload to Google Drive
     *
     * @param file, the file to upload to Google Drive
     * @return {@link HttpResponse<FileInfoDTO>} Returns metadata of document uploaded to Google Drive
     */
    @Post(uri = "/{checkInId}", consumes = MediaType.MULTIPART_FORM_DATA)
    @Status(HttpStatus.CREATED)
    public FileInfoDTO upload(@NotNull UUID checkInId, CompletedFileUpload file) {
        return fileServices.uploadFile(checkInId, file);
    }

    /**
     * Delete a document from Google Drive
     *
     * @param {uploadDocId}, the uploadDocId of the document
     * @return HttpResponse
     */
    @Delete("/{uploadDocId}")
    public boolean delete(@NotNull String uploadDocId) {
        return fileServices.deleteFile(uploadDocId);
    }
}

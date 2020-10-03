package com.objectcomputing.checkins.services.file;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

@Validated
@Controller("/services/file")
@Tag(name = "file")
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
public class FileController {

    private FileServices fileServices;

    public FileController(FileServices fileServices) {
        this.fileServices = fileServices;
    }

    /**
     * Retrieve documents associated with CheckIn Id or find all from Google Drive
     *
     * @param {id}
     * @return {@link HttpResponse<Set<FileInfoDTO>>} Returns a set of FileInfoDTO associated with CheckInId or all files
     */
    @Get("/{?id}")
    public HttpResponse<?> findDocuments(@Nullable UUID id) {
        return fileServices.findFiles(id);
    }

    /**
     * Download document associated with UploadDocId from Google Drive
     *
     * @param {uploadDocId}, the fileId of the file to be deleted
     * @return {@link HttpResponse<OutputStream>} Returns OutputStream of document
     */
    @Get("/{uploadDocId}/download")
    public HttpResponse<OutputStream> downloadDocument(@NotNull String uploadDocId) {
        return fileServices.downloadFiles(uploadDocId);
    }

    /**
     * Takes in a file to upload to Google Drive
     *
     * @param file, the file to upload to Google Drive
     * @return HttpResponse
     */
    @Post(uri = "/{checkInId}", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<FileInfoDTO> upload(@NotNull UUID checkInId, @Body CompletedFileUpload file) {
        return fileServices.uploadFile(checkInId, file);
    }

    /**
     * Delete a document from Google Drive
     *
     * @param {uploadDocId}, the uploadDocId of the document
     * @return HttpResponse
     */
    @Delete("/{uploadDocId}")
    public HttpResponse<?> delete(@NotNull String uploadDocId) {
        return fileServices.deleteFile(uploadDocId);
    }
}

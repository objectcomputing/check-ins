package com.objectcomputing.checkins.services.file;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Validated
@Controller("/services/file")
@Tag(name = "file")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class FileController {

    private final FileServices fileServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public FileController(FileServices fileServices,
                          EventLoopGroup eventLoopGroup,
                          @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.fileServices = fileServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
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
    public Single<HttpResponse<Set<FileInfoDTO>>> findDocuments(@Nullable UUID id) {

        return Single.fromCallable(() -> fileServices.findFiles(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(fileInfo -> {
                    return (HttpResponse<Set<FileInfoDTO>>)HttpResponse.ok(fileInfo);
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Download document associated with UploadDocId from Google Drive
     *
     * @param {uploadDocId}, the fileId of the file to be deleted
     * @return {@link HttpResponse<java.io.File>} Returns file
     */
    @Get("/{uploadDocId}/download")
    public Single<HttpResponse<File>> downloadDocument(@NotNull String uploadDocId) {
        return Single.fromCallable(() -> fileServices.downloadFiles(uploadDocId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(file -> (HttpResponse<File>) HttpResponse.ok(file))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Takes in a file to upload to Google Drive
     *
     * @param file, the file to upload to Google Drive
     * @return {@link HttpResponse<FileInfoDTO>} Returns metadata of document uploaded to Google Drive
     */
    @Post(uri = "/{checkInId}", consumes = MediaType.MULTIPART_FORM_DATA)
    public Single<HttpResponse<FileInfoDTO>> upload(@NotNull UUID checkInId, @Body CompletedFileUpload file) {
        return Single.fromCallable(() -> fileServices.uploadFile(checkInId, file))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(fileInfo -> (HttpResponse<FileInfoDTO>) HttpResponse.created(fileInfo))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Delete a document from Google Drive
     *
     * @param {uploadDocId}, the uploadDocId of the document
     * @return HttpResponse
     */
    @Delete("/{uploadDocId}")
    public Single<HttpResponse> delete(@NotNull String uploadDocId) {
        return Single.fromCallable(() -> fileServices.deleteFile(uploadDocId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(successFlag -> (HttpResponse)HttpResponse.ok())
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}

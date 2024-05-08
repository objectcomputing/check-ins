package com.objectcomputing.checkins.services.file;

import io.micronaut.core.annotation.Nullable;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Validated
@Controller("/services/files")
@Tag(name = "files")
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
    public Mono<HttpResponse<Set<FileInfoDTO>>> findDocuments(@Nullable UUID id) {

        return Mono.fromCallable(() -> fileServices.findFiles(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(fileInfo -> {
                    return (HttpResponse<Set<FileInfoDTO>>)HttpResponse.ok(fileInfo);
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Download document associated with UploadDocId from Google Drive
     *
     * @param {uploadDocId}, the fileId of the file to be deleted
     * @return {@link HttpResponse<java.io.File>} Returns file
     */
    @Get("/{uploadDocId}/download")
    public Mono<HttpResponse<File>> downloadDocument(@NotNull String uploadDocId) {
        return Mono.fromCallable(() -> fileServices.downloadFiles(uploadDocId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(file -> (HttpResponse<File>) HttpResponse.ok(file))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Takes in a file to upload to Google Drive
     *
     * @param file, the file to upload to Google Drive
     * @return {@link HttpResponse<FileInfoDTO>} Returns metadata of document uploaded to Google Drive
     */
    @Post(uri = "/{checkInId}", consumes = MediaType.MULTIPART_FORM_DATA)
    public Mono<HttpResponse<FileInfoDTO>> upload(@NotNull UUID checkInId, CompletedFileUpload file) {
        return Mono.fromCallable(() -> fileServices.uploadFile(checkInId, file))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(fileInfo -> (HttpResponse<FileInfoDTO>) HttpResponse.created(fileInfo))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Delete a document from Google Drive
     *
     * @param {uploadDocId}, the uploadDocId of the document
     * @return HttpResponse
     */
    @Delete("/{uploadDocId}")
    public Mono<HttpResponse> delete(@NotNull String uploadDocId) {
        return Mono.fromCallable(() -> fileServices.deleteFile(uploadDocId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse)HttpResponse.ok())
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }
}

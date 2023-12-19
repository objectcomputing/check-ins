package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/checkin-documents")
@Secured({RoleType.Constants.ADMIN_ROLE, RoleType.Constants.PDL_ROLE})
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin documents")

public class CheckinDocumentController {
        
    private final CheckinDocumentServices checkinDocumentService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public CheckinDocumentController(CheckinDocumentServices checkinDocumentService,
                                    EventLoopGroup eventLoopGroup,
                                    @Named(TaskExecutors.IO) ExecutorService ioExecutorService){
        this.checkinDocumentService = checkinDocumentService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }


    /**
     * Find CheckinDocument(s) based on checkinsId
     *
     * @param checkinsId
     * @return {@link Set<CheckinDocument> Set of CheckinDocument(s) associated with the checkinsId}
     */

    @Get("/{?checkinsId}")
    @RequiredPermission(Permissions.CAN_VIEW_CHECKINS_ELEVATED)
    public Mono<HttpResponse<Set<CheckinDocument>>> findCheckinDocument(@Nullable UUID checkinsId) {
        return Mono.fromCallable(() -> checkinDocumentService.read(checkinsId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(checkinDocuments -> (HttpResponse<Set<CheckinDocument>>) HttpResponse.ok(checkinDocuments))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Create and save a new CheckinDocument.
     *
     * @param checkinDocument, {@link CheckinDocumentCreateDTO}
     * @return {@link HttpResponse<CheckinDocument>}
     */

    @Post()
    @RequiredPermission(Permissions.CAN_CREATE_CHECKINS_ELEVATED)
    public Mono<HttpResponse<CheckinDocument>> createCheckinDocument(@Body @Valid CheckinDocumentCreateDTO checkinDocument,
                                                                    HttpRequest<CheckinDocumentCreateDTO> request) {
        return Mono.fromCallable(() -> checkinDocumentService.save(new CheckinDocument(checkinDocument.getCheckinsId(),checkinDocument.getUploadDocId())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdCheckinDocument -> {return (HttpResponse<CheckinDocument>) HttpResponse
                    .created(createdCheckinDocument)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdCheckinDocument.getId()))));
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update a CheckinDocument
     *
     * @param checkinDocument, {@link CheckinDocument}
     * @return {@link HttpResponse<CheckinDocument>}
     */
    @Put()
    public Mono<HttpResponse<CheckinDocument>> update(@Body @Valid CheckinDocument checkinDocument,
                                            HttpRequest<CheckinDocument> request) {
        if (checkinDocument == null) {
            return Mono.just(HttpResponse.ok());
        }
        return Mono.fromCallable(() -> checkinDocumentService.update(checkinDocument))
            .publishOn(Schedulers.fromExecutor(eventLoopGroup))
            .map(updatedCheckinDocument -> (HttpResponse<CheckinDocument>) HttpResponse
                    .ok()
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedCheckinDocument.getId()))))
                    .body(updatedCheckinDocument))
            .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Delete a CheckinDocument
     *
     * @param checkinsId, id of the checkins record you wish to delete
     * @return {@link HttpResponse<>}
     */
    @Delete("/{checkinsId}")
    public HttpResponse<?> delete(UUID checkinsId) {
        checkinDocumentService.deleteByCheckinId(checkinsId);
        return HttpResponse
                .noContent();
    }
}
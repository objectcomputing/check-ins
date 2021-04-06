package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/checkin-document")
@Secured({RoleType.Constants.ADMIN_ROLE, RoleType.Constants.PDL_ROLE})
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin document")

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
    public Single<HttpResponse<Set<CheckinDocument>>> findCheckinDocument(@Nullable UUID checkinsId) {
        return Single.fromCallable(() -> checkinDocumentService.read(checkinsId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(checkinDocuments -> {return (HttpResponse<Set<CheckinDocument>>) HttpResponse.ok(checkinDocuments);
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Create and save a new CheckinDocument.
     *
     * @param checkinDocument, {@link CheckinDocumentCreateDTO}
     * @return {@link HttpResponse<CheckinDocument>}
     */

    @Post()
    public Single<HttpResponse<CheckinDocument>> createCheckinDocument(@Body @Valid CheckinDocumentCreateDTO checkinDocument,
                                                                    HttpRequest<CheckinDocumentCreateDTO> request) {
        return Single.fromCallable(() -> checkinDocumentService.save(new CheckinDocument(checkinDocument.getCheckinsId(),checkinDocument.getUploadDocId())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdCheckinDocument -> {return (HttpResponse<CheckinDocument>) HttpResponse
                    .created(createdCheckinDocument)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdCheckinDocument.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update a CheckinDocument
     *
     * @param checkinDocument, {@link CheckinDocument}
     * @return {@link HttpResponse<CheckinDocument>}
     */
    @Put()
    public Single<HttpResponse<CheckinDocument>> update(@Body @Valid CheckinDocument checkinDocument,
                                            HttpRequest<CheckinDocument> request) {
        if (checkinDocument == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> checkinDocumentService.update(checkinDocument))
            .observeOn(Schedulers.from(eventLoopGroup))
            .map(updatedCheckinDocument -> (HttpResponse<CheckinDocument>) HttpResponse
                    .ok()
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedCheckinDocument.getId()))))
                    .body(updatedCheckinDocument))
            .subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Delete a CheckinDocument
     *
     * @param checkinsId, id of the checkins record you wish to delete
     * @return {@link HttpResponse<?>}
     */
    @Delete("/{checkinsId}")
    public HttpResponse<?> delete(UUID checkinsId) {
        checkinDocumentService.deleteByCheckinId(checkinsId);
        return HttpResponse
                .noContent();
    }
}
package com.objectcomputing.checkins.services.checkindocument;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.concurrent.ExecutorService;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

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
                                       ExecutorService ioExecutorService){
            this.checkinDocumentService = checkinDocumentService;
            this.eventLoopGroup = eventLoopGroup;
            this.ioExecutorService = ioExecutorService;
        }
    
    @Error(exception = CheckinDocumentBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, CheckinDocumentBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Find CheckinDocument(s) based on checkinsId
     *
     * @param checkinsId
     * @return {@link List<CheckinDocument> list of CheckinDocument(s) associated with the checkinsId}
     */

    @Get("/{?checkinsId}")
    public Single<HttpResponse<Set<CheckinDocument>>> findCheckinDocument(@Nullable UUID checkinsId) {
        return Single.fromCallable(() -> checkinDocumentService.read(checkinsId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(checkinDocuments -> {
                    return (HttpResponse<Set<CheckinDocument>>) HttpResponse.ok(checkinDocuments);
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Create and save a new CheckinDocument.
     *
     * @param checkinDocument, {@link CheckinDocumentCreateDTO}
     * @return {@link HttpResponse<CheckinDocument>}
     */

    @Post("/")
    public Single<HttpResponse<CheckinDocument>> createCheckinDocument(@Body @Valid CheckinDocumentCreateDTO checkinDocument,
                                                                    HttpRequest<CheckinDocumentCreateDTO> request) {
        return Single.fromCallable(() -> checkinDocumentService.save(new CheckinDocument(checkinDocument.getCheckinsId(),checkinDocument.getUploadDocId())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdCheckinDocument -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    return (HttpResponse<CheckinDocument>) HttpResponse
                    .created(createdCheckinDocument)
                    .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), createdCheckinDocument.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update a CheckinDocument
     *
     * @param checkinDocument, {@link CheckinDocument}
     * @return {@link HttpResponse<CheckinDocument>}
     */
    @Put("/")
    public Single<HttpResponse<CheckinDocument>> update(@Body @Valid CheckinDocument checkinDocument,
                                            HttpRequest<CheckinDocument> request) {
        if (checkinDocument == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> checkinDocumentService.update(checkinDocument))
            .observeOn(Schedulers.from(eventLoopGroup))
            .map(updatedCheckinDocument -> //This lambda expression is the preferred way to do this kind of simple mapping.
                    (HttpResponse<CheckinDocument>) HttpResponse
                    .ok()
                    .headers(headers -> headers.location(
                            URI.create(String.format("%s/%s", request.getPath(), updatedCheckinDocument.getId()))))
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
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public HttpResponse<?> delete(UUID checkinsId) {
        checkinDocumentService.delete(checkinsId);
        return HttpResponse
                .noContent();
    }
}
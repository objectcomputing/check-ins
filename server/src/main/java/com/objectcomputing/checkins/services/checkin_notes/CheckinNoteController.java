package com.objectcomputing.checkins.services.checkin_notes;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import io.micronaut.scheduling.TaskExecutors;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.ExecutorService;


@Controller("/services/checkin-note")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin-note")
public class CheckinNoteController {
    private CheckinNoteServices checkinNoteServices;
    private EventLoopGroup eventLoopGroup;
    private ExecutorService ioExecutorService;

    public CheckinNoteController(CheckinNoteServices checkinNoteServices,
                                EventLoopGroup eventLoopGroup,
                                @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.checkinNoteServices = checkinNoteServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Error(exception = CheckinNotesBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, CheckinNotesBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = CheckinNotesNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, CheckinNotesNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    @Error(exception = CheckinNotesBulkLoadException.class)
    public HttpResponse<?> handleBulkLoadException(HttpRequest<?> request, CheckinNotesBulkLoadException e) {
        return HttpResponse.badRequest(e.getErrors())
                .headers(headers -> headers.location(request.getUri()));
    }

    @Error(exception = CompositeException.class)
    public HttpResponse<?> handleRxException(HttpRequest<?> request, CompositeException e) {

        for (Throwable t : e.getExceptions()) {
            if (t instanceof CheckinNotesBadArgException) {
                return handleBadArgs(request, (CheckinNotesBadArgException) t);
            }
            else if (t instanceof CheckinNotesNotFoundException) {
                return handleNotFound(request, (CheckinNotesNotFoundException) t);
            }
        }

        return HttpResponse.<JsonError>serverError();
    }

    /**
     * Create and Save a new check in note
     *
     * @param checkinNote, {@link CheckinNoteCreateDTO}
     * @param request
     * @return {@link HttpResponse <CheckinNote>}
     */
    @Post("/")
    @Secured({RoleType.Constants.PDL_ROLE, RoleType.Constants.ADMIN_ROLE})
    public Single<HttpResponse<CheckinNote>> createCheckinNote(@Body @Valid CheckinNoteCreateDTO checkinNote,
                                                                    HttpRequest<CheckinNoteCreateDTO> request) {
        return Single.fromCallable(() -> checkinNoteServices.save(new CheckinNote(checkinNote.getCheckinid(),
                checkinNote.getCreatedbyid(), checkinNote.getDescription())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdCheckinNote -> (HttpResponse<CheckinNote>) HttpResponse
                    .created(createdCheckinNote)
                    .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), createdCheckinNote.getId())))))
                        .subscribeOn(Schedulers.from(ioExecutorService));
    }

/**
     * Update a check in note
     *
     * @param checkinNote, {@link CheckinNote}
     * @return {@link HttpResponse< CheckinNote >}
     */
    @Put("/")
    @Secured({RoleType.Constants.PDL_ROLE, RoleType.Constants.ADMIN_ROLE})
    public Single<HttpResponse<CheckinNote>> updateCheckinNote(@Body @Valid CheckinNote checkinNote,
                                            HttpRequest<CheckinNote> request) {
        if (checkinNote == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> checkinNoteServices.update(checkinNote))
            .observeOn(Schedulers.from(eventLoopGroup))
            .map(updatedCheckinNote ->(HttpResponse<CheckinNote>) HttpResponse
                    .ok()
                    .headers(headers -> headers.location(
                            URI.create(String.format("%s/%s", request.getPath(), updatedCheckinNote.getId()))))
                    .body(updatedCheckinNote))
            .subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Get notes by checkind or createbyid
     *
     * @param checkinid   {@link UUID} of checkin
     * @param createdbyid {@link UUID} of member
     * @return {@link List < CheckIn > list of checkins}
     */
    @Get("/{?checkinid,createdbyid}")
    public Single<HttpResponse<Set<CheckinNote>>> findCheckinNotes(@Nullable UUID checkinid,
                                           @Nullable UUID createdbyid) {
        return Single.fromCallable(() -> checkinNoteServices.findByFields(checkinid, createdbyid))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(checkinNotes -> (HttpResponse<Set<CheckinNote>>) HttpResponse.ok(checkinNotes))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Get checkin note from id
     *
     * @param id {@link UUID} of the checkin note entry
     * @return {@link CheckinNote}
     */
    @Get("/{id}")
    public Single<HttpResponse<CheckinNote>> readCheckinNote(UUID id) {
        return Single.fromCallable(() -> {
            CheckinNote result = checkinNoteServices.read(id);
            if (result == null) {
                throw new CheckinNotesNotFoundException("No checkin note for UUID");
            }
            return result;
        })
        .observeOn(Schedulers.from(eventLoopGroup))
        .map(checkinNote -> (HttpResponse<CheckinNote>)HttpResponse.ok(checkinNote))
        .subscribeOn(Schedulers.from(ioExecutorService));
    }
}
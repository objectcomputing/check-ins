package com.objectcomputing.checkins.services.private_notes;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNote;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import java.util.concurrent.ExecutorService;

@Controller("/services/private-notes")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "private-notes")
public class PrivateNoteController {

    private final PrivateNoteServices privateNoteServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public PrivateNoteController(PrivateNoteServices privateNoteServices,
                                 EventLoopGroup eventLoopGroup,
                                 @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.privateNoteServices = privateNoteServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Create and Save a new check in private note
     *
     * @param privateNote
     * @param request
     * @return
     */
    @Post("/")
    public Mono<HttpResponse<PrivateNote>> createPrivateNote(@Body @Valid PrivateNoteCreateDTO privateNote, HttpRequest<PrivateNoteCreateDTO> request) {
        return Mono.fromCallable(() -> privateNoteServices.save(new PrivateNote(privateNote.getCheckinid(),
                privateNote.getCreatedbyid(), privateNote.getDescription())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createPrivateNote -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    return (HttpResponse<PrivateNote>) HttpResponse
                            .created(createPrivateNote)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), createPrivateNote.getId()))));
                }).subscribeOn(scheduler);

    }

    /**
     * Update a check in private note
     *
     * @param privateNote
     * @param request
     * @return
     */
    @Put("/")
    public Mono<HttpResponse<PrivateNote>> updatePrivateNote(@Body @Valid PrivateNote privateNote, HttpRequest<PrivateNoteCreateDTO> request) {
        if (privateNote == null) {
            return Mono.just(HttpResponse.ok());
        }
        return Mono.fromCallable(() -> privateNoteServices.update(privateNote))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatePrivateNote ->
                        (HttpResponse<PrivateNote>) HttpResponse
                                .ok()
                                .headers(headers -> headers.location(
                                        URI.create(String.format("%s/%s", request.getPath(), updatePrivateNote.getId()))))
                                .body(updatePrivateNote))
                .subscribeOn(scheduler);
    }

    /**
     * Get notes by checkind or createbyid
     *
     * @param checkinid
     * @param createdbyid
     * @return
     */
    @Get("/{?checkinid,createdbyid}")
    @RequiredPermission(Permissions.CAN_VIEW_CHECKINS_ELEVATED)
    public Set<PrivateNote> findPrivateNote(@Nullable UUID checkinid,
                                            @Nullable UUID createdbyid) {
        return privateNoteServices.findByFields(checkinid, createdbyid);
    }

    /**
     * Get checkin note from id
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    @RequiredPermission(Permissions.CAN_VIEW_CHECKINS_ELEVATED)
    public Mono<HttpResponse<PrivateNote>> readPrivateNote(UUID id) {
        return Mono.fromCallable(() -> {
            PrivateNote result = privateNoteServices.read(id);
            if (result == null) {
                throw new NotFoundException("No private note for UUID");
            }
            return result;
        })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(privateNote -> {
                    return (HttpResponse<PrivateNote>)HttpResponse.ok(privateNote);
                }).subscribeOn(scheduler);

    }

}
package com.objectcomputing.checkins.services.private_notes;

import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

import java.util.concurrent.ExecutorService;

@Controller("/services/private-note")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "private-note")
public class PrivateNoteController {

    private final PrivateNoteServices privateNoteServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public PrivateNoteController(PrivateNoteServices privateNoteServices, EventLoopGroup eventLoopGroup, ExecutorService ioExecutorService) {
        this.privateNoteServices = privateNoteServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and Save a new check in private note
     *
     * @param privateNote
     * @param request
     * @return
     */
    @Post("/")
    public Single<HttpResponse<PrivateNote>> createPrivateNote(@Body @Valid PrivateNoteCreateDTO privateNote, HttpRequest<PrivateNoteCreateDTO> request) {
        return Single.fromCallable(() -> privateNoteServices.save(new PrivateNote(privateNote.getCheckinid(),
                privateNote.getCreatedbyid(), privateNote.getDescription())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createPrivateNote -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    return (HttpResponse<PrivateNote>) HttpResponse
                            .created(createPrivateNote)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), createPrivateNote.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Update a check in private note
     *
     * @param privateNote
     * @param request
     * @return
     */
    @Put("/")
    public Single<HttpResponse<PrivateNote>> updatePrivateNote(@Body @Valid PrivateNote privateNote, HttpRequest<PrivateNoteCreateDTO> request) {
        if (privateNote == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> privateNoteServices.update(privateNote))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatePrivateNote ->
                        (HttpResponse<PrivateNote>) HttpResponse
                                .ok()
                                .headers(headers -> headers.location(
                                        URI.create(String.format("%s/%s", request.getPath(), updatePrivateNote.getId()))))
                                .body(updatePrivateNote))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Get checkin note from id
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    public Single<HttpResponse<PrivateNote>> readPrivateNote(UUID id) {
        return Single.fromCallable(() -> {
            PrivateNote result = privateNoteServices.read(id);
            if (result == null) {
                throw new NotFoundException("No private note for UUID");
            }
            return result;
        })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(privateNote -> {
                    return (HttpResponse<PrivateNote>)HttpResponse.ok(privateNote);
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

}
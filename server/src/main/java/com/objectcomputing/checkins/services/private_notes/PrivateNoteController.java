package com.objectcomputing.checkins.services.private_notes;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller(PrivateNoteController.PATH)
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "private-notes")
class PrivateNoteController {
    public static final String PATH = "/services/private-notes";

    private final PrivateNoteServices privateNoteServices;

    PrivateNoteController(PrivateNoteServices privateNoteServices) {
        this.privateNoteServices = privateNoteServices;
    }

    /**
     * Create and Save a new check in private note
     *
     * @param privateNote
     * @param request
     * @return
     */
    @Post
    @RequiredPermission(Permission.CAN_CREATE_PRIVATE_NOTE)
    HttpResponse<PrivateNote> createPrivateNote(@Body @Valid PrivateNoteCreateDTO privateNote, HttpRequest<?> request) {
        PrivateNote createPrivateNote = privateNoteServices.save(new PrivateNote(privateNote.getCheckinid(), privateNote.getCreatedbyid(), privateNote.getDescription()));
        URI location = UriBuilder.of(PATH).path(createPrivateNote.getId().toString()).build();
        return HttpResponse.created(createPrivateNote)
                .headers(headers -> headers.location(location));
    }

    /**
     * Update a check in private note
     *
     * @param privateNote
     * @param request
     * @return
     */
    @Put
    @RequiredPermission(Permission.CAN_UPDATE_PRIVATE_NOTE)
    HttpResponse<PrivateNote> updatePrivateNote(@Body @Valid PrivateNote privateNote, HttpRequest<?> request) {
        if (privateNote == null) {
            return HttpResponse.ok();
        }
        PrivateNote updatePrivateNote = privateNoteServices.update(privateNote);
        URI location = UriBuilder.of(PATH).path(updatePrivateNote.getId().toString()).build();
        return HttpResponse.ok(updatePrivateNote)
                .headers(headers -> headers.location(location));
    }

    /**
     * Get notes by checkind or createbyid
     *
     * @param checkinid
     * @param createdbyid
     * @return
     */
    @Get("/{?checkinid,createdbyid}")
    @RequiredPermission(Permission.CAN_VIEW_PRIVATE_NOTE)
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
    @RequiredPermission(Permission.CAN_VIEW_PRIVATE_NOTE)
    public PrivateNote readPrivateNote(UUID id) {
        PrivateNote result = privateNoteServices.read(id);
        if (result == null) {
            throw new NotFoundException("No private note for UUID");
        }
        return result;
    }
}
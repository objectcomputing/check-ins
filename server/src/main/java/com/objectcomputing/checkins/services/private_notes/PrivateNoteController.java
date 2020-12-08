package com.objectcomputing.checkins.services.private_notes;

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
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/private-note")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "private-note")
public class PrivateNoteController {

    private final PrivateNoteServices privateNoteServices;

    public PrivateNoteController(PrivateNoteServices privateNoteServices) {
        this.privateNoteServices = privateNoteServices;
    }

    @Error(exception = PrivateNotesBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, PrivateNotesBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and Save a new check in note
     *
     * @param privateNote
     * @param request
     * @return
     */
    @Post("/")
    @Secured({RoleType.Constants.PDL_ROLE, RoleType.Constants.ADMIN_ROLE})
    public HttpResponse<PrivateNote> createPrivateNote(@Body @Valid PrivateNoteCreateDTO privateNote, HttpRequest<PrivateNoteCreateDTO> request) {
        PrivateNote newPrivateNote = privateNoteServices.save(new PrivateNote(privateNote.getCheckinid(), privateNote.getCreatedbyid()
                , privateNote.getDescription()));
        return HttpResponse.created(newPrivateNote)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), newPrivateNote.getId()))));

    }

    /**
     * Update a check in note
     *
     * @param privateNote
     * @param request
     * @return
     */
    @Put("/")
    @Secured({RoleType.Constants.PDL_ROLE, RoleType.Constants.ADMIN_ROLE})
    public HttpResponse<PrivateNote> updatePrivateNote(@Body @Valid PrivateNote privateNote, HttpRequest<PrivateNoteCreateDTO> request) {
        PrivateNote updatePrivateNote = privateNoteServices.update(privateNote);
        return HttpResponse.ok().headers(headers -> headers.location(
                URI.create(String.format("%s/%s", request.getPath(), updatePrivateNote.getId()))))
                .body(updatePrivateNote);
    }

    /**
     * Get notes by checkind or createbyid
     *
     * @param checkinid
     * @param createdbyid
     * @return
     */
    @Get("/{?checkinid,createdbyid}")
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
    public PrivateNote readPrivateNote(@NotNull UUID id) {
        return privateNoteServices.read(id);
    }

}
package com.objectcomputing.checkins.services.checkin_notes;

import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/checkin-notes")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin-notes")
public class CheckinNoteController {

    private final CheckinNoteServices checkinNoteServices;

    public CheckinNoteController(CheckinNoteServices checkinNoteServices) {
        this.checkinNoteServices = checkinNoteServices;
    }

    /**
     * Create and Save a new check in note
     *
     * @param checkinNote
     * @param request
     * @return
     */
    @Post()
    @RequiredPermission(Permission.CAN_CREATE_CHECKINS)
    public HttpResponse<CheckinNote> createCheckinNote(@Body @Valid CheckinNoteCreateDTO checkinNote, HttpRequest<CheckinNoteCreateDTO> request) {
        CheckinNote newCheckinNote = checkinNoteServices.save(new CheckinNote(checkinNote.getCheckinid(), checkinNote.getCreatedbyid()
                , checkinNote.getDescription()));
        return HttpResponse.created(newCheckinNote)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), newCheckinNote.getId()))));

    }

    /**
     * Update a check in note
     *
     * @param checkinNote
     * @param request
     * @return
     */
    @Put()
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public HttpResponse<CheckinNote> updateCheckinNote(@Body @Valid CheckinNote checkinNote, HttpRequest<CheckinNoteCreateDTO> request) {
        CheckinNote updateCheckinNote = checkinNoteServices.update(checkinNote);
        return HttpResponse.ok().headers(headers -> headers.location(
                URI.create(String.format("%s/%s", request.getPath(), updateCheckinNote.getId()))))
                .body(updateCheckinNote);
    }

    /**
     * Get notes by checkind or createbyid
     *
     * @param checkinid
     * @param createdbyid
     * @return
     */
    @Get("/{?checkinid,createdbyid}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public Set<CheckinNote> findCheckinNote(@Nullable UUID checkinid,
                                            @Nullable UUID createdbyid) {
        return checkinNoteServices.findByFields(checkinid, createdbyid);
    }

    /**
     * Get checkin note from id
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public CheckinNote readCheckinNote(@NotNull UUID id) {
        return checkinNoteServices.read(id);
    }

}
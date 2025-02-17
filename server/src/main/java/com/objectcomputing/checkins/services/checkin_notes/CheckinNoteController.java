package com.objectcomputing.checkins.services.checkin_notes;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/checkin-notes")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
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
    @Post
    public HttpResponse<CheckinNote> createCheckinNote(@Body @Valid CheckinNoteCreateDTO checkinNote, HttpRequest<?> request) {
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
    @Put
    public HttpResponse<CheckinNote> updateCheckinNote(@Body @Valid CheckinNote checkinNote, HttpRequest<?> request) {
        CheckinNote updateCheckinNote = checkinNoteServices.update(checkinNote);
        return HttpResponse.ok().headers(headers -> headers.location(
                URI.create(String.format("%s/%s", request.getPath(), updateCheckinNote.getId()))))
                .body(updateCheckinNote);
    }

    /**
     * Get notes by checkind or createbyid. If no parameters are provided, and user has permission to view all checkins,
     * then return all checkin notes.
     *
     * @param checkinid
     * @param createdbyid
     * @return
     */
    @Get("/{?checkinid,createdbyid}")
    public Set<CheckinNote> findCheckinNote(@Nullable UUID checkinid, @Nullable UUID createdbyid) {
        return checkinNoteServices.findByFields(checkinid, createdbyid);
    }

    /**
     * Get checkin note from id
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    public CheckinNote readCheckinNote(@NotNull UUID id) {
        return checkinNoteServices.read(id);
    }

}

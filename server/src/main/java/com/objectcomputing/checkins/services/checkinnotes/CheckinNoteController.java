package com.objectcomputing.checkins.services.checkinnotes;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.security.rules.SecurityRule;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;


@Controller("/services/checkin-note")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin-note")
public class CheckinNoteController {

    @Inject
    CheckinNoteServices checkinNoteServices;

    /**
     * Create and Save a new check in note
     * 
     * @param checkinNote
     * @param request
     * @return
     */
    @Post("/")
    public HttpResponse<CheckinNote> createCheckinNote(@Body @Valid CheckinNoteCreateDTO checkinNote, HttpRequest<CheckinNoteCreateDTO> request) {
        CheckinNote newCheckinNote = checkinNoteServices.save(new CheckinNote(checkinNote.getCheckinid(),checkinNote.getCreatedbyid(),checkinNote.getPrivateNotes()
        ,checkinNote.getDescription()));
        return HttpResponse.created(newCheckinNote)
        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(),newCheckinNote.getUuid()))));
        
    }

    /**
     * Update a check in note
     * 
     * @param checkinNote
     * @param request
     * @return
     */
    @Put("/")
    public HttpResponse<CheckinNote> updateCheckinNote(@Body @Valid CheckinNote checkinNote, HttpRequest<CheckinNoteCreateDTO> request) {
        CheckinNote updateCheckinNote = checkinNoteServices.update(checkinNote);
        return HttpResponse.ok().headers(headers -> headers.location(
                URI.create(String.format("%s/%s", request.getPath(), updateCheckinNote.getUuid()))))
                .body(updateCheckinNote);
    }

    /**
     * Get notes by checkind or createbyid
     * @param checkinid
     * @param createdbyid
     * @return
     */
    @Get("/{?checkinid,createdbyid}")
    public Set<CheckinNote> findActionItems(@Nullable UUID checkinid,
                                           @Nullable UUID createdbyid) {
        return checkinNoteServices.findByFields(checkinid, createdbyid);
    }

    /**
     * Get all checkin notes
     * @return
     */
    @Get("/all")
    public Set<CheckinNote> readAll() {
        return checkinNoteServices.readAll();
    }

    /**
     * Get checkin note from id
     * @param id
     * @return
     */
    @Get("/{id}")
    public CheckinNote readCheckinNote(UUID id) {
        return checkinNoteServices.read(id);
    }

    /**
     * Delete checkin notes
     * @param id
     * @return
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteActionItem(UUID id) {
        checkinNoteServices.delete(id);
        return HttpResponse.ok();
    }

    
    
}
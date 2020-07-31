package com.objectcomputing.checkins.services.checkindocument;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Delete;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/services/checkin-document")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin document")
public class CheckinDocumentController {

    @Inject
    CheckinDocumentService checkinDocumentService;

    /**
     * Get a list of CheckinDocuments associated with a checkinsId.
     * 
     * @param checkinsId
     * @return
     */
    @Get("/{?checkinsId}")
    public HttpResponse<List<CheckinDocument>> findByValue(UUID checkinsId) {
        return checkinDocumentService.findBy(checkinsId);
    }

    /**
     * Save a new CheckinDocument.
     * @param checkinDocument
     * @return
     */
    @Post("/")
    public HttpResponse<CheckinDocument> save(@Body @Valid CheckinDocument checkinDocument) {
        return checkinDocumentService.save(checkinDocument);
    }

    /**
     * Update a CheckinDocument.
     * @param checkinDocument
     * @return
     */
    @Put("/")
    public HttpResponse<CheckinDocument> update(@Body @Valid CheckinDocument checkinDocument) {
        return checkinDocumentService.update(checkinDocument);
    }

    /**
     * Delete a CheckinDocument.
     * @param checkinsId
     * @return
     */
    @Delete("/{?checkinId}")
    public HttpResponse<CheckinDocument> deleteCheckin(@NotNull UUID checkinsId) {
        return checkinDocumentService.deleteCheckin(checkinsId);
    }

    /**
     * Delete an uploaded document.
     * @param uploadDocId
     * @return
     */
    @Delete("/{?uploadDocId}")
    public HttpResponse<CheckinDocument> deleteUploadedDocument(@NotNull String uploadDocId) {
        return checkinDocumentService.deleteUploadedDocument(uploadDocId);
    }


}
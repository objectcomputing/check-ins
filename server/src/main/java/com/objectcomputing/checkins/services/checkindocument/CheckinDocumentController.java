package com.objectcomputing.checkins.services.checkindocument;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;

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
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/services/checkin-document")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin document")
public class CheckinDocumentController {

    @Inject
    CheckinDocumentServices checkinDocumentService;

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
    public Set<CheckinDocument> findCheckinDocuments(@Nullable UUID checkinsId) {
        return checkinDocumentService.read(checkinsId);
    }

    /**
     * Create and save a new CheckinDocument.
     *
     * @param checkinDocument, {@link CheckinDocumentCreateDTO}
     * @return {@link HttpResponse<CheckinDocument>}
     */

    @Post(value = "/")
    public HttpResponse<CheckinDocument> createACheckinDocument(@Body @Valid CheckinDocumentCreateDTO checkinDocument,
                                                                HttpRequest<CheckinDocumentCreateDTO> request) {
        CheckinDocument newCheckinDocument = checkinDocumentService.save(new CheckinDocument(checkinDocument.getCheckinsId(), checkinDocument.getUploadDocId()));
        return HttpResponse
                .created(newCheckinDocument)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), newCheckinDocument.getId()))));
    }

    /**
     * Update a CheckinDocument
     *
     * @param checkinDocument, {@link CheckinDocument}
     * @return {@link HttpResponse<CheckinDocument>}
     */
    @Put("/")
    public HttpResponse<CheckinDocument> update(@Body @Valid CheckinDocument checkinDocument, HttpRequest<CheckinDocument> request) {
        CheckinDocument updatedCheckinDocument = checkinDocumentService.update(checkinDocument);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), updatedCheckinDocument.getId()))))
                .body(updatedCheckinDocument);
    }

    /**
     * Delete a CheckinDocument
     *
     * @param checkinsId, id of the checkins record you wish to delete
     * @return {@link HttpResponse<?>}
     */
    @Delete("/{checkinsId}")
    public HttpResponse<?> delete(UUID checkinsId) {
        checkinDocumentService.delete(checkinsId);
        return HttpResponse
                .noContent();
    }
}
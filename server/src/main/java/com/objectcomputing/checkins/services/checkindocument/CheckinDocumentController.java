package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller("/services/checkin-documents")
@ExecuteOn(TaskExecutors.IO)
@Secured({RoleType.Constants.ADMIN_ROLE, RoleType.Constants.PDL_ROLE})
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin documents")
public class CheckinDocumentController {
        
    private final CheckinDocumentServices checkinDocumentService;

    public CheckinDocumentController(CheckinDocumentServices checkinDocumentService){
        this.checkinDocumentService = checkinDocumentService;
    }


    /**
     * Find CheckinDocument(s) based on checkinsId
     *
     * @param checkinsId
     * @return {@link Set<CheckinDocument> Set of CheckinDocument(s) associated with the checkinsId}
     */

    @Get("/{?checkinsId}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKIN_DOCUMENT)
    public Mono<HttpResponse<Set<CheckinDocument>>> findCheckinDocument(@Nullable UUID checkinsId) {
        return Mono.fromCallable(() -> checkinDocumentService.read(checkinsId))
                .map(HttpResponse::ok);
    }

    /**
     * Create and save a new CheckinDocument.
     *
     * @param checkinDocument, {@link CheckinDocumentCreateDTO}
     * @return {@link HttpResponse<CheckinDocument>}
     */

    @Post
    @RequiredPermission(Permission.CAN_CREATE_CHECKIN_DOCUMENT)
    public Mono<HttpResponse<CheckinDocument>> createCheckinDocument(@Body @Valid CheckinDocumentCreateDTO checkinDocument,
                                                                    HttpRequest<?> request) {
        return Mono.fromCallable(() -> checkinDocumentService.save(new CheckinDocument(checkinDocument.getCheckinsId(),checkinDocument.getUploadDocId())))
                .map(createdCheckinDocument -> HttpResponse.created(createdCheckinDocument)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdCheckinDocument.getId())))));
    }

    /**
     * Update a CheckinDocument
     *
     * @param checkinDocument, {@link CheckinDocument}
     * @return {@link HttpResponse<CheckinDocument>}
     */
    @Put
    @RequiredPermission(Permission.CAN_UPDATE_CHECKIN_DOCUMENT)
    public Mono<HttpResponse<CheckinDocument>> update(@Body @Valid CheckinDocument checkinDocument, HttpRequest<?> request) {
        if (checkinDocument == null) {
            return Mono.just(HttpResponse.ok());
        }
        return Mono.fromCallable(() -> checkinDocumentService.update(checkinDocument))
            .map(updatedCheckinDocument -> HttpResponse.ok(updatedCheckinDocument)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedCheckinDocument.getId())))));

    }

    /**
     * Delete a CheckinDocument
     *
     * @param checkinsId, id of the checkins record you wish to delete
     * @return {@link HttpResponse<>}
     */
    @Delete("/{checkinsId}")
    @RequiredPermission(Permission.CAN_DELETE_CHECKIN_DOCUMENT)
    public Mono<HttpResponse<?>> delete(UUID checkinsId) {
        return Mono.fromRunnable(() -> checkinDocumentService.deleteByCheckinId(checkinsId))
                .thenReturn(HttpResponse.noContent());

    }
}
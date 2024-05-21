package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.checkindocument.CheckinDocumentController.PATH;

@Controller(PATH)
@ExecuteOn(TaskExecutors.IO)
@Secured({RoleType.Constants.ADMIN_ROLE, RoleType.Constants.PDL_ROLE})
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "checkin documents")
class CheckinDocumentController {
    public static final String PATH = "/services/checkin-documents";
    private final CheckinDocumentServices checkinDocumentService;

    CheckinDocumentController(CheckinDocumentServices checkinDocumentService){
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
    Set<CheckinDocument> findCheckinDocument(@Nullable UUID checkinsId) {
        return checkinDocumentService.read(checkinsId);
    }

    /**
     * Create and save a new CheckinDocument.
     *
     * @param checkinDocument, {@link CheckinDocumentCreateDTO}
     * @return {@link HttpResponse<CheckinDocument>}
     */

    @Post()
    @RequiredPermission(Permission.CAN_CREATE_CHECKIN_DOCUMENT)
    HttpResponse<CheckinDocument> createCheckinDocument(@Body @Valid CheckinDocumentCreateDTO checkinDocument) {
        CheckinDocument createdCheckinDocument = checkinDocumentService.save(new CheckinDocument(checkinDocument.getCheckinsId(), checkinDocument.getUploadDocId()));
        URI location = UriBuilder.of(PATH).path(createdCheckinDocument.getId().toString()).build();
        return HttpResponse.created(createdCheckinDocument, location);
    }

    /**
     * Update a CheckinDocument
     *
     * @param checkinDocument, {@link CheckinDocument}
     * @return {@link HttpResponse<CheckinDocument>}
     */
    @Put()
    @RequiredPermission(Permission.CAN_UPDATE_CHECKIN_DOCUMENT)
    HttpResponse<?> update(@Body @Valid CheckinDocument checkinDocument) {
        if (checkinDocument == null) {
            return HttpResponse.ok();
        }
        CheckinDocument updatedCheckinDocument = checkinDocumentService.update(checkinDocument);
        URI location = UriBuilder.of(PATH).path(updatedCheckinDocument.getId().toString()).build();
        return HttpResponse.ok(updatedCheckinDocument)
                .headers(headers -> headers.location(location));

    }

    /**
     * Delete a CheckinDocument
     *
     * @param checkinsId, id of the checkins record you wish to delete
     */
    @Delete("/{checkinsId}")
    @RequiredPermission(Permission.CAN_DELETE_CHECKIN_DOCUMENT)
    @Status(HttpStatus.NO_CONTENT)
    void delete(UUID checkinsId) {
        checkinDocumentService.deleteByCheckinId(checkinsId);
    }
}
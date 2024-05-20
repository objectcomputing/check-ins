package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/action-items")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "action-items")
public class ActionItemController {

    private ActionItemServices actionItemServices;
    public ActionItemController(ActionItemServices actionItemServices) {
        this.actionItemServices = actionItemServices;
    }
    /**
     * Create and save a new actionItem.
     *
     * @param actionItem, {@link ActionItemCreateDTO}
     * @return {@link HttpResponse <ActionItem>}
     */
    @Post()
    @RequiredPermission(Permission.CAN_CREATE_CHECKINS)
    public Mono<HttpResponse<ActionItem>> createActionItem(@Body @Valid ActionItemCreateDTO actionItem,
                                                     HttpRequest<?> request) {
        return Mono.fromCallable(() -> actionItemServices.save(new ActionItem(actionItem.getCheckinid(),
                        actionItem.getCreatedbyid(), actionItem.getDescription())))
                .map(actionItemNew -> HttpResponse.created(actionItemNew).headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), actionItemNew.getId())))));
    }
    /**
     * Update actionItem.
     *
     * @param actionItem, {@link ActionItem}
     * @return {@link HttpResponse< ActionItem >}
     */
    @Put()
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public Mono<HttpResponse<?>> updateActionItem(@Body @Valid ActionItem actionItem, HttpRequest<?> request) {
        return Mono.fromCallable(() -> actionItemServices.update(actionItem))
                .map(actionItemNew -> HttpResponse.ok(actionItemNew).headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), actionItemNew.getId())))));
    }

    /**
     * Delete actionItem
     *
     * @param id, id of {@link ActionItem} to delete
     */
    @Delete("/{id}")
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public Mono<HttpResponse<?>> deleteActionItem(UUID id) {
        return Mono.fromRunnable(() -> actionItemServices.delete(id))
                .map(actionItemNew -> HttpResponse.ok());
    }

    /**
     * Get ActionItem based off id
     *
     * @param id {@link UUID} of the action item entry
     * @return {@link ActionItem}
     */
    @Get("/{id}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public Mono<ActionItem> readActionItem(UUID id) {
        return Mono.fromCallable(() -> actionItemServices.read(id));
    }

    /**
     * Find action items that match all filled in parameters, return all results when given no params
     *
     * @param checkinid   {@link UUID} of checkin
     * @param createdbyid {@link UUID} of member
     * @return {@link List < CheckIn > list of checkins}
     */
    @Get("/{?checkinid,createdbyid}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public Mono<Set<ActionItem>> findActionItems(@Nullable UUID checkinid,
                                           @Nullable UUID createdbyid) {
        return Mono.fromCallable(() -> actionItemServices.findByFields(checkinid, createdbyid));
    }

}

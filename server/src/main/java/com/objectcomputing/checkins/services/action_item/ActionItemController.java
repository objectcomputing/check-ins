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

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/action-items")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
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
    public HttpResponse<ActionItem> createActionItem(@Body @Valid ActionItemCreateDTO actionItem,
                                                     HttpRequest<?> request) {
        ActionItem newActionItem = actionItemServices.save(new ActionItem(actionItem.getCheckinid(),
                actionItem.getCreatedbyid(), actionItem.getDescription()));
        return HttpResponse
                .created(newActionItem)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newActionItem.getId()))));
    }

    /**
     * Update actionItem.
     *
     * @param actionItem, {@link ActionItem}
     * @return {@link HttpResponse< ActionItem >}
     */
    @Put()
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public HttpResponse<?> updateActionItem(@Body @Valid ActionItem actionItem, HttpRequest<?> request) {
        ActionItem updatedActionItem = actionItemServices.update(actionItem);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedActionItem.getId()))))
                .body(updatedActionItem);

    }

    /**
     * Delete actionItem
     *
     * @param id, id of {@link ActionItem} to delete
     */
    @Delete("/{id}")
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public HttpResponse<?> deleteActionItem(UUID id) {
        actionItemServices.delete(id);
        return HttpResponse
                .ok();
    }

    /**
     * Get ActionItem based off id
     *
     * @param id {@link UUID} of the action item entry
     * @return {@link ActionItem}
     */
    @Get("/{id}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public ActionItem readActionItem(UUID id) {
        return actionItemServices.read(id);
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
    public Set<ActionItem> findActionItems(@Nullable UUID checkinid,
                                           @Nullable UUID createdbyid) {
        return actionItemServices.findByFields(checkinid, createdbyid);
    }

}

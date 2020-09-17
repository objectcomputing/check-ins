package com.objectcomputing.checkins.services.action_item;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/action-item")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "action-item")
public class ActionItemController {
    private static final Logger LOG = LoggerFactory.getLogger(ActionItemController.class);

    private ActionItemServices actionItemServices;
    private EventLoopGroup eventLoopGroup;

    public ActionItemController(ActionItemServices actionItemServices,
                                EventLoopGroup eventLoopGroup) {
        this.actionItemServices = actionItemServices;
        this.eventLoopGroup = eventLoopGroup;
    }

    @Error(exception = ActionItemBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, ActionItemBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and save a new actionItem.
     *
     * @param actionItem, {@link ActionItemCreateDTO}
     * @return {@link HttpResponse <ActionItem>}
     */
    @Post()
    public Single<HttpResponse<ActionItem>> createActionItem(@Body @Valid ActionItemCreateDTO actionItem,
                                                                    HttpRequest<ActionItemCreateDTO> request) {
        LOG.info("Entering controller on main event loop");
        return actionItemServices.save(new ActionItem(actionItem.getCheckinid(),
                actionItem.getCreatedbyid(), actionItem.getDescription()))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdActionItem -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    LOG.info("Back on the main event loop in the controller");
                    return HttpResponse
                    .created(createdActionItem)
                    .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), createdActionItem.getId()))));
                });
    }

    /**
     * Update actionItem.
     *
     * @param actionItem, {@link ActionItem}
     * @return {@link HttpResponse< ActionItem >}
     */
    @Put()
    public Single<HttpResponse<?>> updateActionItem(@Body @Valid ActionItem actionItem,
                                            HttpRequest<ActionItem> request) {
        LOG.info("Entering controller on main event loop");
        return actionItemServices.update(actionItem)
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedActionItem -> //This lambda expression is the preferred way to do this kind of simple mapping.
                HttpResponse
                        .ok()
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), updatedActionItem.getId()))))
                        .body(updatedActionItem));

    }

    /**
     * Delete actionItem
     *
     * @param id, id of {@link ActionItem} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteActionItem(UUID id) {
        LOG.info("Entering controller on main event loop");
        actionItemServices.delete(id);
        LOG.info("Back on main event loop. Note when this occurs in the log");
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
    public Single<HttpResponse<ActionItem>> readActionItem(UUID id) {
        LOG.info("Entering controller on main event loop");
        return actionItemServices.read(id)
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(actionItem -> HttpResponse.ok(actionItem));
    }

    /**
     * Find action items that match all filled in parameters, return all results when given no params
     *
     * @param checkinid   {@link UUID} of checkin
     * @param createdbyid {@link UUID} of member
     * @return {@link List < CheckIn > list of checkins}
     */
    @Get("/{?checkinid,createdbyid}")
    public Single<HttpResponse<Set<ActionItem>>> findActionItems(@Nullable UUID checkinid,
                                           @Nullable UUID createdbyid) {
        LOG.info("Entering controller on main event loop");
        return actionItemServices.findByFields(checkinid, createdbyid)
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(actionItems -> {
                    LOG.info("Mapping on main event loop");
                    return HttpResponse.ok(actionItems);
                });
    }

    /**
     * Load action items
     *
     * @param actionItems, {@link List< ActionItemCreateDTO > to load {@link ActionItem action items}}
     * @return {@link HttpResponse<List< ActionItem >}
     */
    @Post("/items")
    public HttpResponse<?> loadActionItems(@Body @Valid @NotNull List<ActionItemCreateDTO> actionItems,
                                           HttpRequest<List<ActionItem>> request) {
        LOG.info("Entire controller method can be done in event loop. Not sure this method should exist, but it is easy to implement this way");
        List<String> errors = new ArrayList<>();
        List<ActionItem> actionItemsCreated = new ArrayList<>();
        for (ActionItemCreateDTO actionItemDTO : actionItems) {
            ActionItem actionItem = new ActionItem(actionItemDTO.getCheckinid(),
                    actionItemDTO.getCreatedbyid(), actionItemDTO.getDescription());
            try {
                actionItemServices.save(actionItem);
                actionItemsCreated.add(actionItem);
            } catch (ActionItemBadArgException e) {
                errors.add(String.format("Member %s's action item was not added to CheckIn %s because: %s", actionItem.getCreatedbyid(),
                        actionItem.getCheckinid(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(actionItemsCreated)
                    .headers(headers -> headers.location(request.getUri()));
        } else {
            return HttpResponse.badRequest(errors)
                    .headers(headers -> headers.location(request.getUri()));
        }
    }


}

package com.objectcomputing.checkins.services.action_item;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/action-item")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "action-item")
public class ActionItemController {
    private static final Logger LOG = LoggerFactory.getLogger(ActionItemController.class);

    private ActionItemServices actionItemServices;
    private EventLoopGroup eventLoopGroup;
    private ExecutorService ioExecutorService;

    public ActionItemController(ActionItemServices actionItemServices,
                                EventLoopGroup eventLoopGroup,
                                @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.actionItemServices = actionItemServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Error(exception = ActionItemBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, ActionItemBadArgException e) {
        LOG.info("Throwing the wrong exception");
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = ActionItemNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, ActionItemNotFoundException e) {
        LOG.info("Throwing the right exception");
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    @Error(exception = ActionItemsBulkLoadException.class)
    public HttpResponse<?> handleBulkLoadException(HttpRequest<?> request, ActionItemsBulkLoadException e) {
        LOG.info("Throwing the right exception");
        return HttpResponse.badRequest(e.getErrors())
                .headers(headers -> headers.location(request.getUri()));
    }

    @Error(exception = CompositeException.class)
    public HttpResponse<?> handleRxException(HttpRequest<?> request, CompositeException e) {
        LOG.info("OH NO COMPOSITES");

        for (Throwable t : e.getExceptions()) {
            if (t instanceof ActionItemBadArgException) {
                return handleBadArgs(request, (ActionItemBadArgException) t);
            }
            else if (t instanceof ActionItemNotFoundException) {
                return handleNotFound(request, (ActionItemNotFoundException) t);
            }
        }

        return HttpResponse.<JsonError>serverError();
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
        return Single.fromCallable(() -> actionItemServices.save(new ActionItem(actionItem.getCheckinid(),
                actionItem.getCreatedbyid(), actionItem.getDescription())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdActionItem -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    LOG.info("Back on the main event loop in the controller");
                    return (HttpResponse<ActionItem>) HttpResponse
                    .created(createdActionItem)
                    .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), createdActionItem.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update actionItem.
     *
     * @param actionItem, {@link ActionItem}
     * @return {@link HttpResponse< ActionItem >}
     */
    @Put()
    public Single<HttpResponse<ActionItem>> updateActionItem(@Body @Valid ActionItem actionItem,
                                            HttpRequest<ActionItem> request) {
        LOG.info("Entering controller on main event loop");
        if (actionItem == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> actionItemServices.update(actionItem))
            .observeOn(Schedulers.from(eventLoopGroup))
            .map(updatedActionItem -> //This lambda expression is the preferred way to do this kind of simple mapping.
                    (HttpResponse<ActionItem>) HttpResponse
                    .ok()
                    .headers(headers -> headers.location(
                            URI.create(String.format("%s/%s", request.getPath(), updatedActionItem.getId()))))
                    .body(updatedActionItem))
            .subscribeOn(Schedulers.from(ioExecutorService));

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
        return Single.fromCallable(() -> {
            ActionItem result = actionItemServices.read(id);
            if (result == null) {
                throw new ActionItemNotFoundException("No action item for UUID");
            }
            return result;
        })
        .observeOn(Schedulers.from(eventLoopGroup))
        .map(actionItem -> {
            LOG.info("Successful find");
            return (HttpResponse<ActionItem>)HttpResponse.ok(actionItem);
        }).subscribeOn(Schedulers.from(ioExecutorService));
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
        return Single.fromCallable(() -> actionItemServices.findByFields(checkinid, createdbyid))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(actionItems -> {
                    LOG.info("Mapping on main event loop");
                    return (HttpResponse<Set<ActionItem>>) HttpResponse.ok(actionItems);
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Load action items
     *
     * @param actionItems, {@link List< ActionItemCreateDTO > to load {@link ActionItem action items}}
     * @return {@link HttpResponse<List< ActionItem >}
     */
    @Post("/items")
    public Single<HttpResponse<?>> loadActionItems(@Body @Valid @NotNull List<ActionItemCreateDTO> actionItems,
                                           HttpRequest<List<ActionItem>> request) {
        LOG.info("Entire controller method can be done in event loop. Not sure this method should exist, but it is easy to implement this way");
        return Single.fromCallable(() -> {
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
                return actionItemsCreated;
            }
            throw new ActionItemsBulkLoadException(errors);
        }).map(actionItemsCreated -> HttpResponse.created(actionItemsCreated)
                .headers(headers -> headers.location(request.getUri())));
    }


}

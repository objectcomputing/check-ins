package com.objectcomputing.checkins.services.agenda_item;

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
import io.reactivex.Single;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Controller("/services/agenda-item")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "agenda-item")
public class AgendaItemController {

    private final AgendaItemServices agendaItemServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public AgendaItemController(AgendaItemServices agendaItemServices,
                                EventLoopGroup eventLoopGroup,
                                @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.agendaItemServices = agendaItemServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }
  
    @Error(exception = AgendaItemBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, AgendaItemBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = AgendaItemNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, AgendaItemNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    @Error(exception = AgendaItemsBulkLoadException.class)
    public HttpResponse<?> handleBulkLoadException(HttpRequest<?> request, AgendaItemsBulkLoadException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = CompositeException.class)
    public HttpResponse<?> handleRxException(HttpRequest<?> request, CompositeException e) {

        for (Throwable t : e.getExceptions()) {
            if (t instanceof AgendaItemBadArgException) {
                return handleBadArgs(request, (AgendaItemBadArgException) t);
            }
            else if (t instanceof AgendaItemNotFoundException) {
                return handleNotFound(request, (AgendaItemNotFoundException) t);
            }
        }

        return HttpResponse.<JsonError>serverError();
    }

    /**
     * Create and save a new agendaItem.
     *
     * @param agendaItem, {@link AgendaItemCreateDTO}
     * @return {@link HttpResponse <AgendaItem>}
     */
    @Post("/")
    public Single<HttpResponse<AgendaItem>> createAgendaItem(@Body @Valid AgendaItemCreateDTO agendaItem,
                                                             HttpRequest<AgendaItemCreateDTO> request) {
        return Single.fromCallable(() -> agendaItemServices.save(new AgendaItem(agendaItem.getCheckinid(),
                agendaItem.getCreatedbyid(), agendaItem.getDescription())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdAgendaItem -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    return (HttpResponse<AgendaItem>) HttpResponse
                            .created(createdAgendaItem)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), createdAgendaItem.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

     /**
     * Update a agenda item
     *
     * @param agendaItem, {@link AgendaItem}
     * @return {@link HttpResponse< AgendaItem >}
     */
    @Put("/")
    public Single<HttpResponse<AgendaItem>> updateAgendaItem(@Body @Valid AgendaItem agendaItem, HttpRequest<AgendaItem> request) {
        if (agendaItem == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> agendaItemServices.update(agendaItem))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedAgendaItem ->
                        (HttpResponse<AgendaItem>) HttpResponse
                                .ok()
                                .headers(headers -> headers.location(
                                        URI.create(String.format("%s/%s", request.getPath(), updatedAgendaItem.getId()))))
                                .body(updatedAgendaItem))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Find agenda items that match all filled in parameters, return all results when given no params
     *
     * @param checkinid   {@link UUID} of checkin
     * @param createdbyid {@link UUID} of member	
     * @return {@link List < CheckIn > list of checkins}	
     */
    @Get("/{?checkinid,createdbyid}")
    public Single<HttpResponse<Set<AgendaItem>>> findAgendaItems(@Nullable UUID checkinid,
                                                                 @Nullable UUID createdbyid) {
        return Single.fromCallable(() -> agendaItemServices.findByFields(checkinid, createdbyid))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(agendaItems -> {
                    return (HttpResponse<Set<AgendaItem>>) HttpResponse.ok(agendaItems);
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

     /**	
     * Get agenda item from id
     *
     * @param id {@link UUID} of the agenda item entry
     * @return {@link AgendaItem}
     */
    @Get("/{id}")
    public Single<HttpResponse<AgendaItem>> readAgendaItem(UUID id) {
        return Single.fromCallable(() -> {
            AgendaItem result = agendaItemServices.read(id);
            if (result == null) {
                throw new AgendaItemNotFoundException("No agenda item for UUID");
            }
            return result;
        })
        .observeOn(Schedulers.from(eventLoopGroup))
        .map(agendaItem -> {
            return (HttpResponse<AgendaItem>)HttpResponse.ok(agendaItem);
        }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Delete agendaItem
     *
     * @param id, id of {@link AgendaItem} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteAgendaItem(UUID id) {
        agendaItemServices.delete(id);
        return HttpResponse
                .ok();
    }

}
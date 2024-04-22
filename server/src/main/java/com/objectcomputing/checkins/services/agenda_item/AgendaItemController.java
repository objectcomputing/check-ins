package com.objectcomputing.checkins.services.agenda_item;

import com.objectcomputing.checkins.exceptions.NotFoundException;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/agenda-items")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "agenda-items")
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

    /**
     * Create and save a new agendaItem.
     *
     * @param agendaItem, {@link AgendaItemCreateDTO}
     * @return {@link HttpResponse <AgendaItem>}
     */
    @Post("/")
    @RequiredPermission(Permission.CAN_CREATE_CHECKINS)
    public Mono<HttpResponse<AgendaItem>> createAgendaItem(@Body @Valid AgendaItemCreateDTO agendaItem,
                                                             HttpRequest<AgendaItemCreateDTO> request) {
        return Mono
            .just(agendaItemServices.save(new AgendaItem(agendaItem.getCheckinid(), agendaItem.getCreatedbyid(), agendaItem.getDescription())))
            .publishOn(Schedulers.fromExecutor(eventLoopGroup))
            .map(createAgendaItem -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    return (HttpResponse<AgendaItem>) HttpResponse
                            .created(createAgendaItem)
                            .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), createAgendaItem.getId()))
                            ));
            }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

     /**
     * Update a agenda item
     *
     * @param agendaItem, {@link AgendaItem}
     * @return {@link HttpResponse< AgendaItem >}
     */
    @Put("/")
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public Mono<HttpResponse<AgendaItem>> updateAgendaItem(@Body @Valid AgendaItem agendaItem, HttpRequest<AgendaItem> request) {
        if (agendaItem == null) {
            return Mono.just(HttpResponse.ok());
        }
        return Mono.fromCallable(() -> agendaItemServices.update(agendaItem))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedAgendaItem ->
                        (HttpResponse<AgendaItem>) HttpResponse
                                .ok()
                                .headers(headers -> headers.location(
                                        URI.create(String.format("%s/%s", request.getPath(), updatedAgendaItem.getId()))))
                                .body(updatedAgendaItem))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Find agenda items that match all filled in parameters, return all results when given no params
     *
     * @param checkinid   {@link UUID} of checkin
     * @param createdbyid {@link UUID} of member	
     * @return {@link List <CheckIn > list of checkins
     */
    @Get("/{?checkinid,createdbyid}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public Mono<HttpResponse<Set<AgendaItem>>> findAgendaItems(@Nullable UUID checkinid,
                                                                 @Nullable UUID createdbyid) {
        return Mono.fromCallable(() -> agendaItemServices.findByFields(checkinid, createdbyid))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(agendaItems -> {
                    return (HttpResponse<Set<AgendaItem>>) HttpResponse.ok(agendaItems);
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

     /**	
     * Get agenda item from id
     *
     * @param id {@link UUID} of the agenda item entry
     * @return {@link AgendaItem}
     */
    @Get("/{id}")
    @RequiredPermission(Permission.CAN_VIEW_CHECKINS)
    public Mono<HttpResponse<AgendaItem>> readAgendaItem(UUID id) {
        return Mono.fromCallable(() -> agendaItemServices.read(id))
                .switchIfEmpty(Mono.error(new NotFoundException("No agennda item for UUID")))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(agendaItem -> (HttpResponse<AgendaItem>)HttpResponse.ok(agendaItem))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Delete agendaItem
     *
     * @param id, id of {@link AgendaItem} to delete
     */
    @Delete("/{id}")
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public HttpResponse<?> deleteAgendaItem(UUID id) {
        agendaItemServices.delete(id);
        return HttpResponse
                .ok();
    }

}
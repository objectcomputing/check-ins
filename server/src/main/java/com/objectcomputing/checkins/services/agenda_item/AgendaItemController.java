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

@Controller("/services/agenda-items")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "agenda-items")
public class AgendaItemController {

    private final AgendaItemServices agendaItemServices;

    public AgendaItemController(AgendaItemServices agendaItemServices) {
        this.agendaItemServices = agendaItemServices;
    }

    /**
     * Create and save a new agendaItem.
     *
     * @param agendaItem, {@link AgendaItemCreateDTO}
     * @return {@link HttpResponse <AgendaItem>}
     */
    @Post("/")
    @RequiredPermission(Permission.CAN_CREATE_CHECKINS)
    public Mono<HttpResponse<AgendaItem>> createAgendaItem(@Body @Valid AgendaItemCreateDTO agendaItem, HttpRequest<?> request) {
        return Mono.fromCallable(() -> agendaItemServices.save(new AgendaItem(agendaItem.getCheckinid(), agendaItem.getCreatedbyid(), agendaItem.getDescription())))
            .map(createAgendaItem -> HttpResponse.created(createAgendaItem)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createAgendaItem.getId())))));
    }

     /**
     * Update a agenda item
     *
     * @param agendaItem, {@link AgendaItem}
     * @return {@link HttpResponse< AgendaItem >}
     */
    @Put("/")
    @RequiredPermission(Permission.CAN_UPDATE_CHECKINS)
    public Mono<HttpResponse<AgendaItem>> updateAgendaItem(@Body @Valid AgendaItem agendaItem, HttpRequest<?> request) {
        if (agendaItem == null) {
            return Mono.just(HttpResponse.ok());
        }
        return Mono.fromCallable(() -> agendaItemServices.update(agendaItem))
                .map(updatedAgendaItem ->
                        HttpResponse.ok(updatedAgendaItem)
                                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedAgendaItem.getId())))));
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
    public Mono<HttpResponse<Set<AgendaItem>>> findAgendaItems(@Nullable UUID checkinid, @Nullable UUID createdbyid) {
        return Mono.fromCallable(() -> agendaItemServices.findByFields(checkinid, createdbyid))
                .map(HttpResponse::ok);
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
                .switchIfEmpty(Mono.error(new NotFoundException("No agenda item for UUID")))
                .map(HttpResponse::ok);

    }

    /**
     * Delete agendaItem
     *
     * @param id, id of {@link AgendaItem} to delete
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteAgendaItem(UUID id) {
        return Mono.fromRunnable(() -> agendaItemServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }
}
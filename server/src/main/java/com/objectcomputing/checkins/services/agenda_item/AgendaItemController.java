package com.objectcomputing.checkins.services.agenda_item;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Controller(AgendaItemController.PATH)
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "agenda-items")
class AgendaItemController {
    public static final String PATH = "/services/agenda-items";

    private final AgendaItemServices agendaItemServices;

    AgendaItemController(AgendaItemServices agendaItemServices) {
        this.agendaItemServices = agendaItemServices;
    }

    /**
     * Create and save a new agendaItem.
     *
     * @param agendaItem, {@link AgendaItemCreateDTO}
     * @return {@link HttpResponse <AgendaItem>}
     */
    @Post("/")
    HttpResponse<AgendaItem> createAgendaItem(@Body @Valid AgendaItemCreateDTO agendaItem) {
        AgendaItem createAgendaItem = agendaItemServices.save(new AgendaItem(agendaItem.getCheckinid(), agendaItem.getCreatedbyid(), agendaItem.getDescription()));
        URI location = UriBuilder.of(PATH).path(createAgendaItem.getId().toString()).build();
        return HttpResponse.created(createAgendaItem)
                .headers(headers -> headers.location(location));
    }

     /**
     * Update a agenda item
     *
     * @param agendaItem, {@link AgendaItem}
     * @return {@link HttpResponse<AgendaItem>}
     */
    @Put("/")
    HttpResponse<?> updateAgendaItem(@Body @Valid AgendaItem agendaItem) {
        if (agendaItem == null) {
            return HttpResponse.ok();
        }
        AgendaItem updatedAgendaItem = agendaItemServices.update(agendaItem);
        URI location = UriBuilder.of(PATH).path(updatedAgendaItem.getId().toString()).build();
        return HttpResponse.ok(updatedAgendaItem)
                .headers(headers -> headers.location(location));
    }

    /**
     * Find agenda items that match all filled in parameters, or return all results when provided no parameters, and
     * the user has permission to view all checkin items
     *
     * @param checkinid   {@link UUID} of checkin
     * @param createdbyid {@link UUID} of member	
     * @return a Set of {@link CheckIn}
     */
    @Get("/{?checkinid,createdbyid}")
    Set<AgendaItem> findAgendaItems(@Nullable UUID checkinid, @Nullable UUID createdbyid) {
        return agendaItemServices.findByFields(checkinid, createdbyid);
    }

     /**	
     * Get agenda item from id
     *
     * @param id {@link UUID} of the agenda item entry
     * @return {@link AgendaItem}
     */
    @Get("/{id}")
    AgendaItem readAgendaItem(UUID id) {
        AgendaItem read = agendaItemServices.read(id);
        if (read == null) {
            throw new NotFoundException("No agenda item for UUID");
        }
        return read;
    }

    /**
     * Delete agendaItem
     *
     * @param id, id of {@link AgendaItem} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    void deleteAgendaItem(UUID id) {
        agendaItemServices.delete(id);
    }
}

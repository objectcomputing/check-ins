package com.objectcomputing.checkins.services.agenda_item;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/agenda-item")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "agenda-item")
public class AgendaItemController {

    @Inject
    private AgendaItemServices agendaItemServices;

    @Error(exception = AgendaItemBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, AgendaItemBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and save a new agendaItem.
     *
     * @param agendaItem, {@link AgendaItemCreateDTO}
     * @return {@link HttpResponse <AgendaItem>}
     */
    @Post()
    public HttpResponse<AgendaItem> createAgendaItem(@Body @Valid AgendaItemCreateDTO agendaItem,
                                                     HttpRequest<AgendaItemCreateDTO> request) {
        AgendaItem newAgendaItem = agendaItemServices.save(new AgendaItem(agendaItem.getCheckinid(),
                agendaItem.getCreatedbyid(), agendaItem.getDescription()));
        return HttpResponse
                .created(newAgendaItem)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newAgendaItem.getId()))));
    }

    /**
     * Update agendaItem.
     *
     * @param agendaItem, {@link AgendaItem}
     * @return {@link HttpResponse< AgendaItem >}
     */
    @Put()
    public HttpResponse<?> updateAgendaItem(@Body @Valid AgendaItem agendaItem, HttpRequest<AgendaItem> request) {
        AgendaItem updatedAgendaItem = agendaItemServices.update(agendaItem);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedAgendaItem.getId()))))
                .body(updatedAgendaItem);

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

    /**
     * Get AgendaItem based off id
     *
     * @param id {@link UUID} of the agenda item entry
     * @return {@link AgendaItem}
     */
    @Get("/{id}")
    public AgendaItem readAgendaItem(UUID id) {
        return agendaItemServices.read(id);
    }

    /**
     * Find agenda items that match all filled in parameters, return all results when given no params
     *
     * @param checkinid   {@link UUID} of checkin
     * @param createdbyid {@link UUID} of member
     * @return {@link List < CheckIn > list of checkins}
     */
    @Get("/{?checkinid,createdbyid}")
    public Set<AgendaItem> findAgendaItems(@Nullable UUID checkinid,
                                           @Nullable UUID createdbyid) {
        return agendaItemServices.findByFields(checkinid, createdbyid);
    }

    /**
     * Load agenda items
     *
     * @param agendaItems, {@link List< AgendaItemCreateDTO > to load {@link AgendaItem agenda items}}
     * @return {@link HttpResponse<List< AgendaItem >}
     */
    @Post("/items")
    public HttpResponse<?> loadAgendaItems(@Body @Valid @NotNull List<AgendaItemCreateDTO> agendaItems,
                                           HttpRequest<List<AgendaItem>> request) {
        List<String> errors = new ArrayList<>();
        List<AgendaItem> agendaItemsCreated = new ArrayList<>();
        for (AgendaItemCreateDTO agendaItemDTO : agendaItems) {
            AgendaItem agendaItem = new AgendaItem(agendaItemDTO.getCheckinid(),
                    agendaItemDTO.getCreatedbyid(), agendaItemDTO.getDescription());
            try {
                agendaItemServices.save(agendaItem);
                agendaItemsCreated.add(agendaItem);
            } catch (AgendaItemBadArgException e) {
                errors.add(String.format("Member %s's agenda item was not added to CheckIn %s because: %s", agendaItem.getCreatedbyid(),
                        agendaItem.getCheckinid(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(agendaItemsCreated)
                    .headers(headers -> headers.location(request.getUri()));
        } else {
            return HttpResponse.badRequest(errors)
                    .headers(headers -> headers.location(request.getUri()));
        }
    }


}

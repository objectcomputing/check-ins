package com.objectcomputing.checkins.services.agenda;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.http.annotation.Produces;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/services/agenda-item")
@Secured(SecurityRule.IS_ANONYMOUS)
// @Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="agenda-item")
public class AgendaItemController {

    protected final AgendaItemRepository agendaItemRepository;

    public AgendaItemController(AgendaItemRepository agendaItemRepository){
        this.agendaItemRepository = agendaItemRepository;
    }
    /**
     * Find Agenda item by checkinId or find all.
     * @param checkinId
     * @return
     */
    @Get("/{?checkinId}")
    public List<AgendaItem> findByValue(@Nullable UUID checkinId) {

        if(checkinId != null) {
            return agendaItemRepository.findByCheckinId(checkinId);
        } else {
            return agendaItemRepository.findAll();
        }
    }

    /**
     * Save a new Agenda item.
     * @param agendaItems
     * @return
     */
    @Post("/")
    // @Secured("VIEW")

    public HttpResponse<AgendaItem> save(@Body @Valid AgendaItem agendaItem) {
        AgendaItem newAgendaItem = agendaItemRepository.save(agendaItem);
        
        return HttpResponse
                .created(newAgendaItem)
                .headers(headers -> headers.location(location(newAgendaItem.getUuid())));
    }

    /**
     * Update a Agenda item.
     * @param agendaItem
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid AgendaItem agendaItem) {

        if(null != agendaItem.getUuid()) {
            AgendaItem updatedAgendaItem = agendaItemRepository.update(agendaItem);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedAgendaItem.getUuid())))
                    .body(updatedAgendaItem);
                    
        }
        
        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create("/services/agenda-item" + uuid);
    }
}






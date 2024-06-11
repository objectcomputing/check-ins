package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

import static io.micronaut.http.HttpStatus.CREATED;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "volunteering")
@Controller("/services/volunteer/event")
class VolunteeringEventController {

    private final VolunteeringService volunteeringService;

    VolunteeringEventController(VolunteeringService volunteeringService) {
        this.volunteeringService = volunteeringService;
    }

    /**
     * List all volunteering events.
     * If memberId is provided, restrict to events for that member.
     * If relationshipId is provided, restrict to events for that relationship.
     * If includeInactive is true, include inactive organizations and relationships in the results.
     *
     * @param memberId           the id of the member
     * @param organizationId     the id of the organization
     * @param includeDeactivated whether to include deactivated relationships or organizations
     * @return list of {@link VolunteeringEvent}
     */
    @Get("/{?memberId,organizationId,includeDeactivated}")
    List<VolunteeringEvent> findEvents(@Nullable UUID memberId, @Nullable UUID organizationId, @Nullable Boolean includeDeactivated) {
        return volunteeringService.listEvents(memberId, organizationId, Boolean.TRUE.equals(includeDeactivated));
    }

    /**
     * Create a new volunteering event.
     * Requires you to be the creator of the relationship, or have the {@link Permission#CAN_ADMINISTER_VOLUNTEERING_EVENTS} permission.
     *
     * @param event the event to create
     * @return the created {@link VolunteeringEvent}
     */
    @Post
    @Status(CREATED)
    VolunteeringEvent create(@Valid @Body VolunteeringEventDTO event) {
        return volunteeringService.create(new VolunteeringEvent(
                event.getRelationshipId(),
                event.getEventDate(),
                event.getHours(),
                event.getNotes()
        ));
    }

    /**
     * Update an existing volunteering event.
     * Requires you to be the creator of the relationship, or have the {@link Permission#CAN_ADMINISTER_VOLUNTEERING_EVENTS} permission.
     *
     * @param id           the id of the relationship to update
     * @param event the relationship to update
     * @return the updated {@link VolunteeringEvent}
     */
    @Put("/{id}")
    VolunteeringEvent update(@NotNull UUID id, @Valid @Body VolunteeringEventDTO event) {
        return volunteeringService.update(new VolunteeringEvent(
                id,
                event.getRelationshipId(),
                event.getEventDate(),
                event.getHours(),
                event.getNotes()
        ));
    }

    /**
     * Delete a volunteering event.
     * Requires you to be the creator of the relationship, or have the {@link Permission#CAN_ADMINISTER_VOLUNTEERING_EVENTS} permission.
     *
     * @param id the id of the event to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    void delete(UUID id) {
        volunteeringService.deleteEvent(id);
    }
}

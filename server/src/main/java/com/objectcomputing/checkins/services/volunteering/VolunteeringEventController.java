package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

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
     * If relationshipId is provided, restrict to events for that relationship.
     * If includeInactive is true, include inactive organizations and relationships in the results.
     *
     * @param relationshipId     the id of the relationship
     * @param includeDeactivated whether to include deactivated relationships or organizations
     * @return list of {@link VolunteeringEvent}
     */
    @Get("/{?relationshipId,includeDeactivated}")
    List<VolunteeringEvent> findEvents(@Nullable UUID memberId, @Nullable UUID relationshipId, @Nullable Boolean includeDeactivated) {
        return volunteeringService.listEvents(memberId, relationshipId, Boolean.TRUE.equals(includeDeactivated));
    }
}

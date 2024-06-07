package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.List;
import java.util.UUID;

@Controller("/services/volunteer")
class VolunteeringController {

    private final VolunteeringService volunteeringService;

    public VolunteeringController(VolunteeringService volunteeringService) {
        this.volunteeringService = volunteeringService;
    }

    /**
     * List all volunteering organizations
     *
     * @param includeDeactivated whether to include deactivated organizations
     * @return list of {@link VolunteeringOrganization}
     */
    @Get("/organization/{?includeDeactivated}")
    List<VolunteeringOrganization> findAll(@Nullable Boolean includeDeactivated) {
        return volunteeringService.listOrganizations(Boolean.TRUE.equals(includeDeactivated));
    }

    /**
     * List all volunteering relationships
     * If memberId is provided, restrict to relationships for that member.
     * If organizationId is provided, restrict to relationships for that organization.
     * If includeInactive is true, include inactive relationships and organizations in the results.
     *
     * @param memberId the id of the member
     * @param organizationId the id of the organization
     * @param includeDeactivated whether to include deactivated relationships or organizations
     * @return list of {@link VolunteeringRelationship}
     */
    @Get("/relationship/{?memberId,organizationId,includeDeactivated}")
    List<VolunteeringRelationship> findRelationships(@Nullable UUID memberId, @Nullable UUID organizationId, @Nullable Boolean includeDeactivated) {
        return volunteeringService.listRelationships(memberId, organizationId, Boolean.TRUE.equals(includeDeactivated));
    }

    /**
     * List all volunteering events.
     * If relationshipId is provided, restrict to events for that relationship.
     * If includeInactive is true, include inactive organizations and relationships in the results.
     *
     * @param relationshipId the id of the relationship
     * @param includeDeactivated whether to include deactivated relationships or organizations
     * @return list of {@link VolunteeringEvent}
     */
    @Get("/event/{?relationshipId,includeDeactivated}")
    List<VolunteeringEvent> findEvents(@Nullable UUID memberId, @Nullable UUID relationshipId, @Nullable Boolean includeDeactivated) {
        return volunteeringService.listEvents(memberId, relationshipId, Boolean.TRUE.equals(includeDeactivated));
    }
}

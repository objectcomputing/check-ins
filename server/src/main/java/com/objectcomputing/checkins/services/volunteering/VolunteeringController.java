package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
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
    @Get("/organization{?includeDeactivated}")
    List<VolunteeringOrganization> findAll(@Nullable Boolean includeDeactivated) {
        return volunteeringService.listOrganizations(Boolean.TRUE.equals(includeDeactivated));
    }

    /**
     * Create a new volunteering organization
     *
     * @param organization the organization to create
     * @return the created {@link VolunteeringOrganization}
     */
    @Post("/organization")
    @Status(CREATED)
    @RequiredPermission(Permission.CAN_ADMINISTER_VOLUNTEERING_ORGANIZATIONS)
    VolunteeringOrganization create(@Valid @Body VolunteeringOrganizationDTO organization) {
        return volunteeringService.create(new VolunteeringOrganization(
                organization.getName(),
                organization.getDescription(),
                organization.getWebsite()
        ));
    }

    /**
     * Update an existing volunteering organization
     *
     * @param organization the organization to update
     * @return the updated {@link VolunteeringOrganization}
     */
    @Put("/organization/{id}")
    @RequiredPermission(Permission.CAN_ADMINISTER_VOLUNTEERING_ORGANIZATIONS)
    VolunteeringOrganization update(@NotNull UUID id, @Valid @Body VolunteeringOrganizationDTO organization) {
        return volunteeringService.update(new VolunteeringOrganization(
                id,
                organization.getName(),
                organization.getDescription(),
                organization.getWebsite(),
                Boolean.TRUE.equals(organization.getActive())
        ));
    }

    /**
     * Create a new volunteering relationship
     *
     * @param relationship the relationship to create
     *                     @return the created {@link VolunteeringRelationship}
     */
    @Post("/relationship")
    VolunteeringRelationship create(@Valid @Body VolunteeringRelationshipDTO relationship) {
        return volunteeringService.create(new VolunteeringRelationship(
                relationship.getMemberId(),
                relationship.getOrganizationId(),
                relationship.getStartDate(),
                relationship.getEndDate()
        ));
    }

    /**
     * Update an existing volunteering relationship
     *
     * @param relationship the relationship to update
     * @return the updated {@link VolunteeringRelationship}
     */
    @Put("/relationship/{id}")
    VolunteeringRelationship update(@NotNull UUID id, @Valid @Body VolunteeringRelationshipDTO relationship) {
        return volunteeringService.update(new VolunteeringRelationship(
                id,
                relationship.getMemberId(),
                relationship.getOrganizationId(),
                relationship.getStartDate(),
                relationship.getEndDate(),
                Boolean.TRUE.equals(relationship.getActive())
        ));
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

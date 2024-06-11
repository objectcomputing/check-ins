package com.objectcomputing.checkins.services.volunteering;

import com.objectcomputing.checkins.services.permissions.Permission;
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
@Controller("/services/volunteer/relationship")
class VolunteeringRelationshipController {

    private final VolunteeringService volunteeringService;

    VolunteeringRelationshipController(VolunteeringService volunteeringService) {
        this.volunteeringService = volunteeringService;
    }

    /**
     * List all volunteering relationships
     * If memberId is provided, restrict to relationships for that member.
     * If organizationId is provided, restrict to relationships for that organization.
     * If includeInactive is true, include inactive relationships and organizations in the results.
     *
     * @param memberId           the id of the member
     * @param organizationId     the id of the organization
     * @param includeDeactivated whether to include deactivated relationships or organizations
     * @return list of {@link VolunteeringRelationship}
     */
    @Get("/{?memberId,organizationId,includeDeactivated}")
    List<VolunteeringRelationship> findRelationships(@Nullable UUID memberId, @Nullable UUID organizationId, @Nullable Boolean includeDeactivated) {
        return volunteeringService.listRelationships(memberId, organizationId, Boolean.TRUE.equals(includeDeactivated));
    }

    /**
     * Create a new volunteering relationship.
     *
     * @param relationship the relationship to create
     * @return the created {@link VolunteeringRelationship}
     */
    @Post
    @Status(CREATED)
    VolunteeringRelationship create(@Valid @Body VolunteeringRelationshipDTO relationship) {
        return volunteeringService.create(new VolunteeringRelationship(
                relationship.getMemberId(),
                relationship.getOrganizationId(),
                relationship.getStartDate(),
                relationship.getEndDate()
        ));
    }

    /**
     * Update an existing volunteering relationship.
     * Requires you to be the creator of the relationship, or the {@link Permission#CAN_ADMINISTER_VOLUNTEERING_RELATIONSHIPS} permission.
     *
     * @param id           the id of the relationship to update
     * @param relationship the relationship to update
     * @return the updated {@link VolunteeringRelationship}
     */
    @Put("/{id}")
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
}

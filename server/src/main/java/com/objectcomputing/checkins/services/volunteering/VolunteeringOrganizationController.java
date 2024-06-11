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
@Controller("/services/volunteer/organization")
class VolunteeringOrganizationController {

    private final VolunteeringService volunteeringService;

    VolunteeringOrganizationController(VolunteeringService volunteeringService) {
        this.volunteeringService = volunteeringService;
    }

    /**
     * List all volunteering organizations.
     *
     * @param includeDeactivated whether to include deactivated organizations
     * @return list of {@link VolunteeringOrganization}
     */
    @Get("/{?includeDeactivated}")
    List<VolunteeringOrganization> findAll(@Nullable Boolean includeDeactivated) {
        return volunteeringService.listOrganizations(Boolean.TRUE.equals(includeDeactivated));
    }

    /**
     * Create a new volunteering organization.
     *
     * @param organization the organization to create
     * @return the created {@link VolunteeringOrganization}
     */
    @Post
    @Status(CREATED)
    VolunteeringOrganization create(@Valid @Body VolunteeringOrganizationDTO organization) {
        return volunteeringService.create(new VolunteeringOrganization(
                organization.getName(),
                organization.getDescription(),
                organization.getWebsite()
        ));
    }

    /**
     * Update an existing volunteering organization.
     * Requires the {@link Permission#CAN_ADMINISTER_VOLUNTEERING_ORGANIZATIONS} permission.
     *
     * @param id           the id of the organization to update
     * @param organization the organization to update
     * @return the updated {@link VolunteeringOrganization}
     */
    @Put("/{id}")
//    @RequiredPermission(Permission.CAN_ADMINISTER_VOLUNTEERING_ORGANIZATIONS)
    VolunteeringOrganization update(@NotNull UUID id, @Valid @Body VolunteeringOrganizationDTO organization) {
        return volunteeringService.update(new VolunteeringOrganization(
                id,
                organization.getName(),
                organization.getDescription(),
                organization.getWebsite(),
                Boolean.TRUE.equals(organization.getActive())
        ));
    }
}

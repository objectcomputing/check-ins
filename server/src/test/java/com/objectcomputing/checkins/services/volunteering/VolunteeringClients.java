package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.annotation.Client;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

class VolunteeringClients {

    @Client("/services/volunteer/organization")
    @Requires(property = VolunteeringClients.Organization.ENABLED, value = "true")
    interface Organization {

        String ENABLED = "enable.volunteering.organization.client";

        @Get("/")
        List<VolunteeringOrganization> list(@Header String authorization);

        @Get("/{?includeDeactivated}")
        List<VolunteeringOrganization> list(@Header String authorization, @Nullable Boolean includeDeactivated);

        @Post
        HttpResponse<VolunteeringOrganization> createOrganization(@Header String authorization, @Body VolunteeringOrganizationDTO organization);

        @Put("/{id}")
        VolunteeringOrganization updateOrganization(@Header String authorization, @NotNull UUID id, @Body VolunteeringOrganizationDTO organization);
    }

    @Client("/services/volunteer/relationship")
    @Requires(property = VolunteeringClients.Relationship.ENABLED, value = "true")
    interface Relationship {

        String ENABLED = "enable.volunteering.relationship.client";

        @Get("/{?memberId,organizationId,includeDeactivated}")
        List<VolunteeringRelationship> list(@Header String authorization, @Nullable UUID memberId, @Nullable UUID organizationId, @Nullable Boolean includeDeactivated);

        @Post
        HttpResponse<VolunteeringRelationship> create(@Header String authorization, @Body VolunteeringRelationshipDTO relationship);

        @Put("/{id}")
        VolunteeringRelationship update(@Header String authorization, @NotNull UUID id, @Body VolunteeringRelationshipDTO relationship);
    }
}

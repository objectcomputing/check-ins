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

@Client("/services/volunteer/organization")
@Requires(property = VolunteeringOrganizationClient.ENABLED, value = "true")
public interface VolunteeringOrganizationClient {

    String ENABLED = "enable.volunteering.organization.client";

    @Get("/")
    List<VolunteeringOrganization> getAllOrganizations(@Header String authorization);

    @Get("/{?includeDeactivated}")
    List<VolunteeringOrganization> getAllOrganizations(@Header String authorization, @Nullable Boolean includeDeactivated);

    @Post
    HttpResponse<VolunteeringOrganization> createOrganization(@Header String authorization, @Body VolunteeringOrganizationDTO organization);

    @Put("/{id}")
    VolunteeringOrganization updateOrganization(@Header String authorization, @NotNull UUID id, @Body VolunteeringOrganizationDTO organization);
}

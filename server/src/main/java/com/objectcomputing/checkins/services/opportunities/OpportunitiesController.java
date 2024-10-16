package com.objectcomputing.checkins.services.opportunities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
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

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/services/opportunities")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "opportunities")
public class OpportunitiesController {

    private final OpportunitiesService opportunitiesResponseServices;

    public OpportunitiesController(OpportunitiesService opportunitiesResponseServices) {
        this.opportunitiesResponseServices = opportunitiesResponseServices;
    }

    /**
     * Find opportunities by Name or Description or submittedBy.
     *
     * @param name        {@link String}
     * @param description {@link String}
     * @param submittedBy {@link UUID} of member
     * @return list of opportunities
     */
    @Get("/{?name,description,submittedBy}")
    public List<Opportunities> findOpportunities(@Nullable String name,
                                                 @Nullable String description, @Nullable UUID submittedBy) {
        return opportunitiesResponseServices.findByFields(name, description, submittedBy);
    }

    /**
     * Create and save a new Opportunity.
     *
     * @param opportunitiesResponse, {@link OpportunitiesCreateDTO}
     * @return {@link HttpResponse<Opportunities>}
     */

    @Post
    public HttpResponse<Opportunities> createOpportunities(@Body @Valid OpportunitiesCreateDTO opportunitiesResponse,
                                                           HttpRequest<?> request) {
        Opportunities opportunities = opportunitiesResponseServices.save(new Opportunities(opportunitiesResponse.getName(),
                opportunitiesResponse.getDescription(), opportunitiesResponse.getUrl(),
                opportunitiesResponse.getExpiresOn(), opportunitiesResponse.getPending()));
        return HttpResponse.created(opportunities)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), opportunities.getId()))));
    }

    /**
     * Update an Opportunity
     *
     * @param opportunitiesResponse, {@link Opportunities}
     * @return {@link HttpResponse<Opportunities>}
     */
    @Put
    public HttpResponse<Opportunities> update(@Body @Valid @NotNull Opportunities opportunitiesResponse,
                                              HttpRequest<?> request) {
        Opportunities updatedOpportunities = opportunitiesResponseServices.update(opportunitiesResponse);
        return HttpResponse.ok(updatedOpportunities)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedOpportunities.getId()))));

    }

    /**
     * Delete an opportunity
     *
     * @param id, id of {@link Opportunities} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteOpportunities(@NotNull UUID id) {
        opportunitiesResponseServices.delete(id);
    }
}

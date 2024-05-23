package com.objectcomputing.checkins.services.opportunities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Controller("/services/opportunities")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="opportunities")

public class OpportunitiesController {

    private final OpportunitiesService opportunitiesResponseServices;

    public OpportunitiesController(OpportunitiesService opportunitiesResponseServices) {
        this.opportunitiesResponseServices = opportunitiesResponseServices;
    }

    /**
     * Find opportunities by Name or Description or submittedBy.
     *
     * @param name {@link String}
     * @param description {@link String}
     * @param submittedBy {@link UUID} of member
     * @return {@link Set <Opportunities > list of opportunities
     */
    @Get("/{?name,description,submittedBy}")
    public Mono<HttpResponse<List<Opportunities>>> findOpportunities(@Nullable String name,
                                                                     @Nullable String description, @Nullable UUID submittedBy) {
        return Mono.fromCallable(() -> opportunitiesResponseServices.findByFields(name, description, submittedBy))
                .map(opportunities -> HttpResponse.ok(new ArrayList<>(opportunities)));
    }

    /**
     * Create and save a new Opportunity.
     *
     * @param opportunitiesResponse, {@link OpportunitiesCreateDTO}
     * @return {@link HttpResponse<Opportunities>}
     */

    @Post()
    public Mono<HttpResponse<Opportunities>> createOpportunities(@Body @Valid OpportunitiesCreateDTO opportunitiesResponse,
                                                     HttpRequest<?> request) {
        return Mono.fromCallable(() -> opportunitiesResponseServices.save(new Opportunities(opportunitiesResponse.getName(),
                        opportunitiesResponse.getDescription(), opportunitiesResponse.getUrl(),
                        opportunitiesResponse.getExpiresOn(),opportunitiesResponse.getPending())))
                .map(opportunities -> HttpResponse.created(opportunities)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), opportunities.getId())))));
    }

    /**
     * Update an Opportunity
     *
     * @param opportunitiesResponse, {@link Opportunities}
     * @return {@link HttpResponse<Opportunities>}
     */
    @Put()
    public Mono<HttpResponse<Opportunities>> update(@Body @Valid @NotNull Opportunities opportunitiesResponse,
                                               HttpRequest<?> request) {
        return Mono.fromCallable(() -> opportunitiesResponseServices.update(opportunitiesResponse))
                .map(updatedOpportunities -> HttpResponse.ok(updatedOpportunities)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedOpportunities.getId())))));

    }

    /**
     * Delete an opportunity
     *
     * @param id, id of {@link Opportunities} to delete
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteOpportunities(@NotNull UUID id) {
        return Mono.fromRunnable(() -> opportunitiesResponseServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }
}

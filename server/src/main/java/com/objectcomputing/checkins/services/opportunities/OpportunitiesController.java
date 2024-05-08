package com.objectcomputing.checkins.services.opportunities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


@Controller("/services/opportunities")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="opportunities")

public class OpportunitiesController {

    private final OpportunitiesService opportunitiesResponseServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public OpportunitiesController(OpportunitiesService opportunitiesResponseServices,
                            EventLoopGroup eventLoopGroup,
                            @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.opportunitiesResponseServices = opportunitiesResponseServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(opportunities -> { List<Opportunities>  opportunity = new ArrayList<>(opportunities);
                   return (HttpResponse<List<Opportunities>>) HttpResponse.ok(opportunity);})
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
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
        return Mono.fromCallable(() -> opportunitiesResponseServices.save(new Opportunities(opportunitiesResponse.getName(), opportunitiesResponse.getDescription(), opportunitiesResponse.getUrl(), opportunitiesResponse.getExpiresOn(),opportunitiesResponse.getPending())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(opportunities -> (HttpResponse<Opportunities>) HttpResponse
                        .created(opportunities)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), opportunities.getId()))))).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedOpportunities -> (HttpResponse<Opportunities>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedOpportunities.getId()))))
                        .body(updatedOpportunities))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Delete an opportunity
     *
     * @param id, id of {@link Opportunities} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteOpportunities(@NotNull UUID id) {
        opportunitiesResponseServices.delete(id); // todo matt blocking
        return HttpResponse
                .ok();
    }
}

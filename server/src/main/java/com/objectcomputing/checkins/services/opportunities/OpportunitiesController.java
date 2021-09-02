package com.objectcomputing.checkins.services.opportunities;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


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
     * @return {@link Set < Opportunities > list of opportunities}
     */
    @Get("/{?name,description,submittedBy}")
    public Single<HttpResponse<List<Opportunities>>> findOpportunities(@Nullable String name,
                                                                       @Nullable String description,@Nullable UUID submittedBy) {
        return Single.fromCallable(() -> opportunitiesResponseServices.findByFields(name, description, submittedBy))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(opportunities -> { List<Opportunities>  opportunity = opportunities.stream().collect(Collectors.toList());
                   return (HttpResponse<List<Opportunities>>) HttpResponse.ok(opportunity);})
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Create and save a new Opportunity.
     *
     * @param opportunitiesResponse, {@link OpportunitiesCreateDTO}
     * @return {@link HttpResponse<Opportunities>}
     */

    @Post()
    public Single<HttpResponse<Opportunities>> createOpportunities(@Body @Valid OpportunitiesCreateDTO opportunitiesResponse,
                                                     HttpRequest<OpportunitiesCreateDTO> request) {
        return Single.fromCallable(() -> opportunitiesResponseServices.save(new Opportunities(opportunitiesResponse.getName(), opportunitiesResponse.getDescription(), opportunitiesResponse.getUrl(), opportunitiesResponse.getExpiresOn(),opportunitiesResponse.getPending())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(opportunities -> {return (HttpResponse<Opportunities>) HttpResponse
                        .created(opportunities)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), opportunities.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update an Opportunity
     *
     * @param opportunitiesResponse, {@link Opportunities}
     * @return {@link HttpResponse<Opportunities>}
     */
    @Put()
    public Single<HttpResponse<Opportunities>> update(@Body @Valid @NotNull Opportunities opportunitiesResponse,
                                               HttpRequest<Opportunities> request) {
        return Single.fromCallable(() -> opportunitiesResponseServices.update(opportunitiesResponse))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedOpportunities -> (HttpResponse<Opportunities>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedOpportunities.getId()))))
                        .body(updatedOpportunities))
                .subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Delete an opportunity
     *
     * @param id, id of {@link Opportunities} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteOpportunities(@NotNull UUID id) {
        opportunitiesResponseServices.delete(id);
        return HttpResponse
                .ok();
    }
}

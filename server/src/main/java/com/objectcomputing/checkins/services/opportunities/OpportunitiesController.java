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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


@Controller("/services/opportunitiess")
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
     * Find opportunities by Name, Team Member or Date Range.
     *
     * @param name
     * @param submittedBy
     * @return
     */
    @Get("/{?name,submittedBy}")
    public Single<HttpResponse<Set<Opportunities>>> findOpportunitiess(@Nullable String name,
                                                         @Nullable UUID submittedBy) {
        return Single.fromCallable(() -> {
            if (name!=null || submittedBy!=null) {
                return opportunitiesResponseServices.findByFields(name, submittedBy);
            } else {
                return opportunitiesResponseServices.readAll();
            }
        })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(opportunities -> (HttpResponse<Set<Opportunities>>) HttpResponse.ok(opportunities))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Create and save a new Opportunities.
     *
     * @param opportunitiesResponse, {@link OpportunitiesCreateDTO}
     * @return {@link HttpResponse<Opportunities>}
     */

    @Post()
    public Single<HttpResponse<Opportunities>> createOpportunities(@Body @Valid OpportunitiesCreateDTO opportunitiesResponse,
                                                     HttpRequest<OpportunitiesCreateDTO> request) {
        return Single.fromCallable(() -> opportunitiesResponseServices.save(new Opportunities(opportunitiesResponse.getSubmittedOn(), opportunitiesResponse.getSubmittedBy(), opportunitiesResponse.getName(), opportunitiesResponse.getDescription())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(opportunities -> {return (HttpResponse<Opportunities>) HttpResponse
                        .created(opportunities)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), opportunities.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update a Opportunities
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
     * Delete A opportunities
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

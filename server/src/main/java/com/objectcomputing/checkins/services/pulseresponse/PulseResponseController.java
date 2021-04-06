package com.objectcomputing.checkins.services.pulseresponse;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Put;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.time.LocalDate;
import io.micronaut.core.convert.format.Format;

import javax.inject.Named;
import java.util.concurrent.ExecutorService;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.micronaut.scheduling.TaskExecutors;

@Controller("/services/pulse-response")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="pulse-response")

public class PulseResponseController {

    private final PulseResponseService pulseResponseServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public PulseResponseController(PulseResponseService pulseResponseServices,
                                   EventLoopGroup eventLoopGroup,
                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.pulseResponseServices = pulseResponseServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Find Pulse Response by Team Member or Date Range.
     * 
     * @param teamMemberId
     * @param dateFrom
     * @param dateTo
     * @return
     */
    @Get("/{?teamMemberId,dateFrom,dateTo}")
    public Single<HttpResponse<Set<PulseResponse>>> findPulseResponses(@Nullable @Format("yyyy-MM-dd") LocalDate dateFrom,
                                                                       @Nullable @Format("yyyy-MM-dd") LocalDate dateTo,
                                                                       @Nullable UUID teamMemberId) {
        return Single.fromCallable(() -> pulseResponseServices.findByFields(teamMemberId, dateFrom, dateTo))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(pulseresponse -> (HttpResponse<Set<PulseResponse>>) HttpResponse.ok(pulseresponse))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

     /**
     * Create and save a new PulseResponse.
     *
     * @param pulseResponse, {@link PulseResponseCreateDTO}
     * @return {@link HttpResponse<PulseResponse>}
     */

    @Post()
    public Single<HttpResponse<PulseResponse>> createPulseResponse(@Body @Valid PulseResponseCreateDTO pulseResponse,
                                                                    HttpRequest<PulseResponseCreateDTO> request) {
        return Single.fromCallable(() -> pulseResponseServices.save(new PulseResponse(pulseResponse.getSubmissionDate(),pulseResponse.getUpdatedDate(), pulseResponse.getTeamMemberId(), pulseResponse.getInternalFeelings(), pulseResponse.getExternalFeelings())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(pulseresponse -> {return (HttpResponse<PulseResponse>) HttpResponse
                    .created(pulseresponse)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), pulseresponse.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

     /**
     * Update a PulseResponse
     *
     * @param pulseResponse, {@link PulseResponse}
     * @return {@link HttpResponse<PulseResponse>}
     */
    @Put()
    public Single<HttpResponse<PulseResponse>> update(@Body @Valid @NotNull PulseResponse pulseResponse,
                                                      HttpRequest<PulseResponse> request) {
        return Single.fromCallable(() -> pulseResponseServices.update(pulseResponse))
            .observeOn(Schedulers.from(eventLoopGroup))
            .map(updatedPulseResponse -> (HttpResponse<PulseResponse>) HttpResponse
                    .ok()
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedPulseResponse.getId()))))
                    .body(updatedPulseResponse))
            .subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * 
     * @param id
     * @return
     */
    @Get("/{id}")
    public Single<HttpResponse<PulseResponse>> readRole(@NotNull UUID id) {
        return Single.fromCallable(() -> {
            PulseResponse result = pulseResponseServices.read(id);
            if (result == null) {
                throw new NotFoundException("No role item for UUID");
            }
            return result;
        })
        .observeOn(Schedulers.from(eventLoopGroup))
        .map(pulseresponse -> {
            return (HttpResponse<PulseResponse>)HttpResponse.ok(pulseresponse);
        }).subscribeOn(Schedulers.from(ioExecutorService));

    }
}
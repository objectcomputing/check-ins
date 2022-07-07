package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/pulse-responses")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="pulse-responses")
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
    @RequiredPermission(Permissions.CAN_VIEW_PULSE_RESPONSE)
    @Get("/{?teamMemberId,dateFrom,dateTo}")
    public Mono<HttpResponse<Set<PulseResponse>>> findPulseResponses(@Nullable @Format("yyyy-MM-dd") LocalDate dateFrom,
                                                                       @Nullable @Format("yyyy-MM-dd") LocalDate dateTo,
                                                                       @Nullable UUID teamMemberId) {
        return Mono.fromCallable(() -> pulseResponseServices.findByFields(teamMemberId, dateFrom, dateTo))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(pulseresponse -> (HttpResponse<Set<PulseResponse>>) HttpResponse.ok(pulseresponse))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

     /**
     * Create and save a new PulseResponse.
     *
     * @param pulseResponse, {@link PulseResponseCreateDTO}
     * @return {@link HttpResponse<PulseResponse>}
     */
    @RequiredPermission(Permissions.CAN_CREATE_PULSE_RESPONSE)
    @Post()
    public Mono<HttpResponse<PulseResponse>> createPulseResponse(@Body @Valid PulseResponseCreateDTO pulseResponse,
                                                                    HttpRequest<PulseResponseCreateDTO> request) {
        return Mono.fromCallable(() -> pulseResponseServices.save(new PulseResponse(pulseResponse.getSubmissionDate(),pulseResponse.getUpdatedDate(), pulseResponse.getTeamMemberId(), pulseResponse.getInternalFeelings(), pulseResponse.getExternalFeelings())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(pulseresponse -> {return (HttpResponse<PulseResponse>) HttpResponse
                    .created(pulseresponse)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), pulseresponse.getId()))));
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

     /**
     * Update a PulseResponse
     *
     * @param pulseResponse, {@link PulseResponse}
     * @return {@link HttpResponse<PulseResponse>}
     */
    @Put()
    public Mono<HttpResponse<PulseResponse>> update(@Body @Valid @NotNull PulseResponse pulseResponse,
                                                      HttpRequest<PulseResponse> request) {
        return Mono.fromCallable(() -> pulseResponseServices.update(pulseResponse))
            .publishOn(Schedulers.fromExecutor(eventLoopGroup))
            .map(updatedPulseResponse -> (HttpResponse<PulseResponse>) HttpResponse
                    .ok()
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedPulseResponse.getId()))))
                    .body(updatedPulseResponse))
            .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * 
     * @param id
     * @return
     */
    @Get("/{id}")
    public Mono<HttpResponse<PulseResponse>> readRole(@NotNull UUID id) {
        return Mono.fromCallable(() -> {
            PulseResponse result = pulseResponseServices.read(id);
            if (result == null) {
                throw new NotFoundException("No role item for UUID");
            }
            return result;
        })
        .publishOn(Schedulers.fromExecutor(eventLoopGroup))
        .map(pulseresponse -> {
            return (HttpResponse<PulseResponse>)HttpResponse.ok(pulseresponse);
        }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }
}
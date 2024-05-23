package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
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
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Controller("/services/pulse-responses")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="pulse-responses")
public class PulseResponseController {

    private final PulseResponseService pulseResponseServices;

    public PulseResponseController(PulseResponseService pulseResponseServices) {
        this.pulseResponseServices = pulseResponseServices;
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
    public Mono<HttpResponse<Set<PulseResponse>>> findPulseResponses(@Nullable @Format("yyyy-MM-dd") LocalDate dateFrom,
                                                                       @Nullable @Format("yyyy-MM-dd") LocalDate dateTo,
                                                                       @Nullable UUID teamMemberId) {
        return Mono.fromCallable(() -> pulseResponseServices.findByFields(teamMemberId, dateFrom, dateTo))
                .map(HttpResponse::ok);
    }

     /**
     * Create and save a new PulseResponse.
     *
     * @param pulseResponse, {@link PulseResponseCreateDTO}
     * @return {@link HttpResponse<PulseResponse>}
     */

    @Post
    public Mono<HttpResponse<PulseResponse>> createPulseResponse(@Body @Valid PulseResponseCreateDTO pulseResponse,
                                                                    HttpRequest<?> request) {
        return Mono.fromCallable(() -> pulseResponseServices.save(new PulseResponse(pulseResponse.getInternalScore(), pulseResponse.getExternalScore(), pulseResponse.getSubmissionDate(), pulseResponse.getTeamMemberId(), pulseResponse.getInternalFeelings(), pulseResponse.getExternalFeelings())))
                .map(pulseresponse -> HttpResponse.created(pulseresponse)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), pulseresponse.getId()))))
                );
    }

     /**
     * Update a PulseResponse
     *
     * @param pulseResponse, {@link PulseResponse}
     * @return {@link HttpResponse<PulseResponse>}
     */
    @Put
    public Mono<HttpResponse<PulseResponse>> update(@Body @Valid @NotNull PulseResponse pulseResponse,
                                                      HttpRequest<?> request) {
        return Mono.fromCallable(() -> pulseResponseServices.update(pulseResponse))
            .map(updatedPulseResponse -> HttpResponse.ok(updatedPulseResponse)
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedPulseResponse.getId())))));

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
        }).map(HttpResponse::ok);

    }
}
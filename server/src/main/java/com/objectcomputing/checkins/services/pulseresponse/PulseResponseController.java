package com.objectcomputing.checkins.services.pulseresponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;

import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/pulse-response")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "pulse response")
public class PulseResponseController {

    @Inject
    PulseResponseService pulseResponseService;

    /**
     * Find Pulse Response by Team Member or Date Range.
     * 
     * @param dateFrom
     * @param dateTo
     * @param teamMemberId
     * @return
     */
    @Get("/{?dateFrom,dateTo,teamMemberId}")
    public HttpResponse<List<PulseResponse>> findByValue(@Nullable @Format("yyyy-MM-dd") LocalDate dateFrom,
            @Nullable @Format("yyyy-MM-dd") LocalDate dateTo, @Nullable UUID teamMemberId) {

        return pulseResponseService.findBy(dateFrom, dateTo, teamMemberId);
    }

    /**
     * Save a new Pulse Response.
     * @param pulseResponse
     * @return
     */
    @Post("/")
    public HttpResponse<PulseResponse> save(@Body @Valid PulseResponse pulseResponse) {
        return pulseResponseService.save(pulseResponse);
    }

    /**
     * Update a Pulse Response.
     * @param pulseResponse
     * @return
     */
    @Put("/")
    public HttpResponse<PulseResponse> update(@Body @Valid PulseResponse pulseResponse) {
        return pulseResponseService.update(pulseResponse);
    }
}
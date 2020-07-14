package com.objectcomputing.checkins.services.pulseresponse;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
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

    protected final PulseResponseRepository pulseResponseRepository;

    public PulseResponseController(PulseResponseRepository pulseResponseRepository) {
        this.pulseResponseRepository = pulseResponseRepository;
    }

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

        if(teamMemberId != null) {
            return HttpResponse
                    .ok()
                    .body(pulseResponseRepository.findByTeamMemberId(teamMemberId));
        } else if(dateFrom != null && dateTo != null) {
            return HttpResponse
                    .ok()
                    .body(pulseResponseRepository.findBySubmissionDateBetween(dateFrom, dateTo));
        }

        return HttpResponse.badRequest();
    }

    /**
     * Save a new Pulse Response.
     * @param pulseResponse
     * @return
     */
    @Post("/")
    public HttpResponse<PulseResponse> save(@Body @Valid PulseResponse pulseResponse) {
        PulseResponse newPulseResponse = pulseResponseRepository.save(pulseResponse);
        
        return HttpResponse
                .created(newPulseResponse)
                .headers(headers -> headers.location(location(newPulseResponse.getId())));
    }

    /**
     * Update a Pulse Response.
     * @param pulseResponse
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid PulseResponse pulseResponse) {

        if(null != pulseResponse.getId()) {
            PulseResponse updatedPulseResponse = pulseResponseRepository.update(pulseResponse);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedPulseResponse.getId())))
                    .body(updatedPulseResponse);
                    
        }
        
        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create("/pulse-response/" + uuid);
    }
}
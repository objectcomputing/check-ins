package com.objectcomputing.checkins.services.pulseresponse;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import io.micronaut.http.annotation.Error;
import java.time.LocalDate;
import io.micronaut.core.convert.format.Format;

@Controller("/services/pulse-response")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="pulse-response")
public class PulseResponseController {
    
    @Inject
    PulseResponseService pulseResponseServices ;

    @Error(exception = PulseResponseBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, PulseResponseBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
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
    public Set<PulseResponse> findByValue(@Nullable @Format("yyyy-MM-dd") LocalDate dateFrom, @Nullable @Format("yyyy-MM-dd") LocalDate dateTo,@Nullable UUID teamMemberId) {          
            return pulseResponseServices.findByFields(teamMemberId, dateFrom, dateTo);
    }

    /**
     * Create and save a new PulseResponse.
     *
     * @param pulseResponse, {@link PulseResponseCreateDTO}
     * @return {@link HttpResponse<PulseResponse>}
     */

    @Post(value = "/")
    public HttpResponse<PulseResponse> createAPulseResponse(@Body @Valid PulseResponseCreateDTO pulseResponse,
                                                                HttpRequest<PulseResponseCreateDTO> request) {
        PulseResponse newMemberPulseResponse = pulseResponseServices.save(new PulseResponse(pulseResponse.getSubmissionDate(),pulseResponse.getUpdatedDate(), pulseResponse.getTeamMemberId(), pulseResponse.getInternalFeelings(), pulseResponse.getExternalFeelings()));
        return HttpResponse
                .created(newMemberPulseResponse)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), newMemberPulseResponse.getId()))));
    }

    /**
     * Update pulseresponse details
     * @param pulseResponse
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid PulseResponse pulseResponse,HttpRequest<PulseResponseCreateDTO> request) {

            PulseResponse updatedMemberPulseResponse = pulseResponseServices.update(pulseResponse);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(),updatedMemberPulseResponse.getId()))))
                    .body(updatedMemberPulseResponse);                  
    }

    /**
     * 
     * @param id
     * @return
     */
    @Get("/{id}")
    public PulseResponse readPulseResponse(@NotNull UUID id){
        return pulseResponseServices.read(id);
    }
}
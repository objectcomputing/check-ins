package com.objectcomputing.checkins.services.pulseresponse;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/services/pulse-response")
@Secured({RoleType.Constants.ADMIN_ROLE, RoleType.Constants.PDL_ROLE})
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "pulse response")
public class PulseResponseController {

    @Inject
    PulseResponseService pulseResponseServices;

    @Error(exception = PulseResponseBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, PulseResponseBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Find PulseResponse(s) based on teamMemberId
     *
     * @param teamMemberId
     * @return {@link List<PulseResponse> list of PulseResponse(s) associated with the teamMemberId}
     */

    @Get("/{?teamMemberId}")
    public Set<PulseResponse> findPulseResponses(@Nullable UUID teamMemberId) {
        return pulseResponseServices.read(teamMemberId);
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
        PulseResponse newPulseResponse = pulseResponseServices.save(new PulseResponse(pulseResponse.getSubmissionDate(),pulseResponse.getUpdatedDate(), pulseResponse.getTeamMemberId(), pulseResponse.getInternalFeelings(), pulseResponse.getExternalFeelings()));
        return HttpResponse
                .created(newPulseResponse)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), newPulseResponse.getId()))));
    }

    /**
     * Update a PulseResponse
     *
     * @param pulseResponse, {@link PulseResponse}
     * @return {@link HttpResponse<PulseResponse>}
     */
    @Put("/")
    public HttpResponse<PulseResponse> update(@Body @Valid PulseResponse pulseResponse, HttpRequest<PulseResponse> request) {
        PulseResponse updatedPulseResponse = pulseResponseServices.update(pulseResponse);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), updatedPulseResponse.getId()))))
                .body(updatedPulseResponse);
    }

    /**
     * Delete a PulseResponse
     *
     * @param teamMemberId, id of the pulseresponse record you wish to delete
     * @return {@link HttpResponse<?>}
     */
    @Delete("/{teamMemberId}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public HttpResponse<?> delete(UUID teamMemberId) {
        pulseResponseServices.delete(teamMemberId);
        return HttpResponse
                .noContent();
    }
}
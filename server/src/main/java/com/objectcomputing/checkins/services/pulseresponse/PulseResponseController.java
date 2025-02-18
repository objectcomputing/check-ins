package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.slack.SlackSubmissionHandler;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import io.micronaut.http.MediaType;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Controller("/services/pulse-responses")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "pulse-responses")
public class PulseResponseController {
    private static final Logger LOG = LoggerFactory.getLogger(PulseResponseController.class);

    private final PulseResponseService pulseResponseServices;
    private final MemberProfileServices memberProfileServices;
    private final SlackSubmissionHandler slackSubmissionHandler;

    public PulseResponseController(PulseResponseService pulseResponseServices,
                                   MemberProfileServices memberProfileServices,
                                   SlackSubmissionHandler slackSubmissionHandler) {
        this.pulseResponseServices = pulseResponseServices;
        this.memberProfileServices = memberProfileServices;
        this.slackSubmissionHandler = slackSubmissionHandler;
    }

    /**
     * Find Pulse Response by Team Member or Date Range.
     *
     * @param teamMemberId
     * @param dateFrom
     * @param dateTo
     * @return
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/{?teamMemberId,dateFrom,dateTo}")
    public Set<PulseResponse> findPulseResponses(@Nullable @Format("yyyy-MM-dd") LocalDate dateFrom,
                                                 @Nullable @Format("yyyy-MM-dd") LocalDate dateTo,
                                                 @Nullable UUID teamMemberId) {
        return pulseResponseServices.findByFields(teamMemberId, dateFrom, dateTo);
    }

    /**
     * Create and save a new PulseResponse.
     *
     * @param pulseResponse, {@link PulseResponseCreateDTO}
     * @return {@link HttpResponse<PulseResponse>}
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post
    public HttpResponse<PulseResponse> createPulseResponse(@Body @Valid PulseResponseCreateDTO pulseResponse,
                                                           HttpRequest<?> request) {
        PulseResponse pulseresponse = pulseResponseServices.save(new PulseResponse(pulseResponse.getInternalScore(), pulseResponse.getExternalScore(), pulseResponse.getSubmissionDate(), pulseResponse.getTeamMemberId(), pulseResponse.getInternalFeelings(), pulseResponse.getExternalFeelings()));
        return HttpResponse.created(pulseresponse)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), pulseresponse.getId()))));
    }

    /**
     * Update a PulseResponse
     *
     * @param pulseResponse, {@link PulseResponse}
     * @return {@link HttpResponse<PulseResponse>}
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Put
    public HttpResponse<PulseResponse> update(@Body @Valid @NotNull PulseResponse pulseResponse,
                                              HttpRequest<?> request) {
        PulseResponse updatedPulseResponse = pulseResponseServices.update(pulseResponse);
        return HttpResponse.ok(updatedPulseResponse)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedPulseResponse.getId()))));
    }

    /**
     * @param id
     * @return
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/{id}")
    public PulseResponse readPulse(@NotNull UUID id) {
        PulseResponse result = pulseResponseServices.read(id);
        if (result == null) {
            throw new NotFoundException("No pulse item for UUID");
        }
        return result;
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post(uri = "/command", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse commandPulseResponse(
               @Header("X-Slack-Signature") String signature,
               @Header("X-Slack-Request-Timestamp") String timestamp,
               @Body String requestBody) {
        return slackSubmissionHandler.commandResponse(signature,
                                                      timestamp, requestBody);
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post(uri = "/external", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse externalPulseResponse(
               @Header("X-Slack-Signature") String signature,
               @Header("X-Slack-Request-Timestamp") String timestamp,
               @Body String requestBody,
               HttpRequest<?> request) {
        return slackSubmissionHandler.externalResponse(signature, timestamp,
                                                       requestBody, request);
    }
}

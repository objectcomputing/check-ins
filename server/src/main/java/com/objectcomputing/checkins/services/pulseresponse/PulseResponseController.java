package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.util.form.FormUrlEncodedDecoder;
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
import java.util.Map;
import java.nio.charset.StandardCharsets;

@Controller("/services/pulse-responses")
@ExecuteOn(TaskExecutors.BLOCKING)
@Tag(name = "pulse-responses")
public class PulseResponseController {
    private static final Logger LOG = LoggerFactory.getLogger(PulseResponseController.class);

    private final PulseResponseService pulseResponseServices;
    private final MemberProfileServices memberProfileServices;
    private final SlackSignatureVerifier slackSignatureVerifier;
    private final PulseSlackCommand pulseSlackCommand;
    private final SlackPulseResponseConverter slackPulseResponseConverter;

    public PulseResponseController(PulseResponseService pulseResponseServices,
                                   MemberProfileServices memberProfileServices,
                                   SlackSignatureVerifier slackSignatureVerifier,
                                   PulseSlackCommand pulseSlackCommand,
                                   SlackPulseResponseConverter slackPulseResponseConverter) {
        this.pulseResponseServices = pulseResponseServices;
        this.memberProfileServices = memberProfileServices;
        this.slackSignatureVerifier = slackSignatureVerifier;
        this.pulseSlackCommand = pulseSlackCommand;
        this.slackPulseResponseConverter = slackPulseResponseConverter;
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
    public PulseResponse readRole(@NotNull UUID id) {
        PulseResponse result = pulseResponseServices.read(id);
        if (result == null) {
            throw new NotFoundException("No role item for UUID");
        }
        return result;
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post(uri = "/command", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse commandPulseResponse(
               @Header("X-Slack-Signature") String signature,
               @Header("X-Slack-Request-Timestamp") String timestamp,
               @Body String requestBody) {
        // Validate the request
        if (slackSignatureVerifier.verifyRequest(signature,
                                                 timestamp, requestBody)) {
            // Convert the request body to a map of values.
            FormUrlEncodedDecoder formUrlEncodedDecoder = new FormUrlEncodedDecoder();
            Map<String, Object> body =
                formUrlEncodedDecoder.decode(requestBody,
                                             StandardCharsets.UTF_8);

            // Respond to the slack command.
            String triggerId = (String)body.get("trigger_id");
            if (pulseSlackCommand.send(triggerId)) {
                return HttpResponse.ok();
            } else {
                return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return HttpResponse.unauthorized();
        }
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post(uri = "/external", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse externalPulseResponse(
               @Header("X-Slack-Signature") String signature,
               @Header("X-Slack-Request-Timestamp") String timestamp,
               @Body String requestBody,
               HttpRequest<?> request) {
        // DEBUG Only
        LOG.info(requestBody);

        // Validate the request
        if (slackSignatureVerifier.verifyRequest(signature,
                                                 timestamp, requestBody)) {
            // DEBUG Only
            LOG.info("Request has been verified");

            // Convert the request body to a map of values.
            FormUrlEncodedDecoder formUrlEncodedDecoder =
                new FormUrlEncodedDecoder();
            Map<String, Object> body =
                formUrlEncodedDecoder.decode(requestBody,
                                             StandardCharsets.UTF_8);

            final String key = "payload";
            if (body.containsKey(key)) {
                PulseResponseCreateDTO pulseResponseDTO =
                    slackPulseResponseConverter.get(memberProfileServices,
                                                    (String)body.get(key));
                // If we receive a null DTO, that means that this is not the
                // actual submission of the form.  We can just return 200 so
                // that Slack knows to continue without error.
                if (pulseResponseDTO == null) {
                    return HttpResponse.ok();
                }

                // DEBUG Only
                LOG.info("Request has been converted");

                // Create the pulse response
                PulseResponse pulseResponse =
                    pulseResponseServices.unsecureSave(
                        new PulseResponse(
                            pulseResponseDTO.getInternalScore(),
                            pulseResponseDTO.getExternalScore(),
                            pulseResponseDTO.getSubmissionDate(),
                            pulseResponseDTO.getTeamMemberId(),
                            pulseResponseDTO.getInternalFeelings(),
                            pulseResponseDTO.getExternalFeelings()
                        )
                );

                if (pulseResponse == null) {
                    return HttpResponse.status(HttpStatus.CONFLICT,
                                               "Already submitted today");
                } else {
                    return HttpResponse.ok();
                }
            } else {
                return HttpResponse.unprocessableEntity();
            }
        } else {
            return HttpResponse.unauthorized();
        }
    }
}

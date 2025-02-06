package com.objectcomputing.checkins.services.slack;

import com.objectcomputing.checkins.util.form.FormUrlEncodedDecoder;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponse;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseService;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseCreateDTO;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;

import jakarta.inject.Singleton;

import java.util.Map;
import java.nio.charset.StandardCharsets;

@Singleton
public class SlackSubmissionHandler {
    private final PulseResponseService pulseResponseServices;
    private final SlackSignatureVerifier slackSignatureVerifier;
    private final PulseSlackCommand pulseSlackCommand;
    private final SlackPulseResponseConverter slackPulseResponseConverter;

    public SlackSubmissionHandler(PulseResponseService pulseResponseServices,
                                  SlackSignatureVerifier slackSignatureVerifier,
                                  PulseSlackCommand pulseSlackCommand,
                                  SlackPulseResponseConverter slackPulseResponseConverter) {
        this.pulseResponseServices = pulseResponseServices;
        this.slackSignatureVerifier = slackSignatureVerifier;
        this.pulseSlackCommand = pulseSlackCommand;
        this.slackPulseResponseConverter = slackPulseResponseConverter;
    }

    public HttpResponse commandResponse(String signature,
                                        String timestamp,
                                        String requestBody) {
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

    public HttpResponse externalResponse(String signature,
                                         String timestamp,
                                         String requestBody,
                                         HttpRequest<?> request) {
        // Validate the request
        if (slackSignatureVerifier.verifyRequest(signature,
                                                 timestamp, requestBody)) {
            // Convert the request body to a map of values.
            FormUrlEncodedDecoder formUrlEncodedDecoder =
                new FormUrlEncodedDecoder();
            Map<String, Object> body =
                formUrlEncodedDecoder.decode(requestBody,
                                             StandardCharsets.UTF_8);

            final String key = "payload";
            if (body.containsKey(key)) {
                PulseResponseCreateDTO pulseResponseDTO =
                    slackPulseResponseConverter.get((String)body.get(key));

                // If we receive a null DTO, that means that this is not the
                // actual submission of the form.  We can just return 200 so
                // that Slack knows to continue without error.
                if (pulseResponseDTO == null) {
                    return HttpResponse.ok();
                }

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

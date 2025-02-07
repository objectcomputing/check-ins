package com.objectcomputing.checkins.services.slack;

import com.objectcomputing.checkins.services.slack.pulseresponse.PulseSlackCommand;
import com.objectcomputing.checkins.services.slack.pulseresponse.SlackPulseResponseConverter;
import com.objectcomputing.checkins.services.slack.kudos.SlackKudosResponseHandler;

import com.objectcomputing.checkins.util.form.FormUrlEncodedDecoder;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponse;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseService;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseCreateDTO;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.nio.charset.StandardCharsets;

@Singleton
public class SlackSubmissionHandler {
    private final String typeKey = "type";

    private final PulseResponseService pulseResponseServices;
    private final SlackSignatureVerifier slackSignatureVerifier;
    private final PulseSlackCommand pulseSlackCommand;
    private final SlackPulseResponseConverter slackPulseResponseConverter;
    private final SlackKudosResponseHandler slackKudosResponseHandler;

    public SlackSubmissionHandler(PulseResponseService pulseResponseServices,
                                  SlackSignatureVerifier slackSignatureVerifier,
                                  PulseSlackCommand pulseSlackCommand,
                                  SlackPulseResponseConverter slackPulseResponseConverter,
                                  SlackKudosResponseHandler slackKudosResponseHandler) {
        this.pulseResponseServices = pulseResponseServices;
        this.slackSignatureVerifier = slackSignatureVerifier;
        this.pulseSlackCommand = pulseSlackCommand;
        this.slackPulseResponseConverter = slackPulseResponseConverter;
        this.slackKudosResponseHandler = slackKudosResponseHandler;
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
                try {
                    final ObjectMapper mapper = new ObjectMapper();
                    final Map<String, Object> map =
                            mapper.readValue((String)body.get(key),
                                             new TypeReference<>() {});
                    if (isPulseSubmission(map)) {
                        return completePulse(map);
                    } else if (isKudosSubmission(map)) {
                        return completeKudos(map);
                    }
                } catch(JsonProcessingException ex) {
                    // Fall through to the bottom...
                }
            }
        } else {
            return HttpResponse.unauthorized();
        }

        return HttpResponse.unprocessableEntity();
    }

    private boolean isPulseSubmission(Map<String, Object> map) {
        if (map.containsKey(typeKey)) {
            final String type = (String)map.get(typeKey);
            if (type.equals("view_submission")) {
                final String viewKey = "view";
                if (map.containsKey(viewKey)) {
                    final Map<String, Object> view =
                            (Map<String, Object>)map.get(viewKey);
                    final String callbackKey = "callback_id";
                    if (view.containsKey(callbackKey)) {
                        return "pulseSubmission".equals(view.get(callbackKey));
                    }
                }
            }
        }
        return false;
    }

    private HttpResponse completePulse(Map<String, Object> map) {
        PulseResponseCreateDTO pulseResponseDTO =
                            slackPulseResponseConverter.get(map);

        // If we receive a null DTO, that means that this is not the actual
        // submission of the form.  We can just return 200 so that Slack knows
        // to continue without error.  Realy, this should not happen.  But, just
        // in case...
        if (pulseResponseDTO != null) {
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
                // If pulse response is null, that means that this user has
                // already submitted a response today.
                return HttpResponse.status(HttpStatus.CONFLICT,
                                           "Already submitted today");
            }
        }
        return HttpResponse.ok();
    }

    private boolean isKudosSubmission(Map<String, Object> map) {
        if (map.containsKey(typeKey)) {
            final String type = (String)map.get(typeKey);
            if (type.equals("block_actions")) {
                final String actionKey = "actions";
                return map.containsKey(actionKey);
            }
        }
        return false;
    }

    private HttpResponse completeKudos(Map<String, Object> map) {
        if (slackKudosResponseHandler.handle(map)) {
            return HttpResponse.ok();
        } else {
            // Something was wrong and we were not able to handle this.
            return HttpResponse.unprocessableEntity();
        }
    }
}

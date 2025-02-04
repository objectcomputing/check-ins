package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.notifications.social_media.SlackSearch;

import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.UUID;
import java.time.LocalDate;

@Singleton
public class SlackPulseResponseConverter {
    private static final Logger LOG = LoggerFactory.getLogger(SlackPulseResponseConverter.class);

    private final SlackSearch slackSearch;

    public SlackPulseResponseConverter(SlackSearch slackSearch) {
        this.slackSearch = slackSearch;
    }

    public PulseResponseCreateDTO get(
                    MemberProfileServices memberProfileServices, String body) {
        try {
            // Get the map of values from the string body
            final ObjectMapper mapper = new ObjectMapper();
            final Map<String, Object> map =
                    mapper.readValue(body, new TypeReference<>() {});
            final String type = (String)map.get("type");

            if (type.equals("view_submission")) {
                final Map<String, Object> view =
                        (Map<String, Object>)map.get("view");
                final Map<String, Object> state =
                        (Map<String, Object>)view.get("state");
                final Map<String, Object> values =
                        (Map<String, Object>)state.get("values");

                dumpMap(values, "");

                // Create the pulse DTO and fill in the values.
                PulseResponseCreateDTO response = new PulseResponseCreateDTO();
                response.setTeamMemberId(lookupUser(memberProfileServices, map));
                response.setSubmissionDate(LocalDate.now());

                // Internal Score
                Map<String, Object> internalBlock =
                    (Map<String, Object>)values.get("internalNumber");
                response.setInternalScore(Integer.parseInt(getMappedValue(
                    internalBlock, "internalScore", "selected_option", true)));
                // Internal Feelings
                response.setInternalFeelings(getMappedValue(
                    values, "internaltext", "internalFeelings", false));

                // External Score
                Map<String, Object> externalBlock =
                    (Map<String, Object>)values.get("externalNumber");
                String score = getMappedValue(externalBlock, "externalScore",
                                              "selected_option", false);
                if (score != null && !score.isEmpty()) {
                    response.setExternalScore(Integer.parseInt(score));
                }
                // External Feelings
                response.setExternalFeelings(getMappedValue(
                    values, "externalText", "externalFeelings", false));

                return response;
            } else {
                // If it's not a view submission, we need to return null so
                // the the caller knows that this is not the full pulse
                // response.
                return null;
            }
        } catch(JsonProcessingException ex) {
            LOG.error(ex.getMessage());
            throw new BadArgException(ex.getMessage());
        } catch(NumberFormatException ex) {
            LOG.error(ex.getMessage());
            throw new BadArgException("Pulse scores must be integers");
        }
    }

    private String getMappedValue(Map<String, Object> map, String key1,
                                  String key2, boolean required) {
        final String valueKey = "value";
        if (map != null && map.containsKey(key1)) {
            Map<String, Object> firstMap = (Map<String, Object>)map.get(key1);
            if (firstMap != null && firstMap.containsKey(key2)) {
                final Map<String, Object> secondMap =
                    (Map<String, Object>)firstMap.get(key2);
                if (secondMap != null && secondMap.containsKey(valueKey)) {
                    return (String)secondMap.get(valueKey);
                }
            }
        }

        if (required) {
            LOG.error("Expected {}.{}.{} was not found", key1, key2, valueKey);
            throw new BadArgException(
                String.format("Expected %s.%s.%s was not found",
                              key1, key2, valueKey));
        } else {
            return null;
        }
    }

    private UUID lookupUser(MemberProfileServices memberProfileServices,
                            Map<String, Object> map) {
        // Get the user's profile map.
        Map<String, Object> user = (Map<String, Object>)map.get("user");

        // Lookup the user based on the email address.
        String email = slackSearch.findUserEmail((String)user.get("id"));
        if (email == null) {
            throw new BadArgException("Unable to find the user email address");
        }
        MemberProfile member = memberProfileServices.findByWorkEmail(email);
        return member.getId();
    }

    // DEBUG Only
    private void dumpMap(Map<?, ?> map, String indent) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            LOG.info(indent + entry.getKey() + " : " + entry.getValue());

            if (entry.getValue() instanceof Map) {
                dumpMap((Map<?, ?>) entry.getValue(), indent + "  ");
            }
        }
    }
}

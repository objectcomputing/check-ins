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

            response.setInternalScore(Integer.parseInt(getMappedValue(
                values, "internalNumber", "internalScore", true)));
            response.setInternalFeelings(getMappedValue(
                values, "internaltext", "internalFeelings", false));

            String score = getMappedValue(values, "externalScore",
                                          "externalScore", false);
            if (!score.isEmpty()) {
                response.setExternalScore(Integer.parseInt(score));
            }
            response.setExternalFeelings(getMappedValue(
                values, "externalText", "externalFeelings", false));

            return response;
        } catch(JsonProcessingException ex) {
            LOG.error(ex.getMessage());
            throw new BadArgException(ex.getMessage());
        } catch(NumberFormatException ex) {
            LOG.error(ex.getMessage());
            throw new BadArgException("Pulse scores must be integers");
        }
    }

    private String getMappedValue(Map<String, Object> map, String blockId,
                                  String key, boolean required) {
        if (!map.containsKey(blockId)) {
            if (required) {
                throw new BadArgException(
                    String.format("Block ID - %s was not found", blockId));
            } else {
                return "";
            }
        }
        Map<String, Object> blockMap = (Map<String, Object>)map.get(blockId);

        final String valueKey = "value";
        if (blockMap.containsKey(key)) {
            final Map<String, Object> other =
                (Map<String, Object>)blockMap.get(key);
            if (other.containsKey(valueKey)) {
                return (String)other.get(valueKey);
            }
        }

        if (required) {
            LOG.error("Expected {}.{}.{} was not found", blockId, key, valueKey);
            throw new BadArgException(
                String.format("Expected %s.%s.%s was not found",
                              blockId, key, valueKey));
        } else {
            return "";
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

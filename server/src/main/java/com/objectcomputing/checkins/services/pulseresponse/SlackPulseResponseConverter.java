package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.UUID;
import java.time.LocalDate;

public class SlackPulseResponseConverter {
    public static PulseResponseCreateDTO get(
                    MemberProfileServices memberProfileServices, String body) {
        final String key = "payload=";
        final int start = body.indexOf(key);
        if (start >= 0) {
            try {
                // Get the map of values from the string body
                final ObjectMapper mapper = new ObjectMapper();
                final Map<String, Object> map =
                        mapper.readValue(body.substring(start + key.length()),
                                         new TypeReference<>() {});
                final Map<String, Object> view =
                        (Map<String, Object>)map.get("view");
                final Map<String, Object> state =
                        (Map<String, Object>)view.get("state");
                final Map<String, Object> values =
                        (Map<String, Object>)state.get("values");

                // Create the pulse DTO and fill in the values.
                PulseResponseCreateDTO response = new PulseResponseCreateDTO();
                response.setTeamMemberId(lookupUser(memberProfileServices, map));
                response.setSubmissionDate(LocalDate.now());

                response.setInternalScore(Integer.parseInt(
                    getMappedValue(values, "internalScore", true)));
                response.setInternalFeelings(
                    getMappedValue(values, "internalFeelings", false));

                String score = getMappedValue(values, "externalScore", false);
                if (!score.isEmpty()) {
                    response.setExternalScore(Integer.parseInt(score));
                }
                response.setExternalFeelings(
                    getMappedValue(values, "externalFeelings", false));

                return response;
            } catch(JsonProcessingException ex) {
                throw new BadArgException(ex.getMessage());
            } catch(NumberFormatException ex) {
                throw new BadArgException("Pulse scores must be integers");
            }
        } else {
            throw new BadArgException("Invalid pulse response body");
        }
    }

    private static String getMappedValue(Map<String, Object> map,
                                         String key, boolean required) {
        final String valueKey = "value";
        if (map.containsKey(key)) {
            final Map<String, Object> other = (Map<String, Object>)map.get(key);
            if (other.containsKey(valueKey)) {
                return (String)other.get(valueKey);
            }
        }

        if (required) {
            throw new BadArgException(
                String.format("Expected %s.%s was not found", key, valueKey));
        } else {
            return "";
        }
    }

    private static UUID lookupUser(MemberProfileServices memberProfileServices,
                                   Map<String, Object> map) {
        // Get the user's profile map.
        Map<String, Object> user = (Map<String, Object>)map.get("user");
        Map<String, Object> profile = (Map<String, Object>)user.get("profile");

        // Lookup the user based on the email address.
        String email = (String)profile.get("email");
        MemberProfile member = memberProfileServices.findByWorkEmail(email);
        return member.getId();
    }
}

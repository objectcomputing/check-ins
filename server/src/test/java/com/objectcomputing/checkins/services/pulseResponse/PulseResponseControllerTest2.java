package com.objectcomputing.checkins.services.pulseresponse;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.PulseResponseFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

// import static com.objectcomputing.checkins.services.pulseresponse.PulseResponseType.Constants.ADMIN_ROLE;
// import static com.objectcomputing.checkins.services.pulseresponse.PulseResponseType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;

class PulseResponseControllerTest2 extends TestContainersSuite implements MemberProfileFixture, PulseResponseFixture {

    @Inject
    @Client("/services/pulseresponse")
    HttpClient client;

    @Test
    void testCreateAPulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponseCreateDTO pulseresponseCreateDTO = new PulseResponseCreateDTO();
        // pulseresponseCreateDTO.setPulseResponse(PulseResponseType.MEMBER);
        pulseresponseCreateDTO.setTeamMemberId(memberProfile.getUuid());

        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseresponseCreateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        PulseResponse pulseresponse = response.body();
        assertNotNull(pulseresponse);
        assertEquals(pulseresponseCreateDTO.getTeamMemberId(), pulseresponse.getTeamMemberId());
        // assertEquals(pulseresponseCreateDTO.getPulseResponse(), pulseresponse.getPulseResponse());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), pulseresponse.getId()), response.getHeaders().get("location"));
    }

    // @Test
    // void testCreateAPulseResponseAlreadyExists() {
    //     MemberProfile memberProfile = createADefaultMemberProfile();
    //     // PulseResponse alreadyExistingPulseResponse = createDefaultPulseResponseRepository(PulseResponseType.MEMBER, memberProfile);
    //     PulseResponseCreateDTO pulseresponseCreateDTO = new PulseResponseCreateDTO();
    //     // pulseresponseCreateDTO.setPulseResponse(alreadyExistingPulseResponse.getPulseResponse());
    //     // pulseresponseCreateDTO.setTeamMemberId(alreadyExistingPulseResponse.getTeamMemberId());

    //     final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseresponseCreateDTO)
    //             .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
    //     final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
    //             client.toBlocking().exchange(request, Map.class));

    //     JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
    //     String error = Objects.requireNonNull(body).get("message").asText();
    //     String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

    //     assertEquals(String.format("Member %s already has pulseresponse %s", pulseresponseCreateDTO.getTeamMemberId(), pulseresponseCreateDTO.getPulseResponse()),
    //             error);
    //     assertEquals(request.getPath(), href);
    // }

    @Test
    void testCreateForbidden() {
        PulseResponseCreateDTO pulseresponseCreateDTO = new PulseResponseCreateDTO();
        // pulseresponseCreateDTO.setPulseResponse(PulseResponseType.MEMBER);
        pulseresponseCreateDTO.setTeamMemberId(UUID.randomUUID());

        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseresponseCreateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateAnInvalidPulseResponse() {
        PulseResponseCreateDTO pulseresponseCreateDTO = new PulseResponseCreateDTO();

        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseresponseCreateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("pulseresponse.teamMemberId: must not be null", errorList.get(0));
        assertEquals("pulseresponse.pulseresponse: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateNonExistingMember() {
        PulseResponseCreateDTO pulseresponse = new PulseResponseCreateDTO();
        pulseresponse.setTeamMemberId(UUID.randomUUID());
        // pulseresponse.setPulseResponse(PulseResponseType.MEMBER);


        final HttpRequest<PulseResponseCreateDTO> request = HttpRequest.POST("", pulseresponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", pulseresponse.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCreateANullPulseResponse() {
        final HttpRequest<String> request = HttpRequest.POST("", "")
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [pulseresponse] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testLoadPulseResponses() {
        MemberProfile memberProfile = createADefaultMemberProfile();

        PulseResponseCreateDTO pulseresponseCreateDTO = new PulseResponseCreateDTO();
        // pulseresponseCreateDTO.setPulseResponse(PulseResponseType.MEMBER);
        pulseresponseCreateDTO.setTeamMemberId(memberProfile.getUuid());

        PulseResponseCreateDTO pulseresponseCreateDTO2 = new PulseResponseCreateDTO();
        // pulseresponseCreateDTO2.setPulseResponse(PulseResponseType.ADMIN);
        pulseresponseCreateDTO2.setTeamMemberId(memberProfile.getUuid());

        List<PulseResponseCreateDTO> dtoList = List.of(pulseresponseCreateDTO, pulseresponseCreateDTO2);

        final MutableHttpRequest<List<PulseResponseCreateDTO>> request = HttpRequest.POST("pulseresponses", dtoList)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<List<PulseResponse>> response = client.toBlocking().exchange(request, Argument.listOf(PulseResponse.class));

        List<PulseResponse> pulseresponses = response.body();
        assertNotNull(pulseresponses);
        assertEquals(2, pulseresponses.size());
        assertEquals(pulseresponseCreateDTO.getTeamMemberId(), pulseresponses.get(0).getTeamMemberId());
        // assertEquals(pulseresponseCreateDTO.getPulseResponse(), pulseresponses.get(0).getPulseResponse());
        assertEquals(pulseresponseCreateDTO2.getTeamMemberId(), pulseresponses.get(1).getTeamMemberId());
        // assertEquals(pulseresponseCreateDTO2.getPulseResponse(), pulseresponses.get(1).getPulseResponse());

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));
    }

    @Test
    void testLoadForbidden() {
        // PulseResponse r = new PulseResponse(UUID.randomUUID(), PulseResponseType.ADMIN, UUID.randomUUID());

        // List<PulseResponse> pulseresponses = List.of(r);

        // final HttpRequest<List<PulseResponse>> request = HttpRequest.POST("pulseresponses", pulseresponses)
        //         .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        // HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
        //         client.toBlocking().exchange(request, String.class));

        // assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        // assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testLoadPulseResponsesInvalidPulseResponse() {
        PulseResponseCreateDTO pulseresponseCreateDTO = new PulseResponseCreateDTO();
        // pulseresponseCreateDTO.setPulseResponse(PulseResponseType.MEMBER);
        pulseresponseCreateDTO.setTeamMemberId(UUID.randomUUID());

        PulseResponseCreateDTO pulseresponseCreateDTO2 = new PulseResponseCreateDTO();

        List<PulseResponseCreateDTO> dtoList = List.of(pulseresponseCreateDTO, pulseresponseCreateDTO2);

        final MutableHttpRequest<List<PulseResponseCreateDTO>> request = HttpRequest.POST("pulseresponses", dtoList)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("pulseresponses.teamMemberId: must not be null", errorList.get(0));
        assertEquals("pulseresponses.pulseresponse: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testReadPulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseresponse = createDefaultPulseResponseRepository(memberProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", pulseresponse.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        assertEquals(pulseresponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadForbidden() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseresponse = createDefaultPulseResponseRepository(memberProfile);

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", pulseresponse.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testReadPulseResponseNotFound() {
        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, PulseResponse.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindPulseResponses() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        // Set<PulseResponse> pulseresponses = Set.of(createDefaultPulseResponseRepository(PulseResponseType.ADMIN, memberProfile),
        //         createDefaultPulseResponseRepository(PulseResponseType.PDL, memberProfile));

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", memberProfile.getUuid()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        // assertEquals(pulseresponses, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindPulseResponsesForbidden() {
        // final HttpRequest<?> request = HttpRequest.GET(String.format("/?pulseresponse=%s&teamMemberId=%s", PulseResponseType.ADMIN,
        //         UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        // HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
        //         client.toBlocking().exchange(request, String.class));

        // assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        // assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testFindPulseResponsesAllParams() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseresponse = createDefaultPulseResponseRepository(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pulseresponse=%s&teamMemberId=%s",
                pulseresponse.getTeamMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

        assertEquals(Set.of(pulseresponse), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    // @Test
    // void testFindPulseResponsesDoesNotExist() {
    //     final HttpRequest<?> request = HttpRequest.GET(String.format("/?pulseresponse=%s", PulseResponseType.ADMIN))
    //             .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
    //     HttpResponse<Set<PulseResponse>> response = client.toBlocking().exchange(request, Argument.setOf(PulseResponse.class));

    //     assertEquals(HttpStatus.OK, response.getStatus());
    //     assertEquals(Set.of(), response.body());
    // }

    @Test
    void testUpdatePulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseresponse = createDefaultPulseResponseRepository(memberProfile);

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseresponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<PulseResponse> response = client.toBlocking().exchange(request, PulseResponse.class);

        assertEquals(pulseresponse, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), pulseresponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateNonExistingMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseresponse = createDefaultPulseResponseRepository(memberProfile);

        pulseresponse.setTeamMemberId(UUID.randomUUID());

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseresponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", pulseresponse.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNonExistingPulseResponseType() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseresponseToUpdate = createDefaultPulseResponseRepository(memberProfile);

        Map<String, String> pulseresponse = new HashMap<>();
        pulseresponse.put("id", pulseresponseToUpdate.getId().toString());
        pulseresponse.put("pulseresponse", "ROLE_DOES_NOT_EXIST");
        pulseresponse.put("teamMemberId", pulseresponseToUpdate.getTeamMemberId().toString());

        final HttpRequest<Map<String, String>> request = HttpRequest.PUT("", pulseresponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertTrue(error.contains("not one of the values accepted for Enum class"));
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNonExistingPulseResponse() {
        // MemberProfile memberProfile = createADefaultMemberProfile();
        // // PulseResponse pulseresponse = new PulseResponse(UUID.randomUUID(), PulseResponseType.MEMBER, memberProfile.getUuid());

        // final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseresponse)
        //         .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        // final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
        //         client.toBlocking().exchange(request, Map.class));

        // JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        // String error = Objects.requireNonNull(body).get("message").asText();
        // String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        // assertEquals(String.format("Unable to locate pulseresponse to update with id %s", pulseresponse.getId()), error);
        // assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateWithoutId() {
        // MemberProfile memberProfile = createADefaultMemberProfile();
        // PulseResponse pulseresponse = createDefaultPulseResponseRepository(PulseResponseType.MEMBER, memberProfile);
        // pulseresponse.setId(null);

        // final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseresponse)
        //         .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        // final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
        //         client.toBlocking().exchange(request, Map.class));

        // JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        // String error = Objects.requireNonNull(body).get("message").asText();
        // String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        // assertEquals("Unable to locate pulseresponse to update with id null", error);
        // assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateForbidden() {
        // PulseResponse r = new PulseResponse(UUID.randomUUID(), PulseResponseType.ADMIN, UUID.randomUUID());

        // final HttpRequest<PulseResponse> request = HttpRequest.PUT("", r)
        //         .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        // HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
        //         client.toBlocking().exchange(request, String.class));

        // assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        // assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateAnInvalidPulseResponse() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        PulseResponse pulseresponse = createDefaultPulseResponseRepository(memberProfile);
        pulseresponse.setTeamMemberId(null);
        // pulseresponse.setPulseResponse(null);

        final HttpRequest<PulseResponse> request = HttpRequest.PUT("", pulseresponse)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("pulseresponse.teamMemberId: must not be null", errorList.get(0));
        assertEquals("pulseresponse.pulseresponse: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullPulseResponse() {
        final HttpRequest<String> request = HttpRequest.PUT("", "")
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String errors = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
        assertEquals("Required Body [pulseresponse] not specified", errors);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void deletePulseResponse() {
        // MemberProfile memberProfile = createADefaultMemberProfile();
        // // PulseResponse pulseresponse = createDefaultPulseResponseRepository(memberProfile);

        // // assertNotNull(findPulseResponse(pulseresponse));

        // final MutableHttpRequest<Object> request = HttpRequest.DELETE(pulseresponse.getId().toString())
        //         .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        // final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        // assertEquals(HttpStatus.OK, response.getStatus());
        // // assertNull(findPulseResponse(pulseresponse));
    }

    @Test
    void deletePulseResponseNonExisting() {
        UUID uuid = UUID.randomUUID();

        // assertNull(findPulseResponseById(uuid));

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid.toString())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        // assertNull(findPulseResponseById(uuid));
    }

    @Test
    void deletePulseResponseBadId() {
        String uuid = "Bill-Nye-The-Science-Guy";

        final MutableHttpRequest<Object> request = HttpRequest.DELETE(uuid)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String errors = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
        assertTrue(errors.contains(String.format("Failed to convert argument [id] for value [%s]", uuid)));
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void deletePulseResponseUnauthorized() {
        UUID uuid = UUID.randomUUID();

        final HttpRequest<UUID> request = HttpRequest.DELETE(uuid.toString());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }
}

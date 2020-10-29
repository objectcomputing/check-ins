package com.objectcomputing.checkins.services.memberprofile;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.Resource;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MemberProfileControllerTest extends TestContainersSuite implements MemberProfileFixture {

    @Inject
    @Client("/services/member-profile")
    private HttpClient client;

    public static MemberProfileUpdateDTO mkUpdateMemberProfileDTO() {
        MemberProfileUpdateDTO dto = new MemberProfileUpdateDTO();
        dto.setId(UUID.fromString("e134d349-cf02-4a58-b9d3-42cc48375628"));
        dto.setName("TestName");
        dto.setTitle("TestRole");
        dto.setLocation("TestLocation");
        dto.setWorkEmail("TestEmail");
        dto.setInsperityId("TestInsperityId");
        dto.setStartDate(LocalDate.of(2019, 1, 01));
        dto.setBioText("TestBio");
        return dto;
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Test
    public void testGETNonExistingEndpointReturns404() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc")
                    .basicAuth(MEMBER_ROLE,MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testGETFindByNameReturnsEmptyBody() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue("dnc"))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGETFindByValueName() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue(memberProfile.getName()))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

        assertEquals(Set.of(memberProfile), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGETGetByIdHappyPath() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", memberProfile.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request, MemberProfile.class);

        assertEquals(memberProfile, response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    public void testPOSTCreateAMemberProfile() {

        MemberProfileUpdateDTO dto = mkUpdateMemberProfileDTO();

        final HttpRequest<?> request = HttpRequest.
                POST("/", dto).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request,MemberProfile.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED,response.getStatus());
        assertEquals(dto.getName(), response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()),"/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPOSTCreateAMemberProfileAlreadyExists() {

        MemberProfileUpdateDTO dto = mkUpdateMemberProfileDTO();

        final HttpRequest<?> request = HttpRequest.
                POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request,MemberProfile.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED,response.getStatus());
        assertEquals(dto.getName(), response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()),"/services" +  response.getHeaders().get("location"));

    }

    @Test
    public void testPOSTCreateANullMemberProfile() {

        MemberProfileCreateDTO memberProfileCreateDTO = new MemberProfileCreateDTO();

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.
                POST("/", memberProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    public void testPUTUpdateMemberProfile() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        memberProfile.setStartDate(LocalDate.of(2019, 1, 01));

        final HttpRequest<MemberProfile> request = HttpRequest.PUT("/", memberProfile)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<MemberProfile> response = client.toBlocking().exchange(request, MemberProfile.class);

        assertEquals(memberProfile, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(),  memberProfile.getId()),"/services" +response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateNonexistentMemberProfile() {

        MemberProfileCreateDTO memberProfileCreateDTO = new MemberProfileCreateDTO();
        memberProfileCreateDTO.setName("reincarnation");

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.
                PUT("/", memberProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    public void testPUTUpdateNullMemberProfile() {

        final HttpRequest<String> request = HttpRequest.PUT("","").basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    // Find By id - when no user data exists for POST
    @Test
    public void testPostValidationFailures() {

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.POST("", new MemberProfileCreateDTO())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

        assertEquals(5, errors.size());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // Find By id - when no user data exists for PUT
    @Test
    public void testPutValidationFailures() {

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.PUT("", new MemberProfileCreateDTO())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

        assertEquals(3, errors.size());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

@Test
void testFindByMemberName() {
    MemberProfile memberProfile = createADefaultMemberProfile();

    final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", encodeValue(memberProfile.getName()))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
    final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

    assertEquals(Set.of(memberProfile), response.body());
    assertEquals(HttpStatus.OK, response.getStatus());

}

@Test
void testFindByMemberRole() {
    MemberProfile memberProfile = createADefaultMemberProfile();

    final HttpRequest<?> request = HttpRequest.GET(String.format("/?title=%s", encodeValue(memberProfile.getTitle()))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
    final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

    assertEquals(Set.of(memberProfile), response.body());
    assertEquals(HttpStatus.OK, response.getStatus());

}

@Test
void testFindByMemberPdlId() {
    MemberProfile memberProfile = createADefaultMemberProfile();
    MemberProfile memberProfilePdl = createADefaultMemberProfileForPdl(memberProfile);

    final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s",memberProfilePdl.getPdlId()).toString()).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
    final HttpResponse<Set<MemberProfile>> response = client.toBlocking().exchange(request, Argument.setOf(MemberProfile.class));

    assertEquals(Set.of(memberProfilePdl), response.body());
    assertEquals(HttpStatus.OK, response.getStatus());

}

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        MemberProfileUpdateDTO testMemberProfile = mkUpdateMemberProfileDTO();
        testMemberProfile.setId(null);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(HttpRequest.PUT("", testMemberProfile)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));
        assertEquals("memberProfile.id: must not be null", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Request with invalid body - missing ID
    @Test
    public void testPutUpdateWithMissingField() {
        MemberProfileUpdateDTO testMemberProfile = mkUpdateMemberProfileDTO();
        testMemberProfile.setName(null);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testMemberProfile)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });
        assertEquals("memberProfile.name: must not be blank", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

}


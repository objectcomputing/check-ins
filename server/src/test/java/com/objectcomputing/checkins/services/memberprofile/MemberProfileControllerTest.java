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
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
public class MemberProfileControllerTest {

    @Inject
    @Client("/member-profile")
    private HttpClient client;

    @Inject
    MemberProfileServices mockMemberServices;

    @MockBean(MemberProfileServices.class)
    public MemberProfileServices getMockMemberServices() {
        return mock(MemberProfileServices.class);
    }

    private static LocalDate testDate = LocalDate.now();
    private static String testUser = "testName";
    private static String testRole = "testRole";

    @BeforeEach
    void setup() {
        reset(mockMemberServices);
    }

    // Find By id - when no user data exists
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

    // Find By id - when no user data exists
    @Test
    public void testPutValidationFailures() {

        final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.PUT("", new MemberProfileCreateDTO())
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

        assertEquals(6, errors.size());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // Find By id - when no user data exists
    @Test
    public void testGetFindByIdReturns404() {

        when(mockMemberServices.getById(testUuid))
                .thenThrow(new MemberProfileDoesNotExistException("testMessage"));

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest
                    .GET(String.format("/%s", testUuid))
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });
        assertEquals("testMessage", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By Name - when no user data exists
    @Test
    public void testGetFindByNameReturnsEmptyBody() {

        MemberProfile memberProfile = mkMemberProfile();
        memberProfile.setName(memberProfile.getName());

        when(mockMemberServices.findByValues(memberProfile.getName(), null, null, null))
                .thenReturn(Collections.EMPTY_SET);

        HttpRequest request = HttpRequest.GET(String.format("/?name=%s", testUser))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<MemberProfileResponseDTO> response = client.toBlocking().retrieve(request, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(0, response.size());
    }

    // Find By Role - when no user data exists
    @Test
    public void testGetFindByRoleReturnsEmptyBody() {

        MemberProfile memberProfile = mkMemberProfile();
        memberProfile.setName(memberProfile.getName());

        when(mockMemberServices.findByValues(null, memberProfile.getRole(), null, null))
                .thenReturn(Collections.EMPTY_SET);

        HttpRequest request = HttpRequest.GET(String.format("/?role=%s", testRole))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<MemberProfileResponseDTO> response = client.toBlocking().retrieve(request, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(0, response.size());
    }

    // Find By PdlId - when no user data exists
    @Test
    public void testGetFindByPdlIdReturnsEmptyBody() {

        MemberProfile memberProfile = mkMemberProfile();
        memberProfile.setPdlId(testPdlId);

        when(mockMemberServices.findByValues(null, null, memberProfile.getPdlId(), null))
                .thenReturn(Collections.EMPTY_SET);

        HttpRequest request = HttpRequest.GET(String.format("/?pdlId=%s", memberProfile.getPdlId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<MemberProfileResponseDTO> response = client.toBlocking().retrieve(request, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(0, response.size());
    }

    // test Find All
    @Test
    public void testGetFindAll() {
        MemberProfile profileOne = mkMemberProfile();
        MemberProfile profileTwo = mkMemberProfile("2");
        when(mockMemberServices.findByValues(null, null, null, null))
                .thenReturn(Set.of(profileOne, profileTwo));

        HttpRequest requestFindAll = HttpRequest.GET("")
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<MemberProfileResponseDTO> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(2, responseFindAll.size());
    }

    // test Find By Id
    @Test
    public void testGetFindById() {

        MemberProfile memberProfile = mkMemberProfile();
        memberProfile.setUuid(testUuid);

        when(mockMemberServices.getById(memberProfile.getUuid())).thenReturn(memberProfile);

        HttpRequest requestFindById = HttpRequest.GET(String.format("/%s", memberProfile.getUuid()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<MemberProfileResponseDTO> response = client.toBlocking().exchange(requestFindById, MemberProfileResponseDTO.class);
        
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertProfilesEqual(memberProfile, response.body());
    }

    // test Find By Name
    @Test
    public void testGetFindByName() {

        MemberProfile memberProfile = mkMemberProfile();

        when(mockMemberServices.findByValues(memberProfile.getName(), null, null, null))
                .thenReturn(Collections.singleton(memberProfile));

        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?name=%s", memberProfile.getName()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<List<MemberProfileResponseDTO>> response = client.toBlocking().exchange(requestFindByName, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());

        List<MemberProfileResponseDTO> responseBody = response.body();

        assertEquals(1, responseBody.size());
        assertProfilesEqual(memberProfile, responseBody.get(0));
    }

    // test Find By Role
    @Test
    public void testGetFindByRole() {
        MemberProfile memberProfile = mkMemberProfile();

        when(mockMemberServices.findByValues(null, memberProfile.getRole(), null, null))
                .thenReturn(Collections.singleton(memberProfile));

        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?role=%s", memberProfile.getRole()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<List<MemberProfileResponseDTO>> response = client.toBlocking().exchange(requestFindByName, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());

        List<MemberProfileResponseDTO> responseBody = response.body();

        assertEquals(1, responseBody.size());
        assertProfilesEqual(memberProfile, responseBody.get(0));
    }

    // test Find By PdlId
    @Test
    public void testGetFindByPdlId() {
        MemberProfile memberProfile = mkMemberProfile();
        memberProfile.setPdlId(testUuid);

        when(mockMemberServices.findByValues(null, null, memberProfile.getPdlId(), null))
                .thenReturn(Collections.singleton(memberProfile));

        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?pdlId=%s", memberProfile.getPdlId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<List<MemberProfileResponseDTO>> response = client.toBlocking().exchange(requestFindByName, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());

        List<MemberProfileResponseDTO> responseBody = response.body();

        assertEquals(1, responseBody.size());
        assertProfilesEqual(memberProfile, responseBody.get(0));

    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();

        when(mockMemberServices.saveProfile(any(MemberProfile.class))).thenReturn(mkMemberProfile());

        final HttpResponse<MemberProfileResponseDTO> response = client
                .toBlocking()
                .exchange(HttpRequest.POST("", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE), MemberProfileResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertProfilesEqual(requestBody, response.body());
        assertEquals("/member-profile/" + response.body().getId(), response.header("location"));
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        MemberProfileUpdateDTO requestBody = mkUpdateMemberProfileDTO();

        MemberProfile expected = mkMemberProfile();
        expected.setUuid(requestBody.getId());

        when(mockMemberServices.saveProfile(any(MemberProfile.class))).thenReturn(expected);

        final HttpResponse<MemberProfileResponseDTO> response = client
                .toBlocking()
                .exchange(HttpRequest.PUT("", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE), MemberProfileResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertProfilesEqual(requestBody, response.body());
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
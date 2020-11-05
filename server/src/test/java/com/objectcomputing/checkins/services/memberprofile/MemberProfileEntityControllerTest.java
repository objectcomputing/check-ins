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
import java.util.*;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
public class MemberProfileEntityControllerTest {

    @Inject
    @Client("/services/member-profile")
    private HttpClient client;

    @Inject
    MemberProfileServices mockMemberServices;

    @MockBean(MemberProfileServices.class)
    public MemberProfileServices getMockMemberServices() {
        return mock(MemberProfileServices.class);
    }

    private static final String testUser = "testName";
    private static final String testRole = "testRole";

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

        assertEquals(4, errors.size());
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

        assertEquals(2, errors.size());
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

        MemberProfileEntity memberProfileEntity = mkMemberProfile();
        memberProfileEntity.setName(memberProfileEntity.getName());

        when(mockMemberServices.findByValues(memberProfileEntity.getName(), null, null, null))
                .thenReturn(Collections.EMPTY_SET);

        HttpRequest request = HttpRequest.GET(String.format("/?name=%s", testUser))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<MemberProfileResponseDTO> response = client.toBlocking().retrieve(request, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(0, response.size());
    }

    // Find By Role - when no user data exists
    @Test
    public void testGetFindByRoleReturnsEmptyBody() {

        MemberProfileEntity memberProfileEntity = mkMemberProfile();
        memberProfileEntity.setName(memberProfileEntity.getName());

        when(mockMemberServices.findByValues(null, memberProfileEntity.getTitle(), null, null))
                .thenReturn(Collections.EMPTY_SET);

        HttpRequest request = HttpRequest.GET(String.format("/?role=%s", testRole))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<MemberProfileResponseDTO> response = client.toBlocking().retrieve(request, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(0, response.size());
    }

    // Find By PdlId - when no user data exists
    @Test
    public void testGetFindByPdlIdReturnsEmptyBody() {

        MemberProfileEntity memberProfileEntity = mkMemberProfile();
        memberProfileEntity.setPdlId(testPdlId);

        when(mockMemberServices.findByValues(null, null, memberProfileEntity.getPdlId(), null))
                .thenReturn(Collections.EMPTY_SET);

        HttpRequest request = HttpRequest.GET(String.format("/?pdlId=%s", memberProfileEntity.getPdlId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<MemberProfileResponseDTO> response = client.toBlocking().retrieve(request, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(0, response.size());
    }

    // test Find All
    @Test
    public void testGetFindAll() {
        MemberProfileEntity profileOne = mkMemberProfile();
        MemberProfileEntity profileTwo = mkMemberProfile("2");
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

        MemberProfileEntity memberProfileEntity = mkMemberProfile();
        memberProfileEntity.setId(testUuid);

        when(mockMemberServices.getById(memberProfileEntity.getId())).thenReturn(memberProfileEntity);

        HttpRequest requestFindById = HttpRequest.GET(String.format("/%s", memberProfileEntity.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<MemberProfileResponseDTO> response = client.toBlocking().exchange(requestFindById, MemberProfileResponseDTO.class);
        
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertProfilesEqual(memberProfileEntity, response.body());
    }

    // test Find By Name
    @Test
    public void testGetFindByName() {

        MemberProfileEntity memberProfileEntity = mkMemberProfile();

        when(mockMemberServices.findByValues(memberProfileEntity.getName(), null, null, null))
                .thenReturn(Collections.singleton(memberProfileEntity));

        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?name=%s", memberProfileEntity.getName()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<List<MemberProfileResponseDTO>> response = client.toBlocking().exchange(requestFindByName, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());

        List<MemberProfileResponseDTO> responseBody = response.body();

        assertEquals(1, responseBody.size());
        assertProfilesEqual(memberProfileEntity, responseBody.get(0));
    }

    // test Find By Role
    @Test
    public void testGetFindByRole() {
        MemberProfileEntity memberProfileEntity = mkMemberProfile();

        when(mockMemberServices.findByValues(null, memberProfileEntity.getTitle(), null, null))
                .thenReturn(Collections.singleton(memberProfileEntity));

        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?title=%s", memberProfileEntity.getTitle()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<List<MemberProfileResponseDTO>> response = client.toBlocking().exchange(requestFindByName, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());

        List<MemberProfileResponseDTO> responseBody = response.body();

        assertEquals(1, responseBody.size());
        assertProfilesEqual(memberProfileEntity, responseBody.get(0));
    }

    // test Find By PdlId
    @Test
    public void testGetFindByPdlId() {
        MemberProfileEntity memberProfileEntity = mkMemberProfile();
        memberProfileEntity.setPdlId(testUuid);

        when(mockMemberServices.findByValues(null, null, memberProfileEntity.getPdlId(), null))
                .thenReturn(Collections.singleton(memberProfileEntity));

        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?pdlId=%s", memberProfileEntity.getPdlId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<List<MemberProfileResponseDTO>> response = client.toBlocking().exchange(requestFindByName, Argument.listOf(MemberProfileResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());

        List<MemberProfileResponseDTO> responseBody = response.body();

        assertEquals(1, responseBody.size());
        assertProfilesEqual(memberProfileEntity, responseBody.get(0));

    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();

        when(mockMemberServices.saveProfile(any(MemberProfileEntity.class))).thenReturn(mkMemberProfile());

        final HttpResponse<MemberProfileResponseDTO> response = client
                .toBlocking()
                .exchange(HttpRequest.POST("", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE), MemberProfileResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertProfilesEqual(requestBody, response.body());
        assertEquals("/member-profile/" + response.body().getId(), response.header("location"));
    }

    // POST - Nullable MemberProfile name
    @Test
    public void testPostWithNullName() {

        MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();
        requestBody.setName(null);

        MemberProfileEntity expected = mkMemberProfile();
        expected.setName(null);

        when(mockMemberServices.saveProfile(any(MemberProfileEntity.class))).thenReturn(expected);

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

        MemberProfileEntity expected = mkMemberProfile();
        expected.setId(requestBody.getId());

        when(mockMemberServices.saveProfile(any(MemberProfileEntity.class))).thenReturn(expected);

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
}
package com.objectcomputing.checkins;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.objectcomputing.checkins.member.MemberProfile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
public class MemberProfileControllerTest {

    @Inject
    @Client("/member-profile")
    private HttpClient client;

    MemberProfileRepository memberProfileRepository = mock(MemberProfileRepository.class);

    @BeforeEach
    void setup() {
        reset(memberProfileRepository);
    }

    @Test
    public void testFindNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/99"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testFindNonExistingEndpointReturnsNotFound() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/bar?order=foo"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By Name - when no user data exists
    @Test
    public void testGetFindByNameReturnsEmptyBody() {

        String testUser = "testUser";
        MemberProfile memberProfile = new MemberProfile();
        List<MemberProfile> result = new ArrayList<MemberProfile>();
        result.add(memberProfile);

        when(memberProfileRepository.findByName("testUser")).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?name=%s", testUser)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // Find By Role - when no user data exists
    @Test
    public void testGetFindByRoleReturnsEmptyBody() {

        String testRole = "test";
        MemberProfile memberProfile = new MemberProfile();
        List<MemberProfile> result = new ArrayList<MemberProfile>();
        result.add(memberProfile);

        when(memberProfileRepository.findByRole("test")).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?role=%s", testRole)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // Find By PdlId - when no user data exists
    @Test
    public void testGetFindByPdlIdReturnsEmptyBody() {

        UUID testUUuid = UUID.randomUUID();
        MemberProfile memberProfile = new MemberProfile();
        List<MemberProfile> result = new ArrayList<MemberProfile>();
        result.add(memberProfile);

        when(memberProfileRepository.findByPdlId(testUUuid)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?pdlId=%s", testUUuid)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    @Test
    public void testGet() {

        Date testDate = new Date(System.currentTimeMillis());
        String testUser = "testName";
        String testRole = "testRole";
        UUID testPdlId = UUID.randomUUID();
        Map<String, Object> fakeBody = new HashMap<String, Object>() {{
            put("name", testUser);
            put("role", testRole);
            put("pdlId", testPdlId);
            put("location", "testLocation");
            put("workEmail", "testEmail");
            put("insperityId", "testInsperityId");
            put("startDate", testDate);
            put("bioText", "testBio");
        }};

        // Save
        client.toBlocking().exchange(HttpRequest.POST("", fakeBody));

        // test Find All
        HttpRequest requestFindAll = HttpRequest.GET(String.format(""));
        List<MemberProfile> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, MemberProfile.class));  
        assertEquals(1, responseFindAll.size());
        assertEquals(testUser, responseFindAll.get(0).getName());
        assertEquals(testRole, responseFindAll.get(0).getRole());

        // test Find By Name
        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?name=%s", testUser));
        List<MemberProfile> responseFindByName = client.toBlocking().retrieve(requestFindByName, Argument.of(List.class, MemberProfile.class));  
        assertEquals(1, responseFindByName.size());
        assertEquals(testUser, responseFindByName.get(0).getName());
        assertEquals(testRole, responseFindByName.get(0).getRole());

        // test Find By Role
        HttpRequest requestFindByRole = HttpRequest.GET(String.format("/?role=%s", testRole));
        List<MemberProfile> responseFindByRole = client.toBlocking().retrieve(requestFindByRole, Argument.of(List.class, MemberProfile.class));  
        assertEquals(1, responseFindByRole.size());
        assertEquals(testUser, responseFindByRole.get(0).getName());
        assertEquals(testRole, responseFindByRole.get(0).getRole());

        // test Find By PdlId
        HttpRequest requestFindByPdlId = HttpRequest.GET(String.format("/?pdlId=%s", testPdlId));
        List<MemberProfile> responseFindByPdlId = client.toBlocking().retrieve(requestFindByPdlId, Argument.of(List.class, MemberProfile.class));  
        assertEquals(1, responseFindByPdlId.size());
        assertEquals(testUser, responseFindByPdlId.get(0).getName());
        assertEquals(testRole, responseFindByPdlId.get(0).getRole());
        assertEquals(testPdlId, responseFindByPdlId.get(0).getPdlId());
    }

    // POST - Valid Body
    @Test
    public void testPostSave() {
        Date testDate = new Date(System.currentTimeMillis());
        Map<String, Object> fakeBody = new HashMap<String, Object>() {{
            put("name", "testName");
            put("role", "testRole");
            put("pdlId", UUID.randomUUID());
            put("location", "testLocation");
            put("workEmail", "testEmail");
            put("insperityId", "testInsperityId");
            put("startDate", testDate);
            put("bioText", "testBio");
        }};

        MemberProfile testMemberProfile = new MemberProfile("testName", "test role", UUID.randomUUID(), "test location", "test email", "test InsperityId", testDate, "test bio");

        when(memberProfileRepository.save(testMemberProfile)).thenReturn(testMemberProfile);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.POST("", fakeBody));
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // POST - Invalid call
    @Test
    public void testPostNonExistingEndpointReturns404() {

        MemberProfile testMemberProfile = new MemberProfile();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("/99", testMemberProfile));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        UUID testId = UUID.randomUUID();
        Date testDate = new Date(System.currentTimeMillis());
        MemberProfile testMemberProfile = new MemberProfile("Name", "test role", UUID.randomUUID(), "test location", "test email", "test InsperityId", testDate, "test bio");
        testMemberProfile.setUuid(testId);

        Map<String, Object> fakeBody = new HashMap<String, Object>() {{
            put("uuid", testId);
            put("name", "updatedName");
            put("role", "testRole");
            put("pdlId", UUID.randomUUID());
            put("location", "testLocation");
            put("workEmail", "testEmail");
            put("insperityId", "testInsperityId");
            put("startDate", testDate);
            put("bioText", "testBio");
        }};

        when(memberProfileRepository.update(testMemberProfile)).thenReturn(testMemberProfile);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.PUT("", fakeBody));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        MemberProfile testMemberProfile = new MemberProfile();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testMemberProfile));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Request with invalid body - missing ID
    @Test
    public void testPutUpdateWithMissingField() {

        Date testDate = new Date(System.currentTimeMillis());
        Map<String, Object> fakeBody = new HashMap<String, Object>() {{
            put("uuid", null);
            put("name", "updatedName");
            put("role", "testRole");
            put("pdlId", UUID.randomUUID());
            put("location", "testLocation");
            put("workEmail", "testEmail");
            put("insperityId", "testInsperityId");
            put("startDate", testDate);
            put("bioText", "testBio");
        }};

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", fakeBody));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }
}
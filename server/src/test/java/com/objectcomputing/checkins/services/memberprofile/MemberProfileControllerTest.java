package com.objectcomputing.checkins.services.memberprofile;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

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

    MemberProfileRepository mockMemberProfileRepository = mock(MemberProfileRepository.class);
    MemberProfile mockMemberProfile = mock(MemberProfile.class);

    private static UUID testUuid;
    private static LocalDate testDate = LocalDate.now();
    private static String testUser = "testName";
    private static String testRole = "testRole";
    private static UUID testPdlId = UUID.randomUUID();
    private static boolean isDataSetupForTest = false;

    private static final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
        put("name", testUser);
        put("role", testRole);
        put("pdlId", testPdlId);
        put("location", "testLocation");
        put("workEmail", "testEmail");
        put("insperityId", "testInsperityId");
        put("startDate", testDate);
        put("bioText", "testBio");
    }};

    @BeforeEach
    void setup() {
        reset(mockMemberProfileRepository);
        reset(mockMemberProfile);
    }

    // Find By id - when no user data exists
    @Test
    public void testGetFindByIdReturns404() {

        UUID testId = UUID.randomUUID();

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET(String.format("/" + testId)));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By Name - when no user data exists
    @Test
    public void testGetFindByNameReturnsEmptyBody() {

        String testUser = "testUser";
        MemberProfile memberProfile = new MemberProfile();
        List<MemberProfile> result = new ArrayList<MemberProfile>();
        result.add(memberProfile);

        when(mockMemberProfileRepository.findByName("testUser")).thenReturn(result);

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

        when(mockMemberProfileRepository.findByRole("test")).thenReturn(result);

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

        when(mockMemberProfileRepository.findByPdlId(testUUuid)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/?pdlId=%s", testUUuid)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // test Find All
    @Test
    public void testGetFindAll() {

        setupTestData();

        HttpRequest requestFindAll = HttpRequest.GET(String.format(""));
        List<MemberProfile> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, mockMemberProfile.getClass()));  
        
        assertEquals(1, responseFindAll.size());
        assertEquals(testUser, responseFindAll.get(0).getName());
        assertEquals(testRole, responseFindAll.get(0).getRole());
    }

    // test Find By Id
    @Test
    public void testGetFindById() {

        setupTestData();

        HttpRequest requestFindById = HttpRequest.GET(String.format("/" + testUuid));
        HttpResponse<MemberProfile> response = client.toBlocking().exchange(requestFindById, mockMemberProfile.getClass());  
        
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(testUuid, response.body().getUuid());
        assertEquals(testUser, response.body().getName());
        assertEquals(testRole, response.body().getRole());
    }

    // test Find By Name
    @Test
    public void testGetFindByName() {

        setupTestData();

        HttpRequest requestFindByName = HttpRequest.GET(String.format("/?name=%s", testUser));
        List<MemberProfile> responseFindByName = client.toBlocking().retrieve(requestFindByName, Argument.of(List.class, mockMemberProfile.getClass()));  
        
        assertEquals(1, responseFindByName.size());
        assertEquals(testUser, responseFindByName.get(0).getName());
        assertEquals(testRole, responseFindByName.get(0).getRole());
    }

    // test Find By Role
    @Test
    public void testGetFindByRole() {

        setupTestData();

        HttpRequest requestFindByRole = HttpRequest.GET(String.format("/?role=%s", testRole));
        List<MemberProfile> responseFindByRole = client.toBlocking().retrieve(requestFindByRole, Argument.of(List.class, mockMemberProfile.getClass()));  
        
        assertEquals(1, responseFindByRole.size());
        assertEquals(testUser, responseFindByRole.get(0).getName());
        assertEquals(testRole, responseFindByRole.get(0).getRole());
    }

    // test Find By PdlId
    @Test
    public void testGetFindByPdlId() {

        setupTestData();

        HttpRequest requestFindByPdlId = HttpRequest.GET(String.format("/?pdlId=%s", testPdlId));
        List<MemberProfile> responseFindByPdlId = client.toBlocking().retrieve(requestFindByPdlId, Argument.of(List.class, mockMemberProfile.getClass()));  

        assertEquals(1, responseFindByPdlId.size());
        assertEquals(testUser, responseFindByPdlId.get(0).getName());
        assertEquals(testRole, responseFindByPdlId.get(0).getRole());
        assertEquals(testPdlId, responseFindByPdlId.get(0).getPdlId());
    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        MemberProfile testMemberProfile = new MemberProfile("testName", "test role", UUID.randomUUID(), "test location", "test email", "test InsperityId", testDate, "test bio");

        when(mockMemberProfileRepository.save(testMemberProfile)).thenReturn(testMemberProfile);

        final HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.POST("", fakeBody));
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        UUID testId = UUID.randomUUID();
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

        when(mockMemberProfileRepository.update(testMemberProfile)).thenReturn(testMemberProfile);

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

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", fakeBody));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    private void setupTestData() {
        
        if(!isDataSetupForTest) {
            HttpResponse<MemberProfile> responseFromPost = client.toBlocking().exchange(HttpRequest.POST("", fakeBody), MemberProfile.class);
            assertEquals(HttpStatus.CREATED, responseFromPost.getStatus());
            assertNotNull(responseFromPost.body());
            testUuid = responseFromPost.body().getUuid();

            isDataSetupForTest = true;
        }
    }
}
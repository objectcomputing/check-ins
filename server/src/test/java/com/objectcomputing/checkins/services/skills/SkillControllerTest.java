package com.objectcomputing.checkins.services.skills;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.sql.Date;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MicronautTest
public class SkillControllerTest {

    @Inject
    @Client("/skill")
    private HttpClient client;

    SkillRepository mockSkillRepository = mock(SkillRepository.class);
    Skill mockSkill = mock(Skill.class);

    private static Date testDate = new Date(System.currentTimeMillis());
    private static String testSkillName = "testName";
    private static boolean pending = false;
    private static boolean isDataSetupForGetTest = false;

    private static final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
        put("name", testSkillName);
        put("pending", true);
    }};

    @BeforeEach
    void setup() {
        reset(mockSkillRepository);
        reset(mockSkill);
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

        String testSkillName = "testSkill";
        Skill skill = new Skill();
        List<Skill> result = new ArrayList<Skill>();
        result.add(skill);

        when(mockSkillRepository.findByName("testSkill")).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/?name=%s", testSkillName)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // Find By Pending - when no skill data exists
    @Test
    public void testGetFindByPendingReturnsEmptyBody() {

        boolean testPending = false;
        Skill skill = new Skill();
        List<Skill> result = new ArrayList<Skill>();
        result.add(skill);

        when(mockSkillRepository.findByPending(false)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/?pending=%b", testPending)));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getContentLength());
    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        Skill testSkill = new Skill("testName");

        when(mockSkillRepository.save(testSkill)).thenReturn(testSkill);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.POST("", fakeBody));
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // POST - Invalid call
    @Test
    public void testPostNonExistingEndpointReturns404() {

        Skill testSkill = new Skill();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("/99", testSkill));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        UUID testId = UUID.randomUUID();
        Skill testSkill = new Skill("Name", false);
        testSkill.setSkillid(testId);

        Map<String, Object> fakeBody = new HashMap<String, Object>() {{
            put("skillid", testId);
            put("name", "updatedName");
            put("pending", false);
        }};

        when(mockSkillRepository.update(testSkill)).thenReturn(testSkill);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.PUT("/updatePending", fakeBody));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        Skill testSkill = new Skill();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("/updatePending", testSkill));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Request with invalid body - missing ID
    @Test
    public void testPutUpdateWithMissingField() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("/updatePending", fakeBody));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    private void setupTestData() {
        client.toBlocking().exchange(HttpRequest.POST("", fakeBody));
    }
}
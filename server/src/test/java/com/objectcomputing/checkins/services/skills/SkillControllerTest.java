package com.objectcomputing.checkins.services.skills;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.test.annotation.MicronautTest;
import org.apache.http.HttpEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
@Secured(SecurityRule.IS_ANONYMOUS)
public class SkillControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(SkillControllerTest.class);

    @Inject
    @Client("/skill")
    private HttpClient client;

    @Inject
    SkillController itemUnderTest;

    SkillServices mockSkillServices = mock(SkillServices.class);
    Skill mockSkill = mock(Skill.class);

    private static String testSkillName = "testSkillName";
    private static String testSkillName2 = "testSkillName2";
    private static boolean pending = true;

//    String fakeUuid = "d05c7870-16b2-4f3e-a5c6-e8e2c82a7f26";
    String fakeUuid = "12345678-9123-4567-abcd-123456789abc";
    String fakeUuid2 = "22345678-9123-4567-abcd-123456789abc";
//    String fakeResponseKey = "98765432-9876-9876-9876-987654321234";

    private static final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
        put("name", testSkillName);
        put("pending", true);
    }};

    private static final Map<String, Object> fakeBody2 = new HashMap<String, Object>() {{
        put("name", testSkillName2);
        put("pending", true);
    }};

    @BeforeEach
    void setup() {
        itemUnderTest.setSkillServices(mockSkillServices);
        reset(mockSkillServices);
        reset(mockSkill);
    }

    // failing bc returns bad request not not found
    @Test
    public void testFindNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/99"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // failing bc returns bad request not not found
    @Test
    public void testFindNonExistingEndpointReturnsNotFound() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/bar?order=foo"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By Name - when no user data exists
    // passes
    @Test
    public void testGetFindByNameReturnsEmptyBody() {

        String testSkillName = "testSkill";
        Skill skill = new Skill();
        List<Skill> result = new ArrayList<Skill>();
        result.add(skill);

        when(mockSkillServices.findByValue("testSkill", null)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/?name=%s", testSkillName)));

        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals(2, response.getContentLength());
    }

    // Find By Name
    //passing
    @Test
    public void testGetFindByName() {

        String testSkillName = "testSkill";
        Skill skill = new Skill();
        List<Skill> result = new ArrayList<Skill>();
        result.add(skill);

        when(mockSkillServices.findByValue("testSkill", null)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/?name=%s", testSkillName)));
        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(skill);
    }

    // Find By Pending
    // passing
    @Test
    public void testGetFindByPending() {

        boolean testPending = false;
        Skill skill = new Skill();
        skill.setPending(testPending);
        List<Skill> result = new ArrayList<Skill>();
        result.add(skill);

        when(mockSkillServices.findByValue("testSkill", testPending)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/?pending=%b", testPending)));
        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(skill);
//        assertEquals(5, response.getContentLength());
    }

    // Find By skillId - when skill data exists
    // fails - recursive update, datasource could not be loaded
    @Test
    public void testGetFindBySkillId_HappyPath() {

        UUID uuid = UUID.fromString(fakeUuid);
        Skill skill = new Skill();
        skill.setSkillid(uuid);
        skill.setName(testSkillName);
        skill.setPending(pending);
        List<Skill> result = new ArrayList<Skill>();
        result.add(skill);

        skill.setSkillid(UUID.fromString(fakeUuid));
        when(mockSkillServices.readSkill(uuid)).thenReturn(skill);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/%s", skill.getSkillid())));
        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(skill);
    }

    // POST - Valid Body
    // fails - already exists
    // todo make work

    @Test
    public void testPostSave() {

        UUID uuid = UUID.fromString(fakeUuid);
        Skill testSkill = new Skill("testName2", pending);
        testSkill.setSkillid(uuid);

   //     when(mockSkillRepository.save(testSkill)).thenReturn(testSkill);
        when(mockSkillServices.saveSkill(testSkill)).thenReturn(testSkill);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.POST("/", fakeBody));

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    // POST - Invalid call
    // fails - unauthorized with both testskill and fakebody
    @Test
    public void testPostNonExistingEndpointReturns404() {

        Skill testSkill = new Skill();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("/99", fakeBody));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // PUT - Valid Body
//    @Test
//    public void testPutUpdate() {
//
//        UUID testId = UUID.randomUUID();
//        Skill testSkill = new Skill("Name", false);
//        testSkill.setSkillid(testId);
//
//        Map<String, Object> fakeBody = new HashMap<String, Object>() {{
//            put("skillid", testId);
//            put("name", "updatedName");
//            put("pending", false);
//        }};
//
//        when(mockSkillRepository.update(testSkill)).thenReturn(testSkill);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest.PUT("/updatePending", fakeBody));
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertNotNull(response.getContentLength());
//    }

    // PUT - Request with empty body
//    @Test
//    public void testPutUpdateForEmptyInput() {
//        Skill testSkill = new Skill();
//        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//            client.toBlocking().exchange(HttpRequest.PUT("/updatePending", testSkill));
//        });
//        assertNotNull(thrown);
//        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
//    }

    // PUT - Request with invalid body - missing ID
//    @Test
//    public void testPutUpdateWithMissingField() {
//
//        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//            client.toBlocking().exchange(HttpRequest.PUT("/updatePending", fakeBody));
//        });
//        assertNotNull(thrown);
//        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
//    }

}
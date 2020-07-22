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

    String fakeUuid = "12345678-9123-4567-abcd-123456789abc";
    String fakeUuid2 = "22345678-9123-4567-abcd-123456789abc";

    private static final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
        put("name", testSkillName);
        put("pending", true);
    }};

    @BeforeEach
    void setup() {
        itemUnderTest.setSkillServices(mockSkillServices);
        reset(mockSkillServices);
        reset(mockSkill);
    }

    @Test
    public void testGETNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testGETFindByNameReturnsEmptyBody() {

        String testSkillName = "testSkill";
        Skill skill = new Skill();
        List<Skill> result = new ArrayList<Skill>();
        result.add(skill);

        when(mockSkillServices.findByValue("testSkill", null)).thenReturn(result);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/?name=%s", testSkillName)));

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindByValue_Name() {

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

    @Test
    public void testGETFindByValue_Pending() {

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

    }

    @Test
    public void testGETGetById_HappyPath() {

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

    @Test
    public void testPOSTCreateASkill() {


        Skill testSkill = new Skill("testName2", pending);

        when(mockSkillServices.saveSkill(testSkill)).thenReturn(testSkill);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.POST("/", testSkill));

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    @Test
    public void testPOSTCreateASkill_Null_Skill() {

        Skill testSkill = new Skill("testName", pending);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("/", testSkill));
        });

        // should throw a 409 - already exists
        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
    }

}
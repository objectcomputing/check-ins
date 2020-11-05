package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(environments = {"local", "localtest"}, transactional = false)
public class LocalLoginControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Client("/oauth/login/google")
    @Inject
    HttpClient client;

    @Test
    void testGetLogin() {
        HttpRequest<?> request = HttpRequest.GET("");
        String response = client.toBlocking().retrieve(request);
        assertEquals("<!DOCTYPE HTML>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Login</title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Local Login</h1>\n" +
                "<form action=\"/oauth/login/google\" method=\"post\">\n" +
                "    <p>Email: <input type=\"text\" name=\"email\"/></p>\n" +
                "    <p>Role Override: <input type=\"text\" name=\"role\"/></p>\n" +
                "    <p><input type=\"submit\" value=\"Submit\" /> </p>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>", response.trim());
    }

    @Test
    void testPostLogin() {
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", "ADMIN", "role", "SUPER"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertNotNull(response);
        assertTrue(response.contains("\"roles\":[\"ADMIN\",\"PDL\",\"MEMBER\"]"));
        assertTrue(response.contains("\"username\":\"ADMIN\""));
        assertTrue(response.contains("\"access_token\":\""));
    }

    @Test
    void testPostLoginUnauthorized() {
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", "ADMIN", "role", "DOES_NOT_EXIST"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpClientResponseException response = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().retrieve(request));
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals("Invalid role selected DOES_NOT_EXIST", response.getMessage());
    }

    @Test
    void testPostLoginAlreadyExistingUserNoOverrides() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        createDefaultRole(RoleType.ADMIN, memberProfileEntity);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", memberProfileEntity.getWorkEmail(), "role", ""))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertNotNull(response);
        assertTrue(response.contains("\"roles\":[\"ADMIN\"]"));
        assertTrue(response.contains(String.format("\"username\":\"%s\"", memberProfileEntity.getWorkEmail())));
        assertTrue(response.contains("\"access_token\":\""));
    }

    @Test
    void testPostLoginAlreadyExistingUserWithOverrides() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        createDefaultRole(RoleType.ADMIN, memberProfileEntity);
        createDefaultRole(RoleType.PDL, memberProfileEntity);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", memberProfileEntity.getWorkEmail(), "role", RoleType.Constants.MEMBER_ROLE))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertNotNull(response);
        assertTrue(response.contains("\"roles\":[\"MEMBER\"]"));
        assertTrue(response.contains(String.format("\"username\":\"%s\"", memberProfileEntity.getWorkEmail())));
        assertTrue(response.contains("\"access_token\":\""));
    }

    @Test
    void testPostLoginDoesNotThrowNullPointerIfUserNameIsNull() {
        MemberProfileEntity memberProfile = createADefaultMemberProfile();
        memberProfile.setName(null);
        createDefaultRole(RoleType.ADMIN, memberProfile);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", memberProfile.getWorkEmail(), "role", ""))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertNotNull(response);
        assertTrue(response.contains("\"roles\":[\"ADMIN\"]"));
        assertTrue(response.contains(String.format("\"username\":\"%s\"", memberProfile.getWorkEmail())));
        assertTrue(response.contains("\"access_token\":\""));
    }
}
package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
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
        assertEquals("<!DOCTYPE HTML>\r\n" +
                "<html>\r\n" +
                "<head>\r\n" +
                "    <title>Login</title>\r\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n" +
                "</head>\r\n" +
                "<body>\r\n" +
                "<h1>Local Login</h1>\r\n" +
                "<form action=\"/oauth/login/google\" method=\"post\">\r\n" +
                "    <p>Email: <input type=\"text\" name=\"email\"/></p>\r\n" +
                "    <p>Role Override: <input type=\"text\" name=\"role\"/></p>\r\n" +
                "    <p><input type=\"submit\" value=\"Submit\" /> </p>\r\n" +
                "</form>\r\n" +
                "</body>\r\n" +
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.ADMIN, memberProfile);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", memberProfile.getWorkEmail(), "role", ""))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertNotNull(response);
        assertTrue(response.contains("\"roles\":[\"ADMIN\"]"));
        assertTrue(response.contains(String.format("\"username\":\"%s\"", memberProfile.getWorkEmail())));
        assertTrue(response.contains("\"access_token\":\""));
    }

    @Test
    void testPostLoginAlreadyExistingUserWithOverrides() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.ADMIN, memberProfile);
        createDefaultRole(RoleType.PDL, memberProfile);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", memberProfile.getWorkEmail(), "role", RoleType.Constants.MEMBER_ROLE))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertNotNull(response);
        assertTrue(response.contains("\"roles\":[\"MEMBER\"]"));
        assertTrue(response.contains(String.format("\"username\":\"%s\"", memberProfile.getWorkEmail())));
        assertTrue(response.contains("\"access_token\":\""));
    }
}
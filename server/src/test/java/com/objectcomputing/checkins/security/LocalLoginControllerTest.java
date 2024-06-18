package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.Environments;
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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = {Environments.LOCAL, Environments.LOCALTEST}, transactional = false)
class LocalLoginControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Client("/oauth/login/google")
    @Inject
    HttpClient client;

    @Test
    void testGetLogin() {
        HttpRequest<?> request = HttpRequest.GET("");
        String response = client.toBlocking().retrieve(request);
        assertEquals("<!DOCTYPE HTML>" +
                "<html>" +
                "<head>" +
                "    <title>Login</title>" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                "</head>" +
                "<body>" +
                "<h1>Local Login</h1>" +
                "<form action=\"/oauth/login/google\" method=\"post\">" +
                "    <p>Email: <input type=\"text\" name=\"email\"/></p>" +
                "    <p>Role Override: <input type=\"text\" name=\"role\"/></p>" +
                "    <p><input type=\"submit\" value=\"Submit\" /> </p>" +
                "</form>" +
                "</body>" +
                "</html>", response.replace(System.getProperty("line.separator"), ""));
    }

    @Test
    void testPostLogin() {
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", "ADMIN", "role", "SUPER"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        Publisher<String> resp = client.retrieve(request);
        StepVerifier.create(resp)
                .thenConsumeWhile(response -> {
                    assertTrue(response.contains("\"roles\":"));
                    assertTrue(response.contains("\"roles\":"));
                    assertTrue(response.contains("\"ADMIN\""));
                    assertTrue(response.contains("\"PDL\""));
                    assertTrue(response.contains("\"MEMBER\""));
                    assertTrue(response.contains("\"username\":\"ADMIN\""));
                    assertTrue(response.contains("\"access_token\":\""));
                    return true;
                })
                .expectComplete()
                .verify();
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
        createAndAssignRole(RoleType.ADMIN, memberProfile);
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
        createAndAssignRole(RoleType.ADMIN, memberProfile);
        createAndAssignRole(RoleType.PDL, memberProfile);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", memberProfile.getWorkEmail(), "role", RoleType.Constants.MEMBER_ROLE))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertNotNull(response);
        assertTrue(response.contains("\"roles\":[\"MEMBER\"]"));
        assertTrue(response.contains(String.format("\"username\":\"%s\"", memberProfile.getWorkEmail())));
        assertTrue(response.contains("\"access_token\":\""));
    }

    @Test
    void testPostLoginDoesNotThrowNullPointerIfUserNameIsNull() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        memberProfile.setFirstName(null);
        createAndAssignRole(RoleType.ADMIN, memberProfile);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("", Map.of("email", memberProfile.getWorkEmail(), "role", ""))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertNotNull(response);
        assertTrue(response.contains("\"roles\":[\"ADMIN\"]"));
        assertTrue(response.contains(String.format("\"username\":\"%s\"", memberProfile.getWorkEmail())));
        assertTrue(response.contains("\"access_token\":\""));
    }
}

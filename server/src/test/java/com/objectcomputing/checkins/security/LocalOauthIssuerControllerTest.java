package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(environments = "local")
public class LocalOauthIssuerControllerTest extends TestContainersSuite {

    @Client("/oauth")
    @Inject
    HttpClient client;

    public LocalOauthIssuerControllerTest() {
        super(false);
    }

    @Test
    void testGetLogin() {
        HttpRequest<?> request = HttpRequest.GET(String.format("/auth/?response_type=%s&redirect_uri=%s&state=%s&client_id=%s",
                "1", "2", "3", "4"));
        String response = client.toBlocking().retrieve(request);
        assertEquals("<!DOCTYPE HTML>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Login</title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Local Login</h1>\n" +
                "<form action=\"/oauth/auth\" method=\"post\">\n" +
                "    <p>Email: <input type=\"text\" name=\"email\"/></p>\n" +
                "    <p>Role Override: <input type=\"text\" name=\"role\"/></p>\n" +
                "    <input type=\"hidden\" value=\"2\" name=\"redirectUri\" />\n" +
                "    <input type=\"hidden\" value=\"3\" name=\"state\" />\n" +
                "    <p><input type=\"submit\" value=\"Submit\" /> </p>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>", response);
    }

    @Test
    void testPostAuth() {
        HttpRequest<Map<String, String>> request = HttpRequest.POST("/auth", Map.of("email", "email",
                "role", "ADMIN", "redirectUri", "/fakeoauth", "state", "test")).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertEquals("{\"state\":\"test\",\"code\":\"{\\\"role\\\":\\\"ADMIN\\\",\\\"email\\\":\\\"email\\\"}\"}", response);
    }

    @Test
    void testPostAuthInvalidRole() {
        HttpRequest<Map<String, String>> request = HttpRequest.POST("/auth", Map.of("email", "email",
                "role", "DNE", "redirectUri", "/fakeoauth", "state", "test")).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        String response = client.toBlocking().retrieve(request);
        assertEquals("{\"error\":\"'DNE is an invalid role'\"}", response);
    }

    @Test
    void testPostToken() throws JsonProcessingException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("email", "email");
        map.put("role", "ADMIN");
        String code = new ObjectMapper().writeValueAsString(map);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("/token", Map.of("code", code)).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpResponse<TokenResponse> response = client.toBlocking().exchange(request, TokenResponse.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        TokenResponse tr = response.body();
        assertNotNull(tr);
        Map<?,?> cmpMap = new ObjectMapper().readValue(tr.getAccessToken(), Map.class);
        assertEquals(map, cmpMap);
        assertEquals("bearer", tr.getTokenType());
    }

    @Test
    void testPostTokenInvalidRole() throws JsonProcessingException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("email", "email");
        map.put("role", "DNE");
        String code = new ObjectMapper().writeValueAsString(map);
        HttpRequest<Map<String, String>> request = HttpRequest.POST("/token", Map.of("code", code)).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpClientResponseException response = Assertions.assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Controller("fakeoauth")
    @Secured(SecuredAnnotationRule.IS_ANONYMOUS)
    public static class FakeAuthCallback {

        @Get
        public Map<String, String> callback(@Nullable String code, @Nullable String state, @Nullable String error) {
            Map<String, String> map = new LinkedHashMap<>();
            if (state != null) {
                map.put("state", state);
            }
            if (code != null) {
                map.put("code", code);
            }
            if (error != null) {
                map.put("error", error);
            }
            return map;
        }
    }
}
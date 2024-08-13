package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.security.ImpersonationController;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import org.json.JSONObject;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = {Environments.LOCAL, Environments.LOCALTEST}, transactional = false)
class ImpersonationControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Client("/impersonation")
    @Inject
    HttpClient client;

    private MemberProfile nonAdmin;
    private MemberProfile admin;
    private String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJjb21wYW55IjoiRnV0dXJlRWQiLCJzdWIiOjEsImlzcyI6Imh0dHA6XC9cL2Z1dHVyZWVkLmRldlwvYXBpXC92MVwvc3R1ZGVudFwvbG9naW5cL3VzZXJuYW1lIiwiaWF0IjoiMTQyNzQyNjc3MSIsImV4cCI6IjE0Mjc0MzAzNzEiLCJuYmYiOiIxNDI3NDI2NzcxIiwianRpIjoiNmFlZDQ3MGFiOGMxYTk0MmE0MTViYTAwOTBlMTFlZTUifQ.MmM2YTUwMjEzYTE0OGNhNjk5Y2Y2MjEwZDdkN2Y1OTQ2NWVhZTdmYmI4OTA5YmM1Y2QwYTMzZjUwNTgwY2Y0MQ";

    @BeforeEach
    void setUp() {
        createAndAssignRoles();

        nonAdmin = createADefaultMemberProfile();

        admin = createASecondDefaultMemberProfile();
        assignAdminRole(admin);
    }

    @Test
    void testPostBeginEnd() {
        HttpRequest<Map<String, String>> request =
            HttpRequest.POST("/begin",
                             Map.of("email", nonAdmin.getWorkEmail()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        ((MutableHttpRequest)request).cookie(
                                 Cookie.of(ImpersonationController.JWT, jwt));
        Publisher<String> response = client.retrieve(request);
        assertNotNull(response);
        final StringBuilder json = new StringBuilder();
        StepVerifier.create(response)
                .thenConsumeWhile(resp -> {
                    assertTrue(resp.contains("\"username\":\"" +
                                             nonAdmin.getWorkEmail()));
                    assertTrue(!resp.contains(jwt));
                    json.append(resp);
                    return true;
                })
                .expectComplete()
                .verify();

        JSONObject jsonObject = new JSONObject(json.toString());
        MutableHttpRequest<Object> next = HttpRequest.GET("/end")
                              .basicAuth(nonAdmin.getWorkEmail(), MEMBER_ROLE);
        next.cookies(
          Set.of(Cookie.of(ImpersonationController.originalJWT, jwt),
                 Cookie.of(ImpersonationController.JWT,
                           jsonObject.get("access_token").toString())));
        response = client.retrieve(next);
        assertNotNull(response);
        // This just needs to complete in order to verify that it has succeeded.
        StepVerifier.create(response)
                .thenConsumeWhile(resp -> {
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testGetEndNoOJWT() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/end")
                              .basicAuth(nonAdmin.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException response =
            assertThrows(HttpClientResponseException.class,
                         () -> client.toBlocking().retrieve(request));
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testPostUnauthorizedBegin() {
        HttpRequest<Map<String, String>> request =
            HttpRequest.POST("/begin",
                             Map.of("email", admin.getWorkEmail()))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpClientResponseException response =
            assertThrows(HttpClientResponseException.class,
                         () -> client.toBlocking().retrieve(request));
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals("Unauthorized", response.getMessage());
    }

    @Test
    void testGetUnauthorizedEnd() {
        HttpRequest<Map<String, String>> request =
            HttpRequest.GET("/end");
        HttpClientResponseException response =
            assertThrows(HttpClientResponseException.class,
                         () -> client.toBlocking().retrieve(request));
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals("Unauthorized", response.getMessage());
    }
}

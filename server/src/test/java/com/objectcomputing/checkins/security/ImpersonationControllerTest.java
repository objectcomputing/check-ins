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
    private String jwt = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVlMTkzZDQ2NDdhYjRhMzU4NWFhOWIyYjNiNDg0YTg3YWE2OGJiNDIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI4MzIxNDAwMjA1OTMtMTJxNWh2b2psajc0N24xdnV1cG9rNXZ2aTk5NXJlYzIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI4MzIxNDAwMjA1OTMtMTJxNWh2b2psajc0N24xdnV1cG9rNXZ2aTk5NXJlYzIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTczOTEzNDIwMzMxOTg1MTI1ODEiLCJoZCI6Im9iamVjdGNvbXB1dGluZy5jb20iLCJlbWFpbCI6ImtpbWJlcmxpbm1Ab2JqZWN0Y29tcHV0aW5nLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoibENmaWkyWEdYaFEtOC1TeEs2N2Z5QSIsIm5vbmNlIjoiYzRkNTkyNjQtYmY3Ny00M2MwLTliMGMtYjZhYzZjNzgxN2Y1IiwibmFtZSI6Ik1pY2hhZWwgS2ltYmVybGluIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FDZzhvY0tIRHNOdlprclpaUS0xN1NiTTJqNWdXdDdpa09SOTI2UXZ0WFZSSnU5cTRRQl82UHZ4PXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Ik1pY2hhZWwiLCJmYW1pbHlfbmFtZSI6IktpbWJlcmxpbiIsImlhdCI6MTc0MjQxMjQxMiwiZXhwIjoxNzQyNDE2MDEyfQ.gVgncacfKyzr5_NSMOAu7xGRKnlBA_tg_1JCcfJ4KBMa0Xnvvhhx9Fix252_SQ5xpaG7b-sDApEl1fTcMKqUfYNrj-5s3SzWzCoV6g4NI44YN1j_KyjKFUZ6RWJ79U1U8_DA_wBATQvA-_NNMT8WL9A4muolH-cjoXixymCkl6dgd5QjriEOQC20QYCSbp_nHpeAgbl6fvCh8ZvpK2bHb6zwDmjYuN_xRuQyztfKS1X24nSB0k840Jdw2kUSzXZITUE6zOskapYKlTxcP8BGQ3GPydrNFxGy9Ec6GR3I0J-QhcE5b4mBijae9hgWw6Yz93SSjpm_w8w9D_fO_6yVSA";

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
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
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

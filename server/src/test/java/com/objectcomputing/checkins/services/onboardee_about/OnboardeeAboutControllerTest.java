package com.objectcomputing.checkins.services.onboardee_about;

import static com.objectcomputing.checkins.services.onboardee_about.OnboardeeAboutTestUtil.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.OnboardeeAboutFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import com.objectcomputing.checkins.services.onboard.onboardee_about.OnboardeeAbout;
import com.objectcomputing.checkins.services.onboard.onboardee_about.OnboardeeAboutCreateDTO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OnboardeeAboutControllerTest extends TestContainersSuite
        implements OnboardeeAboutFixture, MemberProfileFixture, RoleFixture {
    @Inject
    @Client("/services/onboardee-about")
    private HttpClient client;

    @Test
    public void testGETGetById() {

        OnboardeeAbout onboardeeAbout = createADefaultOnboardeeAbout();

        final HttpRequest<Object> request = HttpRequest.GET(String.format("%s", onboardeeAbout.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<OnboardeeAboutResponseDTO> response = client.toBlocking().exchange(request,
                OnboardeeAboutResponseDTO.class);

        assertAboutEquals(onboardeeAbout, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testPOSTCreateANullAboutYou() {
        OnboardeeAboutCreateDTO onboardeeAboutCreateDTO = new OnboardeeAboutCreateDTO();

        final HttpRequest<OnboardeeAboutCreateDTO> request = HttpRequest
                .POST("/", onboardeeAboutCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPOSTCreateAOnboardeeAbout() {
        OnboardeeAboutResponseDTO dto = mkUpdateOnboardeeAbout();

        final HttpRequest<?> request = HttpRequest
                .POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<OnboardeeAbout> response = client.toBlocking().exchange(request, OnboardeeAbout.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(dto.getTshirtSize(), response.body().getTshirtSize());
        assertEquals(dto.getGoogleTraining(), response.body().getGoogleTraining());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()),
                "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateAboutYou() {
        OnboardeeAbout onboardeeAbout = createADefaultOnboardeeAbout();

        OnboardeeAboutResponseDTO onboardeeAboutResponseDTO = toDto(onboardeeAbout);

        onboardeeAboutResponseDTO.setAdditionalSkills("Yes, I have mad skills");

        final HttpRequest<OnboardeeAboutResponseDTO> request = HttpRequest.PUT("/", onboardeeAboutResponseDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<OnboardeeAboutResponseDTO> response = client.toBlocking().exchange(request,
                OnboardeeAboutResponseDTO.class);

        assertEquals(onboardeeAboutResponseDTO, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotEquals(onboardeeAbout.getAdditionalSkills(), response.body().getAdditionalSkills());
        assertEquals(String.format("%s/%s", request.getPath(), onboardeeAboutResponseDTO.getId()),
                "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testDELETEAboutYou() {
        OnboardeeAbout onboardeeAbout = createADefaultOnboardeeAbout();

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(onboardeeAbout.getId().toString())
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest
                .GET(String.format("/%s", onboardeeAbout.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));
    
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
           
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No new about you information for id " + onboardeeAbout.getId(), error);
    }
}

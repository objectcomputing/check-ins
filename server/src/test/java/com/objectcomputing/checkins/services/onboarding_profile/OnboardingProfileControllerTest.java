package com.objectcomputing.checkins.services.onboarding_profile;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.OnboardingFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.*;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileCreateDTO;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileDTO;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.Resource;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.services.onboarding_profile.OnboardingProfileTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OnboardingProfileControllerTest extends TestContainersSuite implements
        MemberProfileFixture, OnboardingFixture, RoleFixture {

        @Inject
        @Client("/services/onboardee-profiles")
        private HttpClient client;

        //TODO: Use Util.MAX instead of defining variable
        /*
         * LocalDate.Max cannot be used for end-to-end tests
         * LocalDate.Max year = 999999999
         * POSTGRES supported date range = 4713 BC - 5874897 AD
         */
        private final LocalDate maxDate = LocalDate.of(2099, 12, 31);

        private String encodeValue(String value) throws UnsupportedEncodingException {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        }

        @Test
        public void testGETAllOnboardees() {

                OnboardingProfile onboardingProfile = createADefaultOnboardeeProfile();
                OnboardingProfile onboardingProfile2 = createSecondOnboardeeProfile();
//                List<OnboardingProfile> onboardees= new ArrayList<>(2);

                final HttpRequest<Object> request = HttpRequest.
                        GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE);

                final HttpResponse<List<OnboardingProfileDTO>> response = client.toBlocking().exchange(request, Argument.listOf(OnboardingProfileDTO.class));
                final List<OnboardingProfileDTO> results = response.body();

                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(2, results.size());

                results.stream().forEach((OnboardingProfileDTO current)->{
                        assertTrue(current.getId().equals(onboardingProfile.getId()) || current.getId().equals(onboardingProfile.getId()));
                        }
                );

        }
        @Test
        public void testGETGetById() {

                OnboardingProfile onboardingProfile = createADefaultOnboardeeProfile();

                final HttpRequest<Object> request = HttpRequest.
                        GET(String.format("/%s", onboardingProfile.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

                final HttpResponse<OnboardingProfileDTO> response = client.toBlocking().exchange(request, OnboardingProfileDTO.class);

                assertProfilesEqual(onboardingProfile, response.body());
                assertEquals(HttpStatus.OK, response.getStatus());
        }

        @Test
        public void testGETGetByIdNotFound() {

                final HttpRequest<Object> request = HttpRequest.
                        GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

                HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request, Map.class));

                assertNotNull(responseException.getResponse());
                assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        }


      @Test
        public void testPOSTCreateAOnboardeeProfile() {

                OnboardingProfileDTO dto = mkUpdateOnboardeeProfileDTO();

                final HttpRequest<?> request = HttpRequest.
                        POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                final HttpResponse<OnboardingProfile> response = client.toBlocking().exchange(request, OnboardingProfile.class);

                assertNotNull(response);
                assertEquals(HttpStatus.CREATED, response.getStatus());
                assertEquals(dto.getFirstName(), response.body().getFirstName());
                assertEquals(dto.getLastName(), response.body().getLastName());
                assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
        }

         @Test
        public void testPOSTCreateANullOnboardeeProfile() {

             OnboardingProfileCreateDTO onboardeeProfileCreateDTO = new OnboardingProfileCreateDTO();

                final HttpRequest<OnboardingProfileCreateDTO> request = HttpRequest.
                        POST("/", onboardeeProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request, Map.class));

                assertNotNull(responseException.getResponse());
                assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        }

        @Test
        public void testPUTUpdateOnboardeeProfile() {
                OnboardingProfile firstProfile= createADefaultOnboardeeProfile();
                OnboardingProfileDTO profileUpdateDTO = toDto(firstProfile);

                profileUpdateDTO.setFirstName("Sally");

                final HttpRequest<OnboardingProfileDTO> request = HttpRequest.PUT("/", profileUpdateDTO)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                final HttpResponse<OnboardingProfileDTO> response = client.toBlocking().exchange(request, OnboardingProfileDTO.class);

                assertEquals(profileUpdateDTO, response.body());
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(String.format("%s/%s", request.getPath(), profileUpdateDTO.getId()), "/services" + response.getHeaders().get("location"));
        }

        @Test
        public void testPUTUpdateNonexistentOnboardeeProfile() {

                OnboardingProfileCreateDTO onboardeeProfileCreateDTO = new OnboardingProfileCreateDTO();
                onboardeeProfileCreateDTO.setFirstName("Pat");
                onboardeeProfileCreateDTO.setLastName("Patterson");

                final HttpRequest<OnboardingProfileCreateDTO> request = HttpRequest.
                        PUT("/", onboardeeProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request, Map.class));

                assertNotNull(responseException.getResponse());
                assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        }

        @Test
        public void testPUTUpdateNullOnboardeeProfile() {

                final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request, Map.class));

                assertNotNull(responseException.getResponse());
                assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        }

        @Test
        public void testPostWithNullName() {

                OnboardingProfileCreateDTO requestBody = mkCreateOnboardeeProfileDTO();
                requestBody.setFirstName(null);

                final HttpRequest<OnboardingProfileCreateDTO> request = HttpRequest.POST("", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request, Map.class));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        }

        @Test
        public void testPutValidationFailures() {

                final HttpRequest<OnboardingProfileDTO> request = HttpRequest.PUT("", new OnboardingProfileDTO())
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request, Map.class));
                JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
                JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");

                assertEquals(4, errors.size());
                assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        }

      @Test
      public void testPutUpdateForEmptyInput() {
              OnboardingProfileDTO testOnboardeeProfile = mkUpdateOnboardeeProfileDTO();
              testOnboardeeProfile.setId(null);
              HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(HttpRequest.PUT("", testOnboardeeProfile)
                     .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));
              JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
              JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
              assertEquals("onboardee Profile.id: must not be null", error.asText());
              assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        }

    @Test
    public void testDeleteOnboardee() {

        OnboardingProfile onboardingProfile = createADefaultOnboardeeProfile();

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(onboardingProfile.getId().toString())
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure profile is Deleted and not Terminated
        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest.GET(String.format("/%s", onboardingProfile.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No onboardee profile for id " + onboardingProfile.getId(), error);
    }
}

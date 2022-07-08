package com.objectcomputing.checkins.services.onboarding_profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.OnboardingFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.*;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileCreateDTO;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileResponseDTO;
import com.objectcomputing.checkins.services.onboardeeprofile.Onboarding_Profile;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.Resource;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static com.objectcomputing.checkins.services.onboarding_profile.OnboardingProfileTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

                Onboarding_Profile onboardingProfile = createADefaultOnboardeeProfile();

                final HttpRequest<Object> request = HttpRequest.
                        GET(String.format("/%s", onboardingProfile.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

                final HttpResponse<Onboarding_Profile> response = client.toBlocking().exchange(request, Onboarding_Profile.class);

                assertEquals(onboardingProfile, response.body());
                assertEquals(HttpStatus.OK, response.getStatus());
        }
        @Test
        public void testGETGetByIdHappyPath() {

                Onboarding_Profile onboardingProfile = createADefaultOnboardeeProfile();

                final HttpRequest<Object> request = HttpRequest.
                        GET(String.format("/%s", onboardingProfile.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

                final HttpResponse<Onboarding_Profile> response = client.toBlocking().exchange(request, Onboarding_Profile.class);

                assertEquals(onboardingProfile, response.body());
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
//Response DTO is used in place of a UpdateDTO
      @Test
        public void testPOSTCreateAOnboardeeProfile() {

                OnboardingProfileResponseDTO dto = mkUpdateOnboardeeProfileDTO();
                Onboarding_Profile onboardingProfile = createADefaultOnboardeeProfile();

                final HttpRequest<?> request = HttpRequest.
                        POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                final HttpResponse<Onboarding_Profile> response = client.toBlocking().exchange(request, Onboarding_Profile.class);

                assertNotNull(response);
                assertEquals(HttpStatus.CREATED, response.getStatus());
                assertEquals(onboardingProfile.getFirstName(dto), onboardingProfile.getFirstName(response.body()));
                assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
        }

        //ResponseDTO used instead of CreateDTO
         @Test
        public void testPOSTCreateANullMemberProfile() {

            OnboardingProfileResponseDTO onboardeeProfileCreateDTO = new OnboardingProfileCreateDTO();

                final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.
                        POST("/", onboardeeProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request, Map.class));

                assertNotNull(responseException.getResponse());
                assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        }

        @Test
        public void testPUTUpdateMemberProfile() {
                OnboardingProfileResponseDTO profileUpdateDTO = mkUpdateOnboardeeProfileDTO();

                final HttpRequest<OnboardingProfileResponseDTO> request = HttpRequest.PUT("/", profileUpdateDTO)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                final HttpResponse<MemberProfileResponseDTO> response = client.toBlocking().exchange(request, OnboardingProfileResponseDTO.class);

                assertProfilesEqual(profileUpdateDTO, response.body());
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(String.format("%s/%s", request.getPath(), profileUpdateDTO.getId()), "/services" + response.getHeaders().get("location"));
        }

//        @Test
//        public void testPUTUpdateNonexistentMemberProfile() {
//
//                MemberProfileCreateDTO memberProfileCreateDTO = new MemberProfileCreateDTO();
//                memberProfileCreateDTO.setFirstName("reincarnation");
//                memberProfileCreateDTO.setLastName("gentleman");
//
//                final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.
//                        PUT("/", memberProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//                HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                        () -> client.toBlocking().exchange(request, Map.class));
//
//                assertNotNull(responseException.getResponse());
//                assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
//        }
//
//        @Test
//        public void testPUTUpdateNullMemberProfile() {
//
//                final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//                HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                        () -> client.toBlocking().exchange(request, Map.class));
//
//                assertNotNull(responseException.getResponse());
//                assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
//        }
//
//        @Test
//        public void testPostWithNullName() {
//
//                MemberProfileCreateDTO requestBody = mkCreateMemberProfileDTO();
//                requestBody.setFirstName(null);
//
//                final HttpRequest<MemberProfileCreateDTO> request = HttpRequest.POST("", requestBody)
//                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//                HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
//                        () -> client.toBlocking().exchange(request, Map.class));
//
//                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//        }
//
//        @Test
//        public void testPutValidationFailures() {
//
//                final HttpRequest<MemberProfileUpdateDTO> request = HttpRequest.PUT("", new MemberProfileUpdateDTO())
//                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//                HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
//                        () -> client.toBlocking().exchange(request, Map.class));
//
//                JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
//                JsonNode errors = Objects.requireNonNull(body).get(Resource.EMBEDDED).get("errors");
//
//                assertEquals(4, errors.size());
//                assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
//        }
//
//        @Test
//        public void testPutUpdateForEmptyInput() {
//                MemberProfileUpdateDTO testMemberProfile = mkUpdateMemberProfileDTO();
//                testMemberProfile.setId(null);
//                HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(HttpRequest.PUT("", testMemberProfile)
//                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE)));
//                JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
//                JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
//                assertEquals("memberProfile.id: must not be null", error.asText());
//                assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
//        }

//    @Test
//    public void testDeleteHappyPath() {
//
//        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
//        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);
//
//        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
//        createAndAssignAdminRole(memberProfileOfAdmin);
//
//        final HttpRequest request = HttpRequest.DELETE(memberProfileOfMember.getId().toString())
//                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
//
//        final HttpResponse<?> response = client.toBlocking().exchange(request);
//        assertEquals(HttpStatus.OK, response.getStatus());
//
//        // Ensure profile is Deleted and not Terminated
//        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest.GET(String.format("/%s", memberProfileOfMember.getId()))
//                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//
//        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));
//
//        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//        String error = Objects.requireNonNull(body).get("message").asText();
//        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
//
//        assertEquals(request.getPath(), href);
//        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
//        assertEquals("No member profile for id " + memberProfileOfMember.getId(), error);
//    }
}

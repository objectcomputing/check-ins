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
import com.objectcomputing.checkins.services.onboard.onboardee_about.OnboardeeAboutDTO;

import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
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

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.HR_ROLE;
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
    public void testGETById() {
        NewHireAccountEntity newHire = createNewHireAccountEntity();
        OnboardeeAbout onboardeeAbout = createWorkingEnvironment(newHire);

        final HttpRequest<Object> request = HttpRequest.GET(String.format("%s", onboardeeAbout.getId()))
                .basicAuth(HR_ROLE, HR_ROLE);

        final HttpResponse<OnboardeeAboutDTO> response = client.toBlocking().exchange(request,
                OnboardeeAboutDTO.class);

        assertEquals(toDto(onboardeeAbout), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testPOSTCreateANullEnvironment() {
        WorkingEnvironmentCreateDTO workingEnvironmentCreateDTO = new WorkingEnvironmentCreateDTO();

        final HttpRequest<WorkingEnvironmentCreateDTO> request = HttpRequest.POST("/", workingEnvironmentCreateDTO)
                .basicAuth(HR_ROLE, HR_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

//     @Test
//     public void testPOSTCreateAOnboardeeAbout() {
//         OnboardeeAboutDTO dto = mkUpdateOnboardeeAbout();

//         final HttpRequest<?> request = HttpRequest
//                 .POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//         final HttpResponse<OnboardeeAbout> response = client.toBlocking().exchange(request, OnboardeeAbout.class);

//         assertNotNull(response);
//         assertEquals(HttpStatus.CREATED, response.getStatus());
//         assertEquals(dto.getTshirtSize(), response.body().getTshirtSize());
//         assertEquals(dto.getGoogleTraining(), response.body().getGoogleTraining());
//         assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()),
//                 "/services" + response.getHeaders().get("location"));
//     }

//     @Test
//     public void testPUTUpdateAboutYou() {
//         OnboardeeAbout onboardeeAbout = createADefaultOnboardeeAbout();

//         OnboardeeAboutDTO onboardeeAboutDTO = toDto(onboardeeAbout);

//         onboardeeAboutDTO.setAdditionalSkills("Yes, I have mad skills");

//         final HttpRequest<OnboardeeAboutDTO> request = HttpRequest.PUT("/", onboardeeAboutDTO)
//                 .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//         final HttpResponse<OnboardeeAboutDTO> response = client.toBlocking().exchange(request,
//                 OnboardeeAboutDTO.class);

//         assertEquals(onboardeeAboutDTO, response.body());
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertNotEquals(onboardeeAbout.getAdditionalSkills(), response.body().getAdditionalSkills());
//         assertEquals(String.format("%s/%s", request.getPath(), onboardeeAboutDTO.getId()),
//                 "/services" + response.getHeaders().get("location"));
//     }

//     @Test
//     public void testDELETEAboutYou() {
//         OnboardeeAbout onboardeeAbout = createADefaultOnboardeeAbout();

//         MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
//         createAndAssignAdminRole(memberProfileOfAdmin);

//         final HttpRequest request = HttpRequest.DELETE(onboardeeAbout.getId().toString())
//                 .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

//         final HttpResponse<?> response = client.toBlocking().exchange(request);
//         assertEquals(HttpStatus.OK, response.getStatus());

//         final HttpRequest<Object> requestForAssertingDeletion = HttpRequest
//                 .GET(String.format("/%s", onboardeeAbout.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

//         HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                 () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));
    
//         JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
//         String error = Objects.requireNonNull(body).get("message").asText();
//         String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();
           
//         assertEquals(request.getPath(), href);
//         assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
//         assertEquals("No new about you information for id " + onboardeeAbout.getId(), error);
//     }
}

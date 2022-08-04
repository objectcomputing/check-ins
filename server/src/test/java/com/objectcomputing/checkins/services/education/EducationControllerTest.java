package com.objectcomputing.checkins.services.education;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.EducationFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.education.EducationTestUtil.*;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;

public class EducationControllerTest extends TestContainersSuite implements EducationFixture, RoleFixture, MemberProfileFixture {

    private static final Logger LOG = LoggerFactory.getLogger(EducationController.class);

    @Inject
    @Client("/services/education")
    private HttpClient client;

    @Test
    public void testGETGetById() {

        Education education = createEducation();

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", education.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<EducationDTO> response = client.toBlocking().exchange(request,
                EducationDTO.class);

        assertEducationEqual(education, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest
            .GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    public void testPOSTCreateEducation() {
        EducationDTO dto = mkUpdateEducationDTO();

        final HttpRequest<?> request = HttpRequest
            .POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Education> response = client.toBlocking().exchange(request, Education.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(dto.getHighestDegree(), response.body().getHighestDegree());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateEducation() {
        Education firstEducation = createEducation();
        EducationDTO educationDTO = toDto(firstEducation);

        educationDTO.setAdditionalInfo("Hello! :)");

        final HttpRequest<EducationDTO> request = HttpRequest.PUT("/", educationDTO)
                                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<EducationDTO> response = client.toBlocking().exchange(request, EducationDTO.class);
        
        assertEquals(educationDTO, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), educationDTO.getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testDeleteEducation(){
        Education education = createEducation();

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(education.getId().toString())
            .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest.GET(String.format("/%s", education.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
            () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No education found for id " + education.getId(), error);
    }
}

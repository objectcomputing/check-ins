package com.objectcomputing.checkins.services.workingenvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.json.Json;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.WorkingEnvironmentFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.workingenvironment.WorkingEnvironmentTestUtil.*;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;

public class WorkingEnvironmentControllerTest extends TestContainersSuite
        implements WorkingEnvironmentFixture, RoleFixture, MemberProfileFixture {
    private static final Logger LOG = LoggerFactory.getLogger(WorkingEnvironmentController.class);

    @Inject
    @Client("/services/working-environment")
    private HttpClient client;

    @Test
    public void testGETGeyById() {

        WorkingEnvironment workingEnvironment = createWorkingEnvironment();

        final HttpRequest<Object> request = HttpRequest.GET(String.format("%s", workingEnvironment.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        final HttpResponse<WorkingEnvironmentResponseDTO> response = client.toBlocking().exchange(request,
                WorkingEnvironmentResponseDTO.class);

        assertEnvironmentEquals(workingEnvironment, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testPOSTCreateANullEnvironment() {
        WorkingEnvironmentCreateDTO workingEnvironmentCreateDTO = new WorkingEnvironmentCreateDTO();

        final HttpRequest<WorkingEnvironmentCreateDTO> request = HttpRequest.POST("/", workingEnvironmentCreateDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPOSTCreateWorkingEnvironment() {
        WorkingEnvironmentResponseDTO dto = mkUpdateWorkingEnvironment();

        final HttpRequest<?> request = HttpRequest.POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<WorkingEnvironment> response = client.toBlocking().exchange(request,
                WorkingEnvironment.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(dto.getKeyType(), response.body().getKeyType());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()),
                "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateWorkingEnvironment() {
        WorkingEnvironment workingEnvironment = createWorkingEnvironment();

        WorkingEnvironmentResponseDTO workingEnvironmentResponseDTO = toDto(workingEnvironment);

        workingEnvironmentResponseDTO.setOsType("Linux");

        final HttpRequest<WorkingEnvironmentResponseDTO> request = HttpRequest.PUT("/", workingEnvironmentResponseDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<WorkingEnvironmentResponseDTO> response = client.toBlocking().exchange(request,
                WorkingEnvironmentResponseDTO.class);

        assertEquals(workingEnvironmentResponseDTO, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotEquals(workingEnvironment.getOsType(), response.body().getOsType());
        assertEquals(String.format("%s/%s", request.getPath(), workingEnvironmentResponseDTO.getId()),
                "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testDELETEWorkingEnvironment() {
        WorkingEnvironment workingEnvironment = createWorkingEnvironment();

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(workingEnvironment.getId().toString()).basicAuth(
                memberProfileOfAdmin.getWorkEmail(),
                ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest
                .GET(String.format("/%s", workingEnvironment.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));
    
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href= Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No new working environment info for id " + workingEnvironment.getId(), error);
        }
}

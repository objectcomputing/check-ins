package com.objectcomputing.checkins.services.employmenthistory;

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
import com.objectcomputing.checkins.services.fixture.EmploymentHistoryFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.employmenthistory.EmploymentHistory;
import com.objectcomputing.checkins.services.employmenthistory.EmploymentHistoryDTO;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.employmenthistory.EmploymentHistoryTestUtil.*;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;

public class EmploymentHistoryControllerTest extends TestContainersSuite
        implements EmploymentHistoryFixture, RoleFixture, MemberProfileFixture {

    private static final Logger LOG = LoggerFactory.getLogger(EmploymentHistoryController.class);

    @Inject
    @Client("/services/employment-history")
    private HttpClient client;

    @Test
    public void testGETGetById() {

        EmploymentHistory employmentHistory = createEmploymentHistory();

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", employmentHistory.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
                
        final HttpResponse<EmploymentHistoryDTO> response = client.toBlocking().exchange(request,
                EmploymentHistoryDTO.class);

        assertHistoryEqual(employmentHistory, response.body());
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
    public void testPOSTCreateAnEmploymentHistory(){
        EmploymentHistoryDTO dto = mkUpdateEmploymentHistoryDTO();

        final HttpRequest<?> request = HttpRequest.POST("/", dto)
                                                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<EmploymentHistory> response = client.toBlocking().exchange(request, EmploymentHistory.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(dto.getCompany(), response.body().getCompany());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateHistory() {
        EmploymentHistory employmentHistory = createEmploymentHistory();
        EmploymentHistoryDTO employmentHistoryDTO = toDto(employmentHistory);

        employmentHistoryDTO.setCompany("Google");

        final HttpRequest<EmploymentHistoryDTO> request = HttpRequest.PUT("/", employmentHistoryDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<EmploymentHistoryDTO> response = client.toBlocking().exchange(request, EmploymentHistoryDTO.class);

        assertEquals(employmentHistoryDTO, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), employmentHistoryDTO.getId()), "/services" + response.getHeaders().get("location"));
    }

    @Test
    public void testDeleteHistory() {
        EmploymentHistory employmentHistory = createEmploymentHistory();

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest request = HttpRequest.DELETE(employmentHistory.getId().toString())
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        final HttpRequest<Object> requestForAssertingDeletion = HttpRequest.GET(String.format("/%s", employmentHistory.getId()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
        () -> client.toBlocking().exchange(requestForAssertingDeletion, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals("No onboardee profile found for id " + employmentHistory.getId(), error);
    }
}

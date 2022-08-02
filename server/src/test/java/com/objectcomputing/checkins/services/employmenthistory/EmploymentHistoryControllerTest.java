package com.objectcomputing.checkins.services.employmenthistory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.EmploymentHistoryFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.employmenthistory.EmploymentHistoryTestUtil.*;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;

public class EmploymentHistoryControllerTest extends TestContainersSuite
        implements EmploymentHistoryFixture, RoleFixture {

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

        LOG.info(response.body().toString());
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
}

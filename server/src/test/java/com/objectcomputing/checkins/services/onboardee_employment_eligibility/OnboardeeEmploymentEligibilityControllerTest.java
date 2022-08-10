package com.objectcomputing.checkins.services.onboardee_employment_eligibility;
import static com.objectcomputing.checkins.services.onboardee_employment_eligibility.OnboardeeEmploymentEligibilityTestUtil.*;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.OnboardeeEmploymentEligibilityFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OnboardeeEmploymentEligibilityControllerTest extends TestContainersSuite implements
        OnboardeeEmploymentEligibilityFixture, RoleFixture {
    @Inject
    @Client("/services/onboardee-employment-eligibility")
    private HttpClient client;

    @Test
    public void testGETAllOnboardeeEmploymentEligibility() {
        createADefaultOnboardeeEmploymentEligibility();
        createADefaultOnboardeeEmploymentEligibility2();
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<OnboardeeEmploymentEligibilityResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(OnboardeeEmploymentEligibilityResponseDTO.class));
        final List<OnboardeeEmploymentEligibilityResponseDTO> results = response.body();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, results.size());
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
    public void testPOSTCreateANullOnboardeeProfile() {

        OnboardeeEmploymentEligibilityCreateDTO onboardeeProfileCreateDTO = new OnboardeeEmploymentEligibilityCreateDTO();

        final HttpRequest<OnboardeeEmploymentEligibilityCreateDTO> request = HttpRequest.
                POST("/", onboardeeProfileCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }
    @Test
    public void testPOSTCreateAOnboardeeEmploymentEligibility() {

        OnboardeeEmploymentEligibilityResponseDTO dto = mkUpdateOnboardeeEmploymentEligibilityResponseDTO();

        final HttpRequest<?> request = HttpRequest.
                POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<OnboardeeEmploymentEligibility> response = client.toBlocking().exchange(request, OnboardeeEmploymentEligibility.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(dto.getAgeLegal(), response.body().getAgeLegal());
        assertEquals(dto.getUsCitizen(), response.body().getUsCitizen());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
    }
}

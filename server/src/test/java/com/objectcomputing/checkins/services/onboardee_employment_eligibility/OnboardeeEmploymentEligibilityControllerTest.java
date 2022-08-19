package com.objectcomputing.checkins.services.onboardee_employment_eligibility;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.BackgroundInformationFixture;
import com.objectcomputing.checkins.services.fixture.OnboardeeEmploymentEligibilityFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility.OnboardeeEmploymentEligibilityCreateDTO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.HR_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OnboardeeEmploymentEligibilityControllerTest extends TestContainersSuite implements
        OnboardeeEmploymentEligibilityFixture, RoleFixture, BackgroundInformationFixture {
    @Inject
    @Client("/services/onboardee-employment-eligibility")
    private HttpClient client;

    @Test
    public void testPOSTCreateANullOnboardeeProfile() {

        OnboardeeEmploymentEligibilityCreateDTO onboardeeProfileCreateDTO = new OnboardeeEmploymentEligibilityCreateDTO();

        final HttpRequest<OnboardeeEmploymentEligibilityCreateDTO> request = HttpRequest.
                POST("/", onboardeeProfileCreateDTO).basicAuth(HR_ROLE, HR_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }
}

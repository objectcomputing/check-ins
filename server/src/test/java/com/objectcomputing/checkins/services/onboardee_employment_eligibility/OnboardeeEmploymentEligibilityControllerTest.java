package com.objectcomputing.checkins.services.onboardee_employment_eligibility;


import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.OnboardeeEmploymentEligibilityFixture;
import com.objectcomputing.checkins.services.fixture.OnboardingFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class OnboardeeEmploymentEligibilityControllerTest extends TestContainersSuite implements
        MemberProfileFixture, OnboardeeEmploymentEligibilityFixture, RoleFixture {
    @Inject
    @Client("/services/onboardee-employment-eligibility")
    private HttpClient client;

    @Test
    public void testGETAllOnboardeeEmploymentEligibility(){
        createADefaultOnboardeeEmploymentEligibility();
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<OnboardeeEmploymentEligibilityResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(OnboardeeEmploymentEligibilityResponseDTO.class));
        final List<OnboardeeEmploymentEligibilityResponseDTO> results = response.body();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, results.size());
    }
}

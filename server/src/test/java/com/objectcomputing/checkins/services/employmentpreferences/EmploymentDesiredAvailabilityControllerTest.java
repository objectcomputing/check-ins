package com.objectcomputing.checkins.services.employmentpreferences;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.EmploymentPreferencesFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmploymentDesiredAvailabilityControllerTest extends TestContainersSuite implements MemberProfileFixture, EmploymentPreferencesFixture, RoleFixture {

    private static final Logger LOG = LoggerFactory.getLogger(EmploymentDesiredAvailabilityControllerTest.class);

    @Inject
    @Client("/services/employment-desired-availability")
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
    public void testGETALLEmploymentPrefrences() {
        createADefaultEmploymentPreferences();
        createSecondDefaultEmploymentPreferences();

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<EmploymentDesiredAvailabilityDTO>> response = client.toBlocking().exchange(request, Argument.listOf(EmploymentDesiredAvailabilityDTO.class));
        final List<EmploymentDesiredAvailabilityDTO> results = response.body();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, results.size());
    }

}

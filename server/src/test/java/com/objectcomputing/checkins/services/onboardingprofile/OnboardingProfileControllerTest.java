package com.objectcomputing.checkins.services.onboardingprofile;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.BackgroundInformationFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.OnboardingFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.HR_ROLE;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OnboardingProfileControllerTest extends TestContainersSuite implements
        MemberProfileFixture, OnboardingFixture, RoleFixture, BackgroundInformationFixture {
    private static final Logger LOG = LoggerFactory.getLogger(OnboardingProfileControllerTest.class);

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
   public void testGETGetByIdNotFound() {

       final HttpRequest<Object> request = HttpRequest.
               GET(String.format("/%s", UUID.randomUUID())).basicAuth(HR_ROLE, HR_ROLE);

       HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
               () -> client.toBlocking().exchange(request, Map.class));

       assertNotNull(responseException.getResponse());
       assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
   }

}

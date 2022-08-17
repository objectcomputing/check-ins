package com.objectcomputing.checkins.services.referraltype;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.ReferralTypeFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferralTypeControllerTest extends TestContainersSuite implements MemberProfileFixture, ReferralTypeFixture, RoleFixture {

    private static final Logger LOG = LoggerFactory.getLogger(ReferralTypeControllerTest.class);

    @Inject
    @Client("/services/referral-type")
    private HttpClient client;

    //TODO: Use Util.MAX instead of defining variable
    /*
     * LocalDate.Max cannot be used for end-to-end tests
     * LocalDate.Max year = 999999999
     * POSTGRES supported date range = 4713 BC - 5874897 AD
     */

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void testGETAllReferralTypes() {
        createADefaultReferralType();
        createSecondDefaultReferralType();

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<ReferralTypeDTO>> response = client.toBlocking().exchange(request, Argument.listOf(ReferralTypeDTO.class));
        final List<ReferralTypeDTO> results = response.body();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, results.size());
    }

}

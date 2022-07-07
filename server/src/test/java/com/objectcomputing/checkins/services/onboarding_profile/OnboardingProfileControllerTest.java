package com.objectcomputing.checkins.services.onboarding_profile;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.OnboardingFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class OnboardingProfileControllerTest extends TestContainersSuite implements
        MemberProfileFixture, OnboardingFixture, RoleFixture {



        @Inject
        @Client("/services/member-profiles")
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
}

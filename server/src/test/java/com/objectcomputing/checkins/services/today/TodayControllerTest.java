package com.objectcomputing.checkins.services.today;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodayControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/today")
    private HttpClient client;

    @Test
    public void testGET() {
        MemberProfile memberProfile = createAnUnrelatedUser();

        MemberProfile birthdayProfile = createADefaultMemberProfileWithBirthDayToday();
        MemberProfile anniversaryProfile = createADefaultMemberProfileWithAnniversaryToday();


        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<TodayResponseDTO> response = client.toBlocking().exchange(request, TodayResponseDTO.class);

        assertEquals(1, response.body().getAnniversaries().size());
        assertEquals(1, response.body().getBirthdays().size());
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}

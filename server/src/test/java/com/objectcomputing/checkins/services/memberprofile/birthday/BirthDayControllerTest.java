package com.objectcomputing.checkins.services.memberprofile.birthday;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.anniversaryreport.AnniversaryReportResponseDTO;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BirthDayControllerTest extends TestContainersSuite implements MemberProfileFixture {

    @Inject
    @Client("/services/reports/birthdays")
    private HttpClient client;

    @Test
    public void testGETFindByValueNameOfTheMonth() {

        MemberProfile memberProfile = createADefaultMemberProfileWithBirthDay();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?month=%s", memberProfile.getStartDate().getMonth().toString())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<BirthDayResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(BirthDayResponseDTO.class));

        assertEquals(1, response.body().size());
        assertEquals(memberProfile.getId(), response.body().get(0).getUserId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindByValueNameOfTheMonthNotAuthorized() {

        MemberProfile memberProfile = createADefaultMemberProfileWithBirthDay();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?month=%s", memberProfile.getStartDate().getMonth().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());

    }



}

package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnniversaryReportControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/reports/anniversaries")
    private HttpClient client;

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void testGETFindByMonthReturnsEmptyBody() throws UnsupportedEncodingException {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?month=%s", encodeValue(memberProfileOfAdmin.getStartDate().getMonth().toString()))).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<List<AnniversaryReportResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(AnniversaryReportResponseDTO.class));

        assertEquals(memberProfileOfAdmin.getId(), Objects.requireNonNull(response.body()).get(0).getUserId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindByMonthNotAuthorized() throws UnsupportedEncodingException {
        MemberProfile memberProfile = createAnUnrelatedUser();
        createAndAssignRole(RoleType.MEMBER, memberProfile);

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?month=%s", encodeValue("dnc"))).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> {
                        var response = client.toBlocking().exchange(request);
                        response.getStatus();
                    });

        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
    }

    @Test
    public void testGETFindByNoValue() {

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest<Object> request = HttpRequest.GET("").basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<List<AnniversaryReportResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(AnniversaryReportResponseDTO.class));

        assertEquals(0, Objects.requireNonNull(response.body()).size());
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}

package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnniversaryReportControllerTest extends TestContainersSuite implements MemberProfileFixture {

    @Inject
    @Client("/services/reports/anniversaries")
    private HttpClient client;

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void testGETFindByMonthReturnsEmptyBody() throws UnsupportedEncodingException {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?month=%s", encodeValue("dnc"))).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<AnniversaryReportResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(AnniversaryReportResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindByMonthNotAuthorized() throws UnsupportedEncodingException {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?month=%s", encodeValue("dnc"))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
    }

    @Test
    public void testGETFindByValueName() throws UnsupportedEncodingException {

        MemberProfile memberProfile = createADefaultMemberProfile();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?month=%s", encodeValue(memberProfile.getStartDate().getMonth().toString()))).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<AnniversaryReportResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(AnniversaryReportResponseDTO.class));

        assertEquals(1, response.body().size());
        assertEquals(memberProfile.getId(), response.body().get(0).getUserId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGETFindByNoValue() throws UnsupportedEncodingException {

        MemberProfile memberProfile = createADefaultMemberProfile();
        final HttpRequest<Object> request = HttpRequest.GET("").basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        final HttpResponse<List<AnniversaryReportResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(AnniversaryReportResponseDTO.class));

        assertEquals(memberProfile.getId(), response.body().get(0).getUserId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}

package com.objectcomputing.checkins.services.memberprofile.csvreport;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.PDL_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberProfileReportControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {
    @Inject
    @Client("/services/reports/member")
    private HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void testGETReportSucceeds() {

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        MemberProfile memberProfile = createADefaultMemberProfile();

        final HttpRequest<Object> request = HttpRequest.
            GET("/csv").basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        HttpResponse<File> response = client.toBlocking().exchange(request, File.class);

        assertNotNull(response);
        assertEquals(200, response.getStatus().getCode());

        File responseBody = response.getBody().orElse(null);
        assertNotNull(responseBody);
    }

    @Test
    public void testGETReportNoPermissions() {

        MemberProfile memberProfileOfPdl = createAnUnrelatedUser();
        assignPdlRole(memberProfileOfPdl);

        MemberProfile memberProfile = createADefaultMemberProfile();

        final HttpRequest<Object> request = HttpRequest.
            GET("/csv").basicAuth(memberProfileOfPdl.getWorkEmail(), PDL_ROLE);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, File.class);
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
    }

}

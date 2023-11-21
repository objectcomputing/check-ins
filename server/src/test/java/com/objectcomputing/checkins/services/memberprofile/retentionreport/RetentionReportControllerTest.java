package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RetentionReportControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {
    @Inject
    @Client("/reports/retention")
    private HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Test
    public void testPOSTEmptyRequestReturns400() {
        MemberProfile adminMember = createAnUnrelatedUser();
        assignAdminRole(adminMember);

        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("", dto)
                    .basicAuth(adminMember.getWorkEmail(), ADMIN_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPOSTNullStartDateReturns400() {
        MemberProfile adminMember = createAnUnrelatedUser();
        assignAdminRole(adminMember);

        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();
        dto.setStartDate(null);
        dto.setEndDate(LocalDate.now());
        dto.setFrequency("WEEKLY");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("", dto)
                    .basicAuth(adminMember.getWorkEmail(), ADMIN_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPOSTNullEndDateReturns400() {
        MemberProfile adminMember = createAnUnrelatedUser();
        assignAdminRole(adminMember);

        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(null);
        dto.setFrequency("WEEKLY");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("", dto)
                    .basicAuth(adminMember.getWorkEmail(), ADMIN_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPOSTEndDateBeforeStartDateReturns400() {
        MemberProfile adminMember = createAnUnrelatedUser();
        assignAdminRole(adminMember);

        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().minusMonths(1));
        dto.setFrequency("WEEKLY");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("", dto)
                    .basicAuth(adminMember.getWorkEmail(), ADMIN_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void testValidRequestEmptyResponse() {
        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();
        dto.setStartDate(LocalDate.now().minusMonths(2));
        dto.setEndDate(LocalDate.now());
        dto.setFrequency("WEEKLY");

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        final HttpRequest<RetentionReportRequestDTO> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<RetentionReportResponseDTO> response = client.toBlocking()
                .exchange(request, RetentionReportResponseDTO.class);

        final RetentionReportResponseDTO responseDTO = response.body();
        Assertions.assertNotNull(responseDTO);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s", request.getPath()), response.getHeaders().get("Location"));
    }

    @Test
    void testValidEmptyFrequencyResponse() {
        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();
        dto.setStartDate(LocalDate.now().minusMonths(2));
        dto.setEndDate(LocalDate.now());
        dto.setFrequency("");

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        final HttpRequest<RetentionReportRequestDTO> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<RetentionReportResponseDTO> response = client.toBlocking()
                .exchange(request, RetentionReportResponseDTO.class);

        final RetentionReportResponseDTO responseDTO = response.body();
        Assertions.assertNotNull(responseDTO);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s", request.getPath()), response.getHeaders().get("Location"));
    }

    @Test
    void testValidCaseIgnoreFrequencyResponse() {
        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();
        dto.setStartDate(LocalDate.now().minusMonths(2));
        dto.setEndDate(LocalDate.now());
        dto.setFrequency("MoNtHlY");

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        final HttpRequest<RetentionReportRequestDTO> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<RetentionReportResponseDTO> response = client.toBlocking()
                .exchange(request, RetentionReportResponseDTO.class);

        final RetentionReportResponseDTO responseDTO = response.body();
        Assertions.assertNotNull(responseDTO);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s", request.getPath()), response.getHeaders().get("Location"));
    }

    @Test
    void testCorrectRetentionResponse() {
        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();
        dto.setStartDate(LocalDate.now().minusMonths(2));
        dto.setEndDate(LocalDate.now());
        dto.setFrequency("MONTHLY");

        final MemberProfile memberProfile1 = createAPastMemberProfile();
        final MemberProfile memberProfile2 = createAPastTerminatedMemberProfile();
        final MemberProfile memberProfile3 = createANewHireProfile();
        final MemberProfile memberProfile4 = createATerminatedNewHireProfile();

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        assignAdminRole(memberProfileOfAdmin);

        final HttpRequest<RetentionReportRequestDTO> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<RetentionReportResponseDTO> response = client.toBlocking()
                .exchange(request, RetentionReportResponseDTO.class);

        final RetentionReportResponseDTO responseDTO = response.body();
        Assertions.assertNotNull(responseDTO);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s", request.getPath()), response.getHeaders().get("Location"));
    }

    @Test
    void testCorrectRetentionNoPermission() {
        RetentionReportRequestDTO dto = new RetentionReportRequestDTO();
        dto.setStartDate(LocalDate.now().minusMonths(2));
        dto.setEndDate(LocalDate.now());
        dto.setFrequency("MONTHLY");

        final MemberProfile memberProfile1 = createAPastMemberProfile();
        final MemberProfile memberProfile2 = createAPastTerminatedMemberProfile();
        final MemberProfile memberProfile3 = createANewHireProfile();
        final MemberProfile memberProfile4 = createATerminatedNewHireProfile();

        MemberProfile memberProfile = createAnUnrelatedUser();
        assignMemberRole(memberProfile);

        final HttpRequest<RetentionReportRequestDTO> request = HttpRequest.POST("", dto)
            .basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, RetentionReportResponseDTO.class);
        });
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

    }


}

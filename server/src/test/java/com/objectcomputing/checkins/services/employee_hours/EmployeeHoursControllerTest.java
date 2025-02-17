package com.objectcomputing.checkins.services.employee_hours;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.EmployeeHoursFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Collections;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeHoursControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, EmployeeHoursFixture {

    @Inject
    @Client("/services/employee/hours")
    HttpClient client;

    private MemberProfile user;

    @BeforeEach
    void setUp() {
        user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);
    }

    @Test
    void testCreateEmployeeHours() {
        File file = new File("src/test/java/com/objectcomputing/checkins/services/employee_hours/test.csv");
        MultipartBody multipartBody = MultipartBody
                .builder()
                .addPart("file","test.csv",new MediaType("text/csv"),file)
                .build();
        MemberProfile memberProfile=createADefaultMemberProfile();
        createADefaultMemberProfileForPdl(memberProfile);

        final HttpRequest<MultipartBody> request = HttpRequest.POST("/upload", multipartBody).basicAuth(user.getWorkEmail(),ADMIN_ROLE).contentType(MediaType.MULTIPART_FORM_DATA);
        final HttpResponse<EmployeeHoursResponseDTO> response = client.toBlocking().exchange(request, EmployeeHoursResponseDTO.class);

        EmployeeHoursResponseDTO employeeHoursResponseDTO = response.body();

        assertEquals(employeeHoursResponseDTO,response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testCreateEmployeeHoursWithNoTeamMemberId() {
        File file = new File("src/test/java/com/objectcomputing/checkins/services/employee_hours/test.csv");
        MultipartBody multipartBody = MultipartBody
                .builder()
                .addPart("file","test.csv",new MediaType("text/csv"),file)
                .build();

        final HttpRequest<MultipartBody> request = HttpRequest.POST("/upload", multipartBody).basicAuth(user.getWorkEmail(),ADMIN_ROLE).contentType(MediaType.MULTIPART_FORM_DATA);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());
    }

    @Test
    void testCreateEmployeeHoursWithNoAdminRole() {
        File file = new File("src/test/java/com/objectcomputing/checkins/services/employee_hours/test.csv");
        MultipartBody multipartBody = MultipartBody
                .builder()
                .addPart("file","test.csv",new MediaType("text/csv"),file)
                .build();

        final HttpRequest<MultipartBody> request = HttpRequest.POST("/upload", multipartBody).basicAuth(MEMBER_ROLE,MEMBER_ROLE).contentType(MediaType.MULTIPART_FORM_DATA);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN,responseException.getStatus());
    }

    @Test
    void testFindAllRecordsWithAdminRole() throws IOException {
        MemberProfile memberProfile=createADefaultMemberProfile();
        createADefaultMemberProfileForPdl(memberProfile);

        createEmployeeHours();
        final HttpRequest<Object> request = HttpRequest.GET("/").basicAuth(user.getWorkEmail(),ADMIN_ROLE);
        final HttpResponse<Set<EmployeeHours>> response = client.toBlocking().exchange(request, Argument.setOf(EmployeeHours.class));
        response.body();
        response.getStatus();

        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testFindRecordsWithEmployeeId() throws IOException {
        MemberProfile memberProfile=createADefaultMemberProfile();
        createADefaultMemberProfileForPdl(memberProfile);
        List<EmployeeHours> employeeHoursList = createEmployeeHours();
        final HttpRequest<Object> request = HttpRequest.GET(String.format("/?employeeId=%s",employeeHoursList.get(0).getEmployeeId())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<EmployeeHours>> response = client.toBlocking().exchange(request, Argument.setOf(EmployeeHours.class));
        assertEquals(Set.of(employeeHoursList.get(0)),response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testFindAllRecordsWithNonAdminRole() throws IOException {
        MemberProfile memberProfile=createADefaultMemberProfile();
        createADefaultMemberProfileForPdl(memberProfile);

        createEmployeeHours();
        final HttpRequest<Object> request = HttpRequest.GET("/").basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(NOT_AUTHORIZED_MSG, error);

    }

    @Test
    void testFindEmployeeHoursNotFound() {

        final HttpRequest<Object> request = HttpRequest.GET("/?employeeId=invalid_id").basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<EmployeeHours>> response = client.toBlocking().exchange(request, Argument.setOf(EmployeeHours.class));
        Set<EmployeeHours> emptySet = Collections.emptySet();
        assertEquals(emptySet,response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }
}

package com.objectcomputing.checkins.services.checkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckInControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, RoleFixture {

    @Inject
    @Client("/services/check-ins")
    private HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }
    @Test
    public void testCreateACheckInByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignAdminRole(memberProfileOfUser);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        CheckIn checkInResponse = response.body();

        assertNotNull(checkInResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkInCreateDTO.getTeamMemberId(), checkInResponse.getTeamMemberId());
        assertEquals(checkInCreateDTO.getPdlId(), checkInResponse.getPdlId());
        assertEquals(String.format("%s/%s", request.getPath(), checkInResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testCreateACheckInByMember() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        CheckIn checkInResponse = response.body();

        assertNotNull(checkInResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkInCreateDTO.getTeamMemberId(), checkInResponse.getTeamMemberId());
        assertEquals(checkInCreateDTO.getPdlId(), checkInResponse.getPdlId());
        assertEquals(String.format("%s/%s", request.getPath(), checkInResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testCreateACheckInByPDL() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        CheckIn checkInResponse = response.body();

        assertNotNull(checkInResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkInCreateDTO.getTeamMemberId(), checkInResponse.getTeamMemberId());
        assertEquals(checkInCreateDTO.getPdlId(), checkInResponse.getPdlId());
        assertEquals(String.format("%s/%s", request.getPath(), checkInResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testCreateACheckInByUnrelatedUser() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();
        Role role = assignPdlRole(memberProfileOfMrNobody);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO).basicAuth(memberProfileOfMrNobody.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testCreateACheckInForSamePDLAndMember() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckIn existingCheckIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(existingCheckIn.getTeamMemberId());
        checkInCreateDTO.setPdlId(existingCheckIn.getTeamMemberId());
        checkInCreateDTO.setCheckInDate(existingCheckIn.getCheckInDate());
        checkInCreateDTO.setCompleted(existingCheckIn.isCompleted());

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO)
                .basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Team member id %s can't be same as PDL id", checkInCreateDTO.getTeamMemberId()), error);
    }

    @Test
    void testCreateAnInvalidCheckIn() {
        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();

        MemberProfile memberProfile = createADefaultMemberProfile();

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());

        assertEquals("checkIn.pdlId: must not be null", errorList.get(0));
        assertEquals("checkIn.teamMemberId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateCheckInForNonExistingMember() {

        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();
        Role role = assignMemberRole(memberProfileOfMrNobody);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(UUID.randomUUID());
        checkInCreateDTO.setPdlId(UUID.randomUUID());
        checkInCreateDTO.setCompleted(true);
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());

        HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO).basicAuth(memberProfileOfMrNobody.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist", checkInCreateDTO.getTeamMemberId()), error);
    }

    @Test
    void testCreateANullCheckIn() {
        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [checkIn] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testCreateACheckInForInvalidPdlID() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(UUID.randomUUID());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("PDL %s is not associated with member %s", checkInCreateDTO.getPdlId(), checkInCreateDTO.getTeamMemberId()), error);
    }

    @Test
    void testCreateACheckInForInvalidDate() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.of(1965, 11, 12, 15, 56));
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("", checkInCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid date for checkin %s", checkInCreateDTO.getTeamMemberId()), error);
    }

    @Test
    void testCreateUnAuthorized() {
        CheckIn checkIn = new CheckIn(null, UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), true);

        final HttpRequest<CheckIn> request = HttpRequest.POST("", checkIn);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

    @Test
    public void testGetReadByIdByAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignAdminRole(memberProfileOfUser);

        CheckIn expected = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetReadByIdByPDL() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfUser);

        CheckIn expected = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetReadByIdByMember() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn expected = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetReadByIdByUnrelatedUser() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();
        Role role = assignMemberRole(memberProfileOfMrNobody);

        CheckIn expected = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileOfMrNobody.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testGetReadCheckInDoesNotExist() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        Role role = assignPdlRole(memberProfileOfPDL);
        UUID randomCheckinID = UUID.randomUUID();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid checkin id %s", randomCheckinID), error);
    }

    @Test
    void testUpdateCheckInByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignAdminRole(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateCheckInByPDL() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateCheckInByMember() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateCheckInByUnrelatedUser() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();
        Role role = assignPdlRole(memberProfileOfMrNobody);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfMrNobody.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testUpdateNonExistingCheckIn() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Checkin %s doesn't exist", checkIn.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotExistingMemberCheckIn() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setTeamMemberId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", checkIn.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateNotMemberCheckInWithoutId() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setId(null);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to find checkin record with id null", checkIn.getId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testUpdateUnAuthorized() {
        CheckIn checkIn = new CheckIn(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), true);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

    @Test
    void testUpdateInvalidCheckIn() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile unrelatedUser = createAnUnrelatedUser();
        Role role = assignMemberRole(unrelatedUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);
        checkIn.setTeamMemberId(null);
        checkIn.setPdlId(null);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(unrelatedUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("checkIn.pdlId: must not be null", errorList.get(0));
        assertEquals("checkIn.teamMemberId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullCheckIn() {
        MemberProfile unrelatedUser = createAnUnrelatedUser();
        Role role = assignMemberRole(unrelatedUser);
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(unrelatedUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [checkIn] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    public void testUpdateACheckInForInvalidPdlID() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setPdlId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("PDL %s is not associated with member %s", checkIn.getPdlId(), checkIn.getTeamMemberId()), error);
    }

    @Test
    void testUpdateInvalidDateCheckIn() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setCheckInDate(LocalDateTime.of(1965, 11, 12, 15, 56));

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Invalid date for checkin %s", checkIn.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCannotUpdateCheckInIfCompletedIsTrue() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("Checkin with id %s is complete and cannot be updated", checkIn.getId()), error);
    }

    @Test
    void testCannotUpdateCheckIfCompletedIsTrueUnlessMadeByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignAdminRole(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testGetFindByTeamMemberIdByMember() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByPdl() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        Role role = assignAdminRole(memberProfileOfAdmin);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByUnrelatedUser() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignMemberRole(memberProfileOfUnrelatedUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    public void testGetFindByPDLIdByMember() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    public void testGetFindByPDLIdByPDL() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetFindByPDLIdByAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        Role role = assignAdminRole(memberProfileOfAdmin);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetFindByPDLIdByUnrelatedUser() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignPdlRole(memberProfileOfUnrelatedUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    public void testGetFindByCompletedByAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignAdminRole(memberProfileOfUnrelatedUser);

        CheckIn checkIn1 = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckIn checkIn2 = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckIn checkIn3 = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", checkIn2.isCompleted())).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        Set<CheckIn> expected = new HashSet<>();
        expected.add(checkIn2);
        expected.add(checkIn3);

        Set<CheckIn> allCheckins = new HashSet<>();
        allCheckins.add(checkIn1);
        allCheckins.add(checkIn2);
        allCheckins.add(checkIn3);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expected.size(), response.body().size());
        assertEquals(expected, response.body());
        assertNotEquals(allCheckins, response.body());
    }

    @Test
    public void testGetFindByCompletedByMember() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignMemberRole(memberProfileOfUser);

        CheckIn checkIn1 = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckIn checkIn2 = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckIn checkIn3 = createACompletedCheckIn(memberProfileOfUnrelatedUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", checkIn2.isCompleted())).basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, response.body().size());
        assertEquals(checkIn2, response.body().iterator().next());
        assertNotEquals(checkIn1, response.body().iterator().next());
        assertNotEquals(checkIn3, response.body().iterator().next());
    }

    @Test
    public void testGetFindByCompletedByPDL() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignPdlRole(memberProfileOfPDL);

        CheckIn checkIn1 = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckIn checkIn2 = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckIn checkIn3 = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUnrelatedUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", checkIn2.isCompleted())).basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        Set<CheckIn> expected = new HashSet<>();
        expected.add(checkIn2);
        expected.add(checkIn3);

        Set<CheckIn> allCheckins = new HashSet<>();
        allCheckins.add(checkIn1);
        allCheckins.add(checkIn2);
        allCheckins.add(checkIn3);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expected.size(), response.body().size());
        assertEquals(expected, response.body());
        assertNotEquals(allCheckins, response.body());
    }

    @Test
    void testFindAllCheckInsByAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignAdminRole(memberProfileOfUnrelatedUser);

        CheckIn checkIn1 = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckIn checkIn2 = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckIn checkIn3 = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        Set<CheckIn> expected = new HashSet<>();
        expected.add(checkIn1);
        expected.add(checkIn2);
        expected.add(checkIn3);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expected.size(), response.body().size());
        assertEquals(expected, response.body());
    }

    @Test
    void testFindAllCheckInsByMember() {

        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignMemberRole(memberProfileOfUnrelatedUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testFindAllCheckInsByPDL() {

        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignPdlRole(memberProfileOfUnrelatedUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testCheckInDoesNotExistForTeamMemberID() {

        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignMemberRole(memberProfileOfUnrelatedUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", memberProfileOfUnrelatedUser.getId())).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());
        HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(Set.of(), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testCheckInDoesNotExistForPdlId() {

        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignPdlRole(memberProfileOfUnrelatedUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", memberProfileOfUnrelatedUser.getId())).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());
        HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(Set.of(), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testCheckInDoesNotExistForCompleted() {

        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();
        Role role = assignMemberRole(memberProfileOfUnrelatedUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", true)).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(), response.body());
    }
}
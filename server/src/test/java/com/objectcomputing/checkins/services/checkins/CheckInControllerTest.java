package com.objectcomputing.checkins.services.checkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CheckInControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture {

    @Inject
    @Client("/services/check-in")
    private HttpClient client;

    @Test
    public void testCreateACheckInByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request,CheckIn.class);

        CheckIn checkInResponse = response.body();

        assertNotNull(checkInResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkInCreateDTO.getTeamMemberId(),checkInResponse.getTeamMemberId());
        assertEquals(checkInCreateDTO.getPdlId(),checkInResponse.getPdlId());
        assertEquals(String.format("%s/%s", request.getPath(), checkInResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testCreateACheckInByMember() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request,CheckIn.class);

        CheckIn checkInResponse = response.body();

        assertNotNull(checkInResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkInCreateDTO.getTeamMemberId(),checkInResponse.getTeamMemberId());
        assertEquals(checkInCreateDTO.getPdlId(),checkInResponse.getPdlId());
        assertEquals(String.format("%s/%s", request.getPath(), checkInResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testCreateACheckInByPDL() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request,CheckIn.class);

        CheckIn checkInResponse = response.body();

        assertNotNull(checkInResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkInCreateDTO.getTeamMemberId(),checkInResponse.getTeamMemberId());
        assertEquals(checkInCreateDTO.getPdlId(),checkInResponse.getPdlId());
        assertEquals(String.format("%s/%s", request.getPath(), checkInResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testCreateACheckInByUnrelatedUser() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Member %s is unauthorized to do this operation", memberProfileOfMrNobody.getId()), error);
    }

    @Test
    void testCreateACheckInForSamePDLAndMember() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn existingCheckIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(existingCheckIn.getTeamMemberId());
        checkInCreateDTO.setPdlId(existingCheckIn.getTeamMemberId());
        checkInCreateDTO.setCheckInDate(existingCheckIn.getCheckInDate());
        checkInCreateDTO.setCompleted(existingCheckIn.isCompleted());

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Team member id %s can't be same as PDL id",checkInCreateDTO.getTeamMemberId()),error);
    }

    @Test
    void testCreateAnInvalidCheckIn() {
        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth("test@test.com", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());

        assertEquals("checkIn.pdlId: must not be null",errorList.get(0));
        assertEquals("checkIn.teamMemberId: must not be null",errorList.get(1));
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateCheckInForNonExistingMember() {

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(UUID.randomUUID());
        checkInCreateDTO.setPdlId(UUID.randomUUID());
        checkInCreateDTO.setCompleted(true);
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());

        HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth("test@test.com", MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Member %s doesn't exist", checkInCreateDTO.getTeamMemberId()),error);
    }

    @Test
    void testCreateANullCheckIn() {
        final HttpRequest<String> request = HttpRequest.POST("","").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [checkIn] not specified",errors.asText());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());
    }

    @Test
    public void testCreateACheckInForInvalidPdlID() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(UUID.randomUUID());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("PDL %s is not associated with member %s", checkInCreateDTO.getPdlId(), checkInCreateDTO.getTeamMemberId()), error);
    }

    @Test
    void testCreateACheckInForInvalidDate() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.of(1965, 11, 12, 15, 56));
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Invalid date for checkin %s",checkInCreateDTO.getTeamMemberId()),error);
    }

    @Test
    void testCreateUnAuthorized() {
        CheckIn checkIn = new CheckIn(null, UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(),true);

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

        CheckIn expected  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetReadByIdByPDL() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn expected  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetReadByIdByMember() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn expected  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetReadByIdByUnrelatedUser() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn expected  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileOfMrNobody.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Member %s is unauthorized to do this operation", memberProfileOfMrNobody.getId()), error);
    }

    @Test
    void testGetReadCheckInDoesNotExist() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        UUID randomCheckinID = UUID.randomUUID();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Invalid checkin id %s", randomCheckinID), error);
    }

    @Test
    void testUpdateCheckInByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateCheckInByPDL() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateCheckInByMember() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
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

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Member %s is unauthorized to do this operation", memberProfileOfMrNobody.getId()), error);
    }

    @Test
    void testUpdateNonExistingCheckIn() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to find checkin record with id %s", checkIn.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotExistingMemberCheckIn() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setTeamMemberId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
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

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setId(null);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
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
        CheckIn checkIn = new CheckIn(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),LocalDateTime.now(),true);

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

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);
        checkIn.setTeamMemberId(null);
        checkIn.setPdlId(null);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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
        final HttpRequest<String> request = HttpRequest.PUT("","").basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [checkIn] not specified",errors.asText());
        assertEquals(request.getPath(),href.asText());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    public void testUpdateACheckInForInvalidPdlID() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setPdlId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("PDL %s is not associated with member %s", checkIn.getPdlId(), checkIn.getTeamMemberId()), error);
    }

    @Test
    void testUpdateInvalidDateCheckIn() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        checkIn.setCheckInDate(LocalDateTime.of(1965, 11, 12, 15, 56));

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Invalid date for checkin %s", checkIn.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCannotUpdateCheckIfCompletedIsTrue() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Checkin with id %s is complete and cannot be updated", checkIn.getId()), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void testCannotUpdateCheckIfCompletedIsTrueUnlessMadeByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testGetFindByTeamMemberIdByMember() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByPdl() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByUnrelatedUser() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("Member %s is unauthorized to do this operation", memberProfileOfUnrelatedUser.getId()), error);
    }

    @Test
    public void testGetFindByPDLIdByMember() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("Member %s is unauthorized to do this operation", memberProfileOfUser.getId()), error);
    }

    @Test
    public void testGetFindByPDLIdByPDL() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByPDLIdByAdmin() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByPDLIdByUnrelatedUser() {

        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileOfUnrelatedUser.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("Member %s is unauthorized to do this operation", memberProfileOfUser.getId()), error);
    }

    @Test
    public void testGetFindByCompleted() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", checkIn.isCompleted())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testFindCheckInAllParams() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s&pdlId=%s&completed=%s", checkIn.getTeamMemberId(),
                checkIn.getTeamMemberId(),checkIn.isCompleted())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testCheckInDoesNotExist() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s",UUID.randomUUID())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(Set.of(), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }
}
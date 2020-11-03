package com.objectcomputing.checkins.services.checkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileEntityOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileEntityOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileEntityOfUser.getWorkEmail(), ADMIN_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileEntityOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileEntityOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileEntityOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileEntityOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileEntityOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileEntityOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testCreateACheckInForSamePDLAndMember() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn existingCheckIn = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(existingCheckIn.getTeamMemberId());
        checkInCreateDTO.setPdlId(existingCheckIn.getTeamMemberId());
        checkInCreateDTO.setCheckInDate(existingCheckIn.getCheckInDate());
        checkInCreateDTO.setCompleted(existingCheckIn.isCompleted());

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), ADMIN_ROLE);
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

        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(UUID.randomUUID());
        checkInCreateDTO.setPdlId(UUID.randomUUID());
        checkInCreateDTO.setCompleted(true);
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());

        HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), MEMBER_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileEntityOfUser.getId());
        checkInCreateDTO.setPdlId(UUID.randomUUID());
        checkInCreateDTO.setCheckInDate(LocalDateTime.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfileEntityOfUser.getId());
        checkInCreateDTO.setPdlId(memberProfileEntityOfPDL.getId());
        checkInCreateDTO.setCheckInDate(LocalDateTime.of(1965, 11, 12, 15, 56));
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
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

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn expected  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileEntityOfUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetReadByIdByPDL() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn expected  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetReadByIdByMember() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn expected  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);
        CheckIn actual = response.body();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTeamMemberId(), actual.getTeamMemberId());
        assertEquals(expected.getPdlId(), actual.getPdlId());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetReadByIdByUnrelatedUser() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();

        CheckIn expected  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", expected.getId())).basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testGetReadCheckInDoesNotExist() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        UUID randomCheckinID = UUID.randomUUID();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateCheckInByPDL() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateCheckInByMember() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateCheckInByUnrelatedUser() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testUpdateNonExistingCheckIn() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        checkIn.setId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        checkIn.setTeamMemberId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        checkIn.setId(null);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
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
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        checkIn.setPdlId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        checkIn.setCheckInDate(LocalDateTime.of(1965, 11, 12, 15, 56));

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createACompletedCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("Checkin with id %s is complete and cannot be updated", checkIn.getId()), error);
    }

    @Test
    void testCannotUpdateCheckIfCompletedIsTrueUnlessMadeByAdmin() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createACompletedCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    public void testGetFindByTeamMemberIdByMember() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByPdl() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByAdmin() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfAdmin = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileEntityOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByTeamMemberIdByUnrelatedUser() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    public void testGetFindByPDLIdByMember() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    public void testGetFindByPDLIdByPDL() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByPDLIdByAdmin() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfAdmin = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileEntityOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testGetFindByPDLIdByUnrelatedUser() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    public void testGetFindByCompletedByAdmin() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn1  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckIn checkIn2  = createACompletedCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckIn checkIn3  = createACompletedCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", checkIn2.isCompleted())).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        Set<CheckIn> expected = new HashSet<>();
        expected.add(checkIn2);
        expected.add(checkIn3);

        assertEquals(HttpStatus.OK,response.getStatus());
        assertEquals(expected.size(), response.body().size());
        assertEquals(expected, response.body());
    }

    @Test
    public void testGetFindByCompletedByMember() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn1  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckIn checkIn2  = createACompletedCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckIn checkIn3  = createACompletedCheckIn(memberProfileEntityOfUnrelatedUser, memberProfileEntityOfPDL);


        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", checkIn2.isCompleted())).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(HttpStatus.OK,response.getStatus());
        assertEquals(1, response.body().size());
        assertEquals(checkIn2, response.body().iterator().next());
    }

    @Test
    public void testGetFindByCompletedByPDL() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn1  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckIn checkIn2  = createACompletedCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckIn checkIn3  = createACompletedCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUnrelatedUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", checkIn2.isCompleted())).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        Set<CheckIn> expected = new HashSet<>();
        expected.add(checkIn2);
        expected.add(checkIn3);

        assertEquals(HttpStatus.OK,response.getStatus());
        assertEquals(expected.size(), response.body().size());
        assertEquals(expected, response.body());
    }

    @Test
    void testFindAllCheckInsByAdmin() {

        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);
        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn1  = createADefaultCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckIn checkIn2  = createACompletedCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);
        CheckIn checkIn3  = createACompletedCheckIn(memberProfileEntityOfUser, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        Set<CheckIn> expected = new HashSet<>();
        expected.add(checkIn1);
        expected.add(checkIn2);
        expected.add(checkIn3);

        assertEquals(HttpStatus.OK,response.getStatus());
        assertEquals(expected.size(), response.body().size());
        assertEquals(expected, response.body());
    }

    @Test
    void testFindAllCheckInsByMember() {

        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testFindAllCheckInsByPDL() {

        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals(String.format("You are not authorized to perform this operation"), error);
    }

    @Test
    void testCheckInDoesNotExistForTeamMemberID() {

        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", memberProfileEntityOfUnrelatedUser.getId())).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), MEMBER_ROLE);
        HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(Set.of(), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    void testCheckInDoesNotExistForPdlId() {

        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", memberProfileEntityOfUnrelatedUser.getId())).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), PDL_ROLE);
        HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(Set.of(), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testCheckInDoesNotExistForCompleted() {

        MemberProfileEntity memberProfileEntityOfUnrelatedUser = createAnUnrelatedUser();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?completed=%s", true)).basicAuth(memberProfileEntityOfUnrelatedUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));

        assertEquals(HttpStatus.OK,response.getStatus());
        assertEquals(Set.of(), response.body());
    }
}
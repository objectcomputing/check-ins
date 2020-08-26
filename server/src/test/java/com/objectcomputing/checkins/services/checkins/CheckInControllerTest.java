package com.objectcomputing.checkins.services.checkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CheckInControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture {

    @Inject
    @Client("/services/check-in")
    private HttpClient client;

    @Test
    public void testCreateACheckIn(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfile.getUuid());
        checkInCreateDTO.setPdlId(memberProfileForPDL.getUuid());
        checkInCreateDTO.setCheckInDate(LocalDate.now());
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request,CheckIn.class);

        CheckIn checkInResponse = response.body();

        assertNotNull(checkInResponse);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkInCreateDTO.getTeamMemberId(),checkInResponse.getTeamMemberId());
        assertEquals(checkInCreateDTO.getPdlId(),checkInResponse.getPdlId());
        assertEquals(String.format("%s/%s", request.getPath(), checkInResponse.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateACheckInForSamePDLAndMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn existingCheckIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);
        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(existingCheckIn.getTeamMemberId());
        checkInCreateDTO.setPdlId(existingCheckIn.getTeamMemberId());
        checkInCreateDTO.setCheckInDate(existingCheckIn.getCheckInDate());
        checkInCreateDTO.setCompleted(existingCheckIn.isCompleted());

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
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

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
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
    void testCreateCheckInForNonExistingMember(){
        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(UUID.randomUUID());
        checkInCreateDTO.setPdlId(UUID.randomUUID());
        checkInCreateDTO.setCompleted(true);
        checkInCreateDTO.setCheckInDate(LocalDate.now());

        HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Member %s doesn't exists",checkInCreateDTO.getTeamMemberId()),error);
    }

    @Test
    void testCreateANullCheckIn() {
        final HttpRequest<String> request = HttpRequest.POST("","").basicAuth(MEMBER_ROLE,MEMBER_ROLE);
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
    void testCreateACheckInForInvalidDate() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckInCreateDTO checkInCreateDTO = new CheckInCreateDTO();
        checkInCreateDTO.setTeamMemberId(memberProfile.getUuid());
        checkInCreateDTO.setPdlId(memberProfileForPDL.getUuid());
        checkInCreateDTO.setCheckInDate(LocalDate.of(1965,11,12));
        checkInCreateDTO.setCompleted(true);

        final HttpRequest<CheckInCreateDTO> request = HttpRequest.POST("",checkInCreateDTO).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Invalid date for checkin %s",checkInCreateDTO.getTeamMemberId()),error);

    }

    @Test
    public void testGetFindByTeamMemberId() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamMemberId=%s", checkIn.getTeamMemberId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGetFindByPdlId() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?pdlId=%s", checkIn.getPdlId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

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
    public void testGetFindById() {

        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkIn.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Set<CheckIn>> response = client.toBlocking().exchange(request, Argument.setOf(CheckIn.class));
        assertEquals(Set.of(checkIn), response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testFindCheckInAllParams(){
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

    @Test
    void testUpdateCheckIn(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<CheckIn> response = client.toBlocking().exchange(request, CheckIn.class);

        assertEquals(checkIn, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), checkIn.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateNonExistingCheckIn(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);
        checkIn.setId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to find checkin record with id %s", checkIn.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotExistingMemberCheckIn(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);
        checkIn.setTeamMemberId(UUID.randomUUID());

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", checkIn.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNotMemberCheckInWithoutId(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);
        checkIn.setId(null);

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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
        CheckIn checkIn = new CheckIn(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),LocalDate.now(),true);

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
    void testUpdateInvalidDateCheckIn(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);
        checkIn.setCheckInDate(LocalDate.of(1965,12,11));

        final HttpRequest<CheckIn> request = HttpRequest.PUT("", checkIn)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Invalid date for checkin %s", checkIn.getTeamMemberId()), error);
        assertEquals(request.getPath(), href);

    }

}
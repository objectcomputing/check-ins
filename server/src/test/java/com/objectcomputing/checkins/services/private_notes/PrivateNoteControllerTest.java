package com.objectcomputing.checkins.services.private_notes;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNoteCreateDTO;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.PrivateNoteFixture;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrivateNoteControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, PrivateNoteFixture {

    @Inject
    @Client("/services/private-note")
    HttpClient client;

    /////////////////////////////////// READ TESTS /////////////////////////////////////////////////
    @Test
    void testReadPrivateNoteNotFound() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        UUID randomCheckinID = UUID.randomUUID();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid private note id %s", randomCheckinID), error);
    }

    @Test
    void testMemberAbleToReadHisPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        PrivateNote privateNote = createADeafultPrivateNote(checkIn, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", privateNote.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(privateNote, response.body());
    }

    @Test
    void testPdlAbleToReadHisPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        PrivateNote privateNote = createADeafultPrivateNote(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", privateNote.getId())).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(privateNote, response.body());
    }

    @Test
    void testAdminAbleToReadPdlsPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfADMIN = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        PrivateNote privateNote = createADeafultPrivateNote(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", privateNote.getId())).basicAuth(memberProfileOfADMIN.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(privateNote, response.body());
    }


    @Test
    void testAdminUnableToReadMembersPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        PrivateNote privateNote = createADeafultPrivateNote(checkIn, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", privateNote.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("Private note is created by Member and Admin is not authorized to read", error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    ////////////////////////////////////// CREATE TESTS //////////////////////////////////////

    @Test
    void testCreateInvalidPrivateNote() {
        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", PrivateNoteCreateDTO).basicAuth("test@test.com", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("privateNote.checkinid: must not be null", errorList.get(0));
        assertEquals("privateNote.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateNullPrivateNote() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [privateNote] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testMemberAbleToCreateHisPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        PrivateNoteCreateDTO privateNoteCreateDTO = new PrivateNoteCreateDTO();
        privateNoteCreateDTO.setCheckinid(checkIn.getId());
        privateNoteCreateDTO.setCreatedbyid(memberProfileOfUser.getId());
        privateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", privateNoteCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(privateNoteCreateDTO.getCheckinid(), response.body().getCheckinid());
        assertEquals(privateNoteCreateDTO.getCreatedbyid(), response.body().getCreatedbyid());
        assertEquals(privateNoteCreateDTO.getDescription(), response.body().getDescription());
    }

    @Test
    void testPdlAbleToCreateHisPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        PrivateNoteCreateDTO privateNoteCreateDTO = new PrivateNoteCreateDTO();
        privateNoteCreateDTO.setCheckinid(checkIn.getId());
        privateNoteCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        privateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", privateNoteCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(privateNoteCreateDTO.getCheckinid(), response.body().getCheckinid());
        assertEquals(privateNoteCreateDTO.getCreatedbyid(), response.body().getCreatedbyid());
        assertEquals(privateNoteCreateDTO.getDescription(), response.body().getDescription());
    }

    @Test
    void testAdminUnableToCreateMembersPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        PrivateNoteCreateDTO privateNoteCreateDTO = new PrivateNoteCreateDTO();
        privateNoteCreateDTO.setCheckinid(checkIn.getId());
        privateNoteCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        privateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", privateNoteCreateDTO).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User1 is unauthorized to do this operation", error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testMemberUnableToCreatePDLsPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        PrivateNoteCreateDTO privateNoteCreateDTO = new PrivateNoteCreateDTO();
        privateNoteCreateDTO.setCheckinid(checkIn.getId());
        privateNoteCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        privateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", privateNoteCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User2 is unauthorized to do this operation", error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateAPrivateNoteByMemberIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();
        PrivateNoteCreateDTO.setCheckinid(checkIn.getId());
        PrivateNoteCreateDTO.setCreatedbyid(checkIn.getTeamMemberId());
        PrivateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", PrivateNoteCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User1 is unauthorized to do this operation", error);

    }

    @Test
    void testCreateAPrivateNoteByPLDIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();
        PrivateNoteCreateDTO.setCheckinid(checkIn.getId());
        PrivateNoteCreateDTO.setCreatedbyid(checkIn.getPdlId());
        PrivateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", PrivateNoteCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User1 is unauthorized to do this operation", error);

    }

    //////////////////////////////// UPDATE TESTS///////////////////////////////

    @Test
    void testUpdateInvalidPrivateNote() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);
        PrivateNote.setCreatedbyid(null);
        PrivateNote.setCheckinid(null);

        final HttpRequest<PrivateNote> request = HttpRequest.PUT("", PrivateNote).basicAuth("test@test.com", PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));


        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("privateNote.checkinid: must not be null", errorList.get(0));
        assertEquals("privateNote.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateNullPrivateNote() {
        final HttpRequest<?> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [privateNote] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateUnAuthorized() {
        PrivateNote cNote = new PrivateNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        final HttpRequest<PrivateNote> request = HttpRequest.PUT("", cNote);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

    @Test
    void testMemberAbleToUpdateHisPrivateNotes() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.PUT("", PrivateNote).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertNotNull(response);
        assertEquals(PrivateNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdatePrivateNoteByPDL() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.PUT("", PrivateNote).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertNotNull(response);
        assertEquals(PrivateNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testAdminUnableToUpdatePrivateNotes() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.PUT("", PrivateNote).basicAuth(memberProfileForPDL.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);
    }

    @Test
    void testUpdatePrivateNoteByMemberIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();
        PrivateNoteCreateDTO.setCheckinid(checkIn.getId());
        PrivateNoteCreateDTO.setCreatedbyid(checkIn.getTeamMemberId());
        PrivateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.PUT("", PrivateNoteCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testUpdateAPrivateNoteByPLDIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();
        PrivateNoteCreateDTO.setCheckinid(checkIn.getId());
        PrivateNoteCreateDTO.setCreatedbyid(checkIn.getPdlId());
        PrivateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.PUT("", PrivateNoteCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }
}
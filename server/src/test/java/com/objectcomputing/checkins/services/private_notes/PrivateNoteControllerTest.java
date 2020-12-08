package com.objectcomputing.checkins.services.private_notes;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
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

    @Test
    void testCreatePrivateNoteByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();
        PrivateNoteCreateDTO.setCheckinid(checkIn.getId());
        PrivateNoteCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        PrivateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", PrivateNoteCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        PrivateNote PrivateNote = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(PrivateNoteCreateDTO.getCheckinid(), PrivateNote.getCheckinid());
        assertEquals(PrivateNoteCreateDTO.getCreatedbyid(), PrivateNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), PrivateNote.getId()), response.getHeaders().get("location"));
    }


    @Test
    void testCreatePrivateNoteByPdl() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();
        PrivateNoteCreateDTO.setCheckinid(checkIn.getId());
        PrivateNoteCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        PrivateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", PrivateNoteCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), PDL_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        PrivateNote PrivateNote = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(PrivateNoteCreateDTO.getCheckinid(), PrivateNote.getCheckinid());
        assertEquals(PrivateNoteCreateDTO.getCreatedbyid(), PrivateNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), PrivateNote.getId()), response.getHeaders().get("location"));
    }

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
        assertEquals("PrivateNote.checkinid: must not be null", errorList.get(0));
        assertEquals("PrivateNote.createdbyid: must not be null", errorList.get(1));
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

        assertEquals("Required Body [PrivateNote] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateAPrivateNoteForNonExistingCheckInId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();
        PrivateNoteCreateDTO.setCheckinid(UUID.randomUUID());
        PrivateNoteCreateDTO.setCreatedbyid(memberProfile.getId());
        PrivateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", PrivateNoteCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("CheckIn %s doesn't exist", PrivateNoteCreateDTO.getCheckinid()), error);

    }

    @Test
    void testCreateAPrivateNoteForNonExistingMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNoteCreateDTO PrivateNoteCreateDTO = new PrivateNoteCreateDTO();
        PrivateNoteCreateDTO.setCheckinid(checkIn.getId());
        PrivateNoteCreateDTO.setCreatedbyid(UUID.randomUUID());
        PrivateNoteCreateDTO.setDescription("test");

        final HttpRequest<PrivateNoteCreateDTO> request = HttpRequest.POST("", PrivateNoteCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist", PrivateNoteCreateDTO.getCreatedbyid()), error);

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
        assertEquals("User is unauthorized to do this operation", error);

    }


    @Test
    void testReadPrivateNoteByPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", PrivateNote.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<PrivateNote>> response = client.toBlocking().exchange(request, Argument.setOf(PrivateNote.class));

        assertEquals(Set.of(PrivateNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadPrivateNoteByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", PrivateNote.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<PrivateNote>> response = client.toBlocking().exchange(request, Argument.setOf(PrivateNote.class));

        assertEquals(Set.of(PrivateNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

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
        assertEquals(String.format("Invalid checkin note id %s", randomCheckinID), error);

    }

    @Test
    void testReadPrivateNoteNotFoundByUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();


        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", PrivateNote.getId())).basicAuth(memberProfileOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindAllPrivateNoteByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<PrivateNote>> response = client.toBlocking().exchange(request, Argument.setOf(PrivateNote.class));

        assertEquals(Set.of(PrivateNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllPrivateNoteByNonAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileForPDL.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindPrivateNoteByBothCheckinIdAndCreateByid() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", PrivateNote.getCheckinid(), PrivateNote.getCreatedbyid()))
                .basicAuth(memberProfile.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<PrivateNote>> response = client.toBlocking().exchange(request, Argument.setOf(PrivateNote.class));

        assertEquals(Set.of(PrivateNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }


    @Test
    void testFindPrivateNoteByMemberIdForAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileForUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", PrivateNote.getCreatedbyid()))
                .basicAuth(memberProfileForUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<PrivateNote>> response = client.toBlocking().exchange(request, Argument.setOf(PrivateNote.class));

        assertEquals(Set.of(PrivateNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindPrivateNoteByCheckinIdForAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileForUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", PrivateNote.getCheckinid()))
                .basicAuth(memberProfileForUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<PrivateNote>> response = client.toBlocking().exchange(request, Argument.setOf(PrivateNote.class));

        assertEquals(Set.of(PrivateNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindPrivateNoteByCheckinIdForPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", PrivateNote.getCheckinid()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<PrivateNote>> response = client.toBlocking().exchange(request, Argument.setOf(PrivateNote.class));

        assertEquals(Set.of(PrivateNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindPrivateNoteByCheckinIdForUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfile1 = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", PrivateNote.getCheckinid()))
                .basicAuth(memberProfile1.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindPrivateNoteByCreatedByIdByMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", PrivateNote.getCreatedbyid()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<PrivateNote>> response = client.toBlocking().exchange(request, Argument.setOf(PrivateNote.class));

        assertEquals(Set.of(PrivateNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindPrivateNoteByCreatedByIdByUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfile1 = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", PrivateNote.getCreatedbyid()))
                .basicAuth(memberProfile1.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }


    @Test
    void testUpdatePrivateNoteByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.PUT("", PrivateNote).basicAuth(memberProfileForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertEquals(PrivateNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdatePrivateNoteByPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.PUT("", PrivateNote).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertEquals(PrivateNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

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
        assertEquals("PrivateNote.checkinid: must not be null", errorList.get(0));
        assertEquals("PrivateNote.createdbyid: must not be null", errorList.get(1));
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
        assertEquals("Required Body [PrivateNote] not specified", errors.asText());
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
    void testUpdateNonExistingPrivateNote() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);
        PrivateNote.setId(UUID.randomUUID());

        final HttpRequest<PrivateNote> request = HttpRequest.PUT("", PrivateNote)
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate checkin note to update with id %s", PrivateNote.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingPrivateNoteForCheckInId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);
        PrivateNote.setCheckinid(UUID.randomUUID());

        final HttpRequest<PrivateNote> request = HttpRequest.PUT("", PrivateNote)
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckIn %s doesn't exist", PrivateNote.getCheckinid()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingPrivateNoteForMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);
        PrivateNote.setCreatedbyid(UUID.randomUUID());

        final HttpRequest<PrivateNote> request = HttpRequest.PUT("", PrivateNote)
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", PrivateNote.getCreatedbyid()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdatePrivateNoteForUnrelatedUserByPdlWhenCompleted() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createACompletedCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<PrivateNote> request = HttpRequest.PUT("", PrivateNote)
                .basicAuth(memberProfileOfMrNobody.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("User is unauthorized to do this operation", error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdatePrivateNoteForUnrelatedUserByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        PrivateNote PrivateNote = createADeafultPrivateNote(checkIn, memberProfile);

        final HttpRequest<PrivateNote> request = HttpRequest.PUT("", PrivateNote)
                .basicAuth(memberProfileOfMrNobody.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<PrivateNote> response = client.toBlocking().exchange(request, PrivateNote.class);

        assertEquals(PrivateNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }
}

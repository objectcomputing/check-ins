package com.objectcomputing.checkins.services.checkin_notes;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.CheckInNoteFixture;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CheckinNoteControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, CheckInNoteFixture {

    @Inject
    @Client("/services/checkin-note")
    HttpClient client;

    @Test
    void testCreateCheckinNoteByAdmin() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(memberProfileEntityOfPDL.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        CheckinNote checkinNote = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinNote.getCheckinid());
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), checkinNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), checkinNote.getId()), response.getHeaders().get("location"));
    }


    @Test
    void testCreateCheckinNoteByPdl() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(memberProfileEntityOfPDL.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(memberProfileEntityOfUser.getWorkEmail(), PDL_ROLE);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        CheckinNote checkinNote = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinNote.getCheckinid());
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), checkinNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), checkinNote.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateInvalidCheckinNote() {
        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth("test@test.com", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("checkinNote.checkinid: must not be null", errorList.get(0));
        assertEquals("checkinNote.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateNullCheckinNote() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [checkinNote] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateACheckInNoteForNonExistingCheckInId() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(UUID.randomUUID());
        checkinNoteCreateDTO.setCreatedbyid(memberProfileEntity.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("CheckIn %s doesn't exist", checkinNoteCreateDTO.getCheckinid()), error);

    }

    @Test
    void testCreateACheckInNoteForNonExistingMemberIdId() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(UUID.randomUUID());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist", checkinNoteCreateDTO.getCreatedbyid()), error);

    }

    @Test
    void testCreateACheckInNoteByPLDIdWhenCompleted() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(checkIn.getPdlId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }


    @Test
    void testReadCheckinNoteByPDL() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId())).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadCheckinNoteByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId())).basicAuth(memberProfileEntityForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadCheckinNoteNotFound() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();

        UUID randomCheckinID = UUID.randomUUID();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid checkin note id %s", randomCheckinID), error);

    }

    @Test
    void testReadCheckinNoteNotFoundByUnrelatedUser() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();


        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId())).basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindAllCheckinNoteByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllCheckinNoteByNonAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindCheckinNoteByBothCheckinIdAndCreateByid() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", checkinNote.getCheckinid(), checkinNote.getCreatedbyid()))
                .basicAuth(memberProfileEntity.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }


    @Test
    void testFindCheckinNoteByMemberIdForAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityForUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", checkinNote.getCreatedbyid()))
                .basicAuth(memberProfileEntityForUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteByCheckinIdForAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityForUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", checkinNote.getCheckinid()))
                .basicAuth(memberProfileEntityForUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteByCheckinIdForPDL() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", checkinNote.getCheckinid()))
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteByCheckinIdForUnrelatedUser() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntity1 = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", checkinNote.getCheckinid()))
                .basicAuth(memberProfileEntity1.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindCheckinNoteByCreatedByIdByMember() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntityForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", checkinNote.getCreatedbyid()))
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteByCreatedByIdByUnrelatedUser() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntity1 = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntityForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", checkinNote.getCreatedbyid()))
                .basicAuth(memberProfileEntity1.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }


    @Test
    void testUpdateCheckinNoteByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.PUT("", checkinNote).basicAuth(memberProfileEntityForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(checkinNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateCheckinNoteByPDL() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.PUT("", checkinNote).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(checkinNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateInvalidCheckinNote() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);
        checkinNote.setCreatedbyid(null);
        checkinNote.setCheckinid(null);

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote).basicAuth("test@test.com", PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));


        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("checkinNote.checkinid: must not be null", errorList.get(0));
        assertEquals("checkinNote.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateNullCheckinNote() {
        final HttpRequest<?> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [checkinNote] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateUnAuthorized() {
        CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", cNote);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

    @Test
    void testUpdateNonExistingCheckInNote() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);
        checkinNote.setId(UUID.randomUUID());

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate checkin note to update with id %s", checkinNote.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingCheckInNoteForCheckInId() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);
        checkinNote.setCheckinid(UUID.randomUUID());

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckIn %s doesn't exist", checkinNote.getCheckinid()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingCheckInNoteForMemberId() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);
        checkinNote.setCreatedbyid(UUID.randomUUID());

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", checkinNote.getCreatedbyid()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateCheckInNoteForUnrelatedUserByPdlWhenCompleted() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createACompletedCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals("User is unauthorized to do this operation", error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateCheckInNoteForUnrelatedUserByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        CheckinNote checkinNote = createADeafultCheckInNote(checkIn, memberProfileEntity);

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(checkinNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }
}

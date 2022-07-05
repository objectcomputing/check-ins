package com.objectcomputing.checkins.services.checkin_notes;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.CheckInNoteFixture;
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
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.PDL_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CheckinNoteControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, CheckInNoteFixture, RoleFixture {

    @Inject
    @Client("/services/checkin-notes")
    HttpClient client;

    @Test
    void testCreateCheckinNoteByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = createAndAssignAdminRole(memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        CheckinNote checkinNote = response.body();

        assertNotNull(checkinNote);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinNote.getCheckinid());
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), checkinNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), checkinNote.getId()), response.getHeaders().get("location"));
    }


    @Test
    void testCreateCheckinNoteByPdl() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileOfUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        CheckinNote checkinNote = response.body();

        assertNotNull(checkinNote);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinNote.getCheckinid());
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), checkinNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), checkinNote.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateCheckinNoteByMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.MEMBER, memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(memberProfileForPDL.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfile.getWorkEmail(), role.getRole());
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        CheckinNote checkinNote = response.body();

        assertNotNull(checkinNote);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinNote.getCheckinid());
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), checkinNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), checkinNote.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateInvalidCheckinNote() {
        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth("test@test.com", ADMIN_ROLE);
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

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth("test@test.com", PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [checkinNote] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateACheckInNoteForNonExistingCheckInId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfile);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(UUID.randomUUID());
        checkinNoteCreateDTO.setCreatedbyid(memberProfile.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("CheckIn %s doesn't exist", checkinNoteCreateDTO.getCheckinid()), error);

    }

    @Test
    void testCreateACheckInNoteForNonExistingMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(UUID.randomUUID());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
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
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(checkIn.getPdlId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfileOfPDL.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testCreateThrowsPermissionException() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = createAndAssignRole(RoleType.MEMBER, memberProfileOfUser);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(checkIn.getPdlId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfileOfUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);
    }

    @Test
    void testCreateCheckinNoteByUnrelatedPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();
        Role pdlRole = createAndAssignRole(RoleType.PDL, memberProfileOfMrNobody);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(checkIn.getPdlId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<?> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfileOfMrNobody.getWorkEmail(), pdlRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("You do not have permission to access this resource", error);

    }

    @Test
    void testCreateCheckinNoteByPDLWhoCreatedIt() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfFormerPDL = createAnUnrelatedUser();
        Role pdlRole = createAndAssignRole(RoleType.PDL, memberProfileOfFormerPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileOfFormerPDL);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(checkIn.getPdlId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<?> request = HttpRequest.POST("", checkinNoteCreateDTO)
                .basicAuth(memberProfileOfFormerPDL.getWorkEmail(), pdlRole.getRole());

        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        CheckinNote checkinNote = response.body();

        assertNotNull(checkinNote);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinNote.getCheckinid());
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), checkinNote.getCreatedbyid());

    }

    @Test
    void testReadCheckinNoteByPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadCheckinNoteByMEMBER() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.MEMBER, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId()))
                .basicAuth(memberProfile.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadCheckinNoteByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.ADMIN, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadCheckinNoteByPDLNotOnCheckin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileForFormerPDL = createASecondDefaultMemberProfileForPdl(memberProfile);
        Role pdlRole = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForFormerPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), pdlRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testReadCheckinNoteNotFound() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        Role pdlRole = createAndAssignRole(RoleType.PDL, memberProfileOfPDL);

        UUID randomCheckinID = UUID.randomUUID();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID))
                .basicAuth(memberProfileOfPDL.getWorkEmail(), pdlRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid checkin note id %s", randomCheckinID), error);

    }

    @Test
    void testReadCheckinNoteNotFoundByUnrelatedPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();
        Role pdlRole = createAndAssignRole(RoleType.PDL, memberProfileOfMrNobody);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId()))
                .basicAuth(memberProfileOfMrNobody.getWorkEmail(), pdlRole.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testReadCheckinNoteByFormerPDLWhoCreatedIt() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileOfFormerPDL = createAnUnrelatedUser();
        Role pdlRole = createAndAssignRole(RoleType.PDL, memberProfileOfFormerPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileOfFormerPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfileOfFormerPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", checkinNote.getId()))
                .basicAuth(memberProfileOfFormerPDL.getWorkEmail(), pdlRole.getRole());
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertNotNull(checkinNote);
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllCheckinNoteByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role pdlRole = createAndAssignRole(RoleType.ADMIN, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileForPDL.getWorkEmail(), pdlRole.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllCheckinNoteByNonAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.MEMBER, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindCheckinNoteByBothCheckinIdAndCreateByid() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", checkinNote.getCheckinid(), checkinNote.getCreatedbyid()))
                .basicAuth(memberProfile.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }


    @Test
    void testFindCheckinNoteByMemberIdForAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileForUnrelatedUser = createAnUnrelatedUser();
        Role role = createAndAssignRole(RoleType.ADMIN, memberProfileForUnrelatedUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", checkinNote.getCreatedbyid()))
                .basicAuth(memberProfileForUnrelatedUser.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteByCheckinIdForAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileForUnrelatedUser = createAnUnrelatedUser();
        Role role = createAndAssignRole(RoleType.ADMIN, memberProfileForUnrelatedUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", checkinNote.getCheckinid()))
                .basicAuth(memberProfileForUnrelatedUser.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteByCheckinIdForPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", checkinNote.getCheckinid()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteByCheckinIdForUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile unrelatedUser = createAnUnrelatedUser();
        Role role = createAndAssignRole(RoleType.PDL, unrelatedUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", checkinNote.getCheckinid()))
                .basicAuth(unrelatedUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindCheckinNoteByCreatedByIdByMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", checkinNote.getCreatedbyid()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
        final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));

        assertEquals(Set.of(checkinNote), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindCheckinNoteByCreatedByIdByUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile anUnrelatedUser = createAnUnrelatedUser();
        Role role = createAndAssignRole(RoleType.PDL, anUnrelatedUser);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", checkinNote.getCreatedbyid()))
                .basicAuth(anUnrelatedUser.getWorkEmail(), role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testUpdateCheckinNoteByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.ADMIN, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);
        checkinNote.setDescription("new description");

        final HttpRequest<?> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(checkinNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateCheckinNoteByPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(checkinNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateCheckinNoteByMEMBER() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.MEMBER, memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfile.getWorkEmail(), role.getRole());
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(checkinNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateInvalidCheckinNote() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);
        checkinNote.setCreatedbyid(null);
        checkinNote.setCheckinid(null);

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth("test@test.com", role.getRole());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        Role role = createAndAssignRole(RoleType.PDL, memberProfile);

        final HttpRequest<?> request = HttpRequest.PUT("", "")
                .basicAuth("test@test.com", role.getRole());
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [checkinNote] not specified", error.asText());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);
        checkinNote.setId(UUID.randomUUID());

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);
        checkinNote.setCheckinid(UUID.randomUUID());

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role role = createAndAssignRole(RoleType.PDL, memberProfileForPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);
        checkinNote.setCreatedbyid(UUID.randomUUID());

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileForPDL.getWorkEmail(), role.getRole());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();
        Role role = createAndAssignRole(RoleType.PDL, memberProfileOfMrNobody);

        CheckIn checkIn = createACompletedCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileOfMrNobody.getWorkEmail(), role.getRole());
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
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();
        Role role = createAndAssignRole(RoleType.ADMIN, memberProfileOfMrNobody);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfile);

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileOfMrNobody.getWorkEmail(), role.getRole());
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        assertEquals(checkinNote, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdateThrowsPermissionException() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileForUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        Role role = createAndAssignRole(RoleType.MEMBER, memberProfileForUser);

        CheckIn checkIn = createACompletedCheckIn(memberProfileForUser, memberProfileOfPDL);
        CheckinNote checkinNote = createADefaultCheckInNote(checkIn, memberProfileForUser);

        final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)
                .basicAuth(memberProfileForUser.getWorkEmail(), role.getRole());
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);
    }
}

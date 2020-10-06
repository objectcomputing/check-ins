package com.objectcomputing.checkins.services.action_item;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNote;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNoteCreateDTO;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.ActionItemFixture;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
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


class ActionItemControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, ActionItemFixture {

    @Inject
    @Client("/services/action-item")
    HttpClient client;

@Test
void testCreateAnActionItemByAdmin() {

    MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
    MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

    CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

    ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
    actionItemCreateDTO.setCheckinid(checkIn.getId());
    actionItemCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
    actionItemCreateDTO.setDescription("dnc");

    final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), ADMIN_ROLE);
    final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

    ActionItem actionItem= response.body();

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatus());
    assertEquals(actionItemCreateDTO.getCheckinid(),actionItem.getCheckinid());
    assertEquals(actionItemCreateDTO.getCreatedbyid(),actionItem.getCreatedbyid());
    assertEquals(String.format("%s/%s", request.getPath(), actionItem.getId()), response.getHeaders().get("location"));
}

    @Test
    void testCreateActionItemByPdl() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), PDL_ROLE);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        CheckinNote checkinNote = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinNote.getCheckinid());
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), checkinNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), checkinNote.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateActionItemBySubjectOfCheckin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        checkinNoteCreateDTO.setCheckinid(checkIn.getId());
        checkinNoteCreateDTO.setCreatedbyid(memberProfileOfUser.getId());
        checkinNoteCreateDTO.setDescription("test");

        final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);

        CheckinNote checkinNote = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinNote.getCheckinid());
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), checkinNote.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), checkinNote.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAnInvalidActionItem() {
        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth("test@test.com",ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("actionItem.checkinid: must not be null", errorList.get(0));
        assertEquals("actionItem.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullActionItem() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE,PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [actionItem] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateAnActionItemForExistingCheckInId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO.setCreatedbyid(memberProfile.getId());
        actionItemCreateDTO.setDescription("test");

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("",actionItemCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("CheckIn %s doesn't exist",actionItemCreateDTO.getCheckinid()),error);

    }

    @Test
    void testCreateAnActionItemForNonExistingMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        actionItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        actionItemCreateDTO.setDescription("test");

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("",actionItemCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals(String.format("Member %s doesn't exist",actionItemCreateDTO.getCreatedbyid()),error);

    }

    @Test
    void testCreateAnActionItemByPLDIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        actionItemCreateDTO.setCreatedbyid(checkIn.getPdlId());
        actionItemCreateDTO.setDescription("test");

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }


    @Test
    void testLoadActionItems() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL,memberProfileOfUser);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        actionItemCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        actionItemCreateDTO.setDescription("dnc");

        ActionItemCreateDTO actionItemCreateDTO2 = new ActionItemCreateDTO();
        actionItemCreateDTO2.setCheckinid(checkIn.getId());
        actionItemCreateDTO2.setCreatedbyid(memberProfileOfPDL.getId());
        actionItemCreateDTO2.setDescription("dnc");

        List<ActionItemCreateDTO> dtoList = List.of(actionItemCreateDTO, actionItemCreateDTO2);

        final MutableHttpRequest<List<ActionItemCreateDTO>> request = HttpRequest.POST("items", dtoList).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<List<ActionItem>> response = client.toBlocking().exchange(request, Argument.listOf(ActionItem.class));

        List<ActionItem> actionItem= response.body();

        assertNotNull(response);
        assertEquals(actionItem.get(0).getCheckinid(), actionItemCreateDTO.getCheckinid());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

    }

    @Test
    void testLoadActionItemsInvalidActionItem() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        actionItemCreateDTO.setDescription("dnc");

        ActionItemCreateDTO actionItemCreateDTO2 = new ActionItemCreateDTO();

        List<ActionItemCreateDTO> dtoList = List.of(actionItemCreateDTO, actionItemCreateDTO2);

        final MutableHttpRequest<List<ActionItemCreateDTO>> request = HttpRequest.POST("items", dtoList).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());

        assertEquals("actionItems.checkinid: must not be null", errorList.get(0));
        assertEquals("actionItems.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testLoadActionItemsThrowException() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser,memberProfileOfPDL);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        actionItemCreateDTO.setCreatedbyid(memberProfileOfUser.getId());
        actionItemCreateDTO.setDescription("dnc");

        ActionItemCreateDTO actionItemCreateDTO2 = new ActionItemCreateDTO();
        actionItemCreateDTO2.setCheckinid(UUID.randomUUID());
        actionItemCreateDTO2.setCreatedbyid(memberProfileOfUser.getId());
        actionItemCreateDTO2.setDescription("dnc");

        List<ActionItemCreateDTO> dtoList = List.of(actionItemCreateDTO, actionItemCreateDTO2);

        final MutableHttpRequest<List<ActionItemCreateDTO>> request = HttpRequest.POST("items", dtoList).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        final String errorMessage = String.format("CheckIn %s doesn't exist",actionItemCreateDTO2.getCheckinid());

        assertEquals(String.format("[\"Member %s's action item was not added to CheckIn %s because: %s\"]",
                actionItemCreateDTO2.getCreatedbyid(), actionItemCreateDTO2.getCheckinid(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

    }

    @Test
    void deleteActionItem() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);
        final HttpRequest<?> request = HttpRequest.DELETE(actionItem.getId().toString()).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadActionItemByIdByPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", actionItem.getId().toString())).basicAuth(memberProfileForPDL.getWorkEmail(),PDL_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadCheckinNoteByAdmin(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s",actionItem.getId())).basicAuth(memberProfileForPDL.getWorkEmail(),ADMIN_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadActionItemFoundByUnrelatedUser(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s",actionItem.getId())).basicAuth(memberProfileOfMrNobody.getWorkEmail(),PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testReadActionItemNotFound() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();

        UUID randomCheckinID = UUID.randomUUID();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid action item id %s", randomCheckinID), error);

    }

    @Test
    void testFindActionItems() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", actionItem.getCheckinid(),
                actionItem.getCreatedbyid())).basicAuth(memberProfile.getWorkEmail(),MEMBER_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindAllActionItemsByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileForPDL.getWorkEmail(),ADMIN_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllActionItemByNonAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileForPDL.getWorkEmail(),MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(),href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testUpdateActionItem() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        assertEquals(actionItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), actionItem.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateAnInvalidActionItem() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);
        actionItem.setCreatedbyid(null);
        actionItem.setCheckinid(null);

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("actionItem.checkinid: must not be null", errorList.get(0));
        assertEquals("actionItem.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullActionItem() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [actionItem] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateUnAuthorized() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),"test");

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

    @Test
    void testUpdateNonExistingActionItem(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);
        actionItem.setId(UUID.randomUUID());

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate actionItem to update with id %s", actionItem.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingActionItemForCheckInId(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);
        actionItem.setCheckinid(UUID.randomUUID());

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckIn %s doesn't exist", actionItem.getCheckinid()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingActionItemForMemberId(){
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);

        ActionItem actionItem = createADeafultActionItem(checkIn,memberProfile);
        actionItem.setCreatedbyid(UUID.randomUUID());

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", actionItem.getCreatedbyid()), error);
        assertEquals(request.getPath(), href);

    }

}

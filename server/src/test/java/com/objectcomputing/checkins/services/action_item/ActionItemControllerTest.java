package com.objectcomputing.checkins.services.action_item;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.ActionItemFixture;
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

import jakarta.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ActionItemControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, CheckInFixture, ActionItemFixture {

    @Inject
    @Client("/services/action-items")
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

        ActionItem actionItem = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(actionItemCreateDTO.getCheckinid(), actionItem.getCheckinid());
        assertEquals(actionItemCreateDTO.getCreatedbyid(), actionItem.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), actionItem.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateActionItemByPdl() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        actionItemCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        actionItemCreateDTO.setDescription("dnc");

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        ActionItem actionItem = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(actionItemCreateDTO.getCheckinid(), actionItem.getCheckinid());
        assertEquals(actionItemCreateDTO.getCreatedbyid(), actionItem.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), actionItem.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateActionItemBySubjectOfCheckin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        actionItemCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        actionItemCreateDTO.setDescription("dnc");

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        ActionItem actionItem = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(actionItemCreateDTO.getCheckinid(), actionItem.getCheckinid());
        assertEquals(actionItemCreateDTO.getCreatedbyid(), actionItem.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), actionItem.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAnInvalidActionItem() {
        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth("test@test.com", ADMIN_ROLE);
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

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        JsonNode embeddedErrors = body.get("_embedded").get("errors");
        JsonNode error = embeddedErrors.get(0).get("message");

        assertEquals("Required Body [actionItem] not specified", error.asText());
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

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid checkin id %s", actionItemCreateDTO.getCheckinid()), error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateAnActionItemForNonExistingMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        final UUID memberId = UUID.randomUUID();
        actionItemCreateDTO.setCreatedbyid(memberId);
        actionItemCreateDTO.setDescription("test");

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("No member profile for id " + memberId, error);

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
    void testCreateAnActionItemByMemberIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        actionItemCreateDTO.setCreatedbyid(checkIn.getTeamMemberId());
        actionItemCreateDTO.setDescription("test");

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testCreateAnActionItemByMemberIdWhenNotCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);

        ActionItemCreateDTO actionItemCreateDTO = new ActionItemCreateDTO();
        actionItemCreateDTO.setCheckinid(checkIn.getId());
        actionItemCreateDTO.setCreatedbyid(checkIn.getTeamMemberId());
        actionItemCreateDTO.setDescription("test");

        final HttpRequest<ActionItemCreateDTO> request = HttpRequest.POST("", actionItemCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        ActionItem actionItem = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(actionItemCreateDTO.getCheckinid(), actionItem.getCheckinid());
        assertEquals(actionItemCreateDTO.getCreatedbyid(), actionItem.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), actionItem.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testDeleteActionItemByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfPDL);
        final HttpRequest<?> request = HttpRequest.DELETE(actionItem.getId().toString()).basicAuth(memberProfileOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteActionItemByPdl() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", actionItem.getId())).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteActionItemByMember() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", actionItem.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteAnActionItemByADMINIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);
        createAndAssignAdminRole(memberProfileOfUser);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", actionItem.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteAnActionItemByPDLIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", actionItem.getId())).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testDeleteAnActionItemByMEMBERIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfUser, memberProfileOfPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", actionItem.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testReadActionItemByIdByPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", actionItem.getId().toString())).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadActionItemByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", actionItem.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadActionItemFoundByUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", actionItem.getId())).basicAuth(memberProfileOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("You are not authorized to perform this operation", error);

    }

    @Test
    void testReadActionItemNotFound() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();

        UUID randomCheckinID = UUID.randomUUID();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = body.get("_embedded").get("errors").get(0).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("actionItem: must not be null", error);

    }

    @Test
    void testFindActionItems() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", actionItem.getCheckinid(),
                actionItem.getCreatedbyid())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindActionItemsByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", actionItem.getCheckinid(),
                actionItem.getCreatedbyid())).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindAllActionItemsByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForAdmin = createADefaultMemberProfileForPdl(memberProfile);
        createAndAssignAdminRole(memberProfileForAdmin);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForAdmin);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileForAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllActionItemByNonAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

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
    void testFindActionItemsBYCreatedby() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s",
                actionItem.getCreatedbyid())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindActionItemsBYCreatedbyAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s",
                actionItem.getCreatedbyid())).basicAuth(memberProfile.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<ActionItem>> response = client.toBlocking().exchange(request, Argument.setOf(ActionItem.class));

        assertEquals(Set.of(actionItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateActionItem() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfUser, memberProfileOfPDL);
        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfUser);

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem).basicAuth(memberProfileOfPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        assertEquals(actionItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), actionItem.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateActionItemByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.PUT("", actionItem).basicAuth(memberProfileOfUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        assertEquals(actionItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateCompletedActionItemByAdmin() {  //!admin and isCompleted
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.PUT("", actionItem).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));


        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("User is unauthorized to do this operation", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

    }

    @Test
    void testUpdateActionItemByPDL() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.PUT("", actionItem).basicAuth(memberProfileOfUser.getWorkEmail(), PDL_ROLE);
        final HttpResponse<ActionItem> response = client.toBlocking().exchange(request, ActionItem.class);

        assertEquals(actionItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }


    @Test
    void testUpdateAnInvalidActionItem() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfile);
        actionItem.setCreatedbyid(null);
        actionItem.setCheckinid(null);

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem).basicAuth("test@test.com", PDL_ROLE);
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

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [actionItem] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateUnAuthorized() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

    @Test
    void testUpdateNonExistingActionItem() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfMember);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfPDL);
        actionItem.setId(UUID.randomUUID());

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem)
                .basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate action item to update with id %s", actionItem.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingActionItemForCheckInId() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfMember);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfPDL);
        actionItem.setCheckinid(UUID.randomUUID());

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem)
                .basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid checkin id %s", actionItem.getCheckinid()), error);

    }

    @Test
    void testUpdateNonExistingActionItemForMemberId() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfMember = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfMember);

        ActionItem actionItem = createADefaultActionItem(checkIn, memberProfileOfPDL);
        final UUID memberId = UUID.randomUUID();
        actionItem.setCreatedbyid(memberId);

        final HttpRequest<ActionItem> request = HttpRequest.PUT("", actionItem)
                .basicAuth(memberProfileOfMember.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("No member profile for id " + memberId, error);

    }

}

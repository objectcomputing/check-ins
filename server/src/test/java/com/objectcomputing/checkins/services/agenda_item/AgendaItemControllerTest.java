package com.objectcomputing.checkins.services.agenda_item;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.AgendaItemFixture;
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
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class AgendaItemControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, AgendaItemFixture, RoleFixture {

    @Inject
    @Client("/services/agenda-items")
    HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testCreateAgendaItemByAdmin() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        AgendaItem agendaItem = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(agendaItemCreateDTO.getCheckinid(), agendaItem.getCheckinid());
        assertEquals(agendaItemCreateDTO.getCreatedbyid(), agendaItem.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), agendaItem.getId()), response.getHeaders().get("location"));
    }


    @Test
    void testCreateAgendaItemByPdl() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), PDL_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        AgendaItem agendaItem = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(agendaItemCreateDTO.getCheckinid(), agendaItem.getCheckinid());
        assertEquals(agendaItemCreateDTO.getCreatedbyid(), agendaItem.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), agendaItem.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAgendaItemByMember() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileOfPDL, memberProfileOfUser);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(memberProfileOfPDL.getId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        AgendaItem agendaItem = response.body();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(agendaItemCreateDTO.getCheckinid(), agendaItem.getCheckinid());
        assertEquals(agendaItemCreateDTO.getCreatedbyid(), agendaItem.getCreatedbyid());
        assertEquals(String.format("%s/%s", request.getPath(), agendaItem.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateInvalidAgendaItem() {
        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth("test@test.com", ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("agendaItem.checkinid: must not be null", errorList.get(0));
        assertEquals("agendaItem.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateNullAgendaItem() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [agendaItem] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateAAgendaItemForNonExistingCheckInId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(UUID.randomUUID());
        agendaItemCreateDTO.setCreatedbyid(memberProfile.getId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("CheckIn %s doesn't exist", agendaItemCreateDTO.getCheckinid()), error);

    }

    @Test
    void testCreateAAgendaItemForNonExistingMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist", agendaItemCreateDTO.getCreatedbyid()), error);

    }

    @Test
    void testCreateAAgendaItemByPLDIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(checkIn.getPdlId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testReadAgendaItemByPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadAgendaItemByMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadAgendaItemByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadAgendaItemNotFound() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();

        UUID randomCheckinID = UUID.randomUUID();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid agenda item id %s", randomCheckinID), error);

    }

    @Test
    void testReadAgendaItemNotFoundByUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);


    }

    @Test
    void testFindAllAgendaItemByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileOfUser);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileOfUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllAgendaItemByNonAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileForPDL.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertTrue(error.contains("User is unauthorized to do this operation"));

    }

    @Test
    void testFindAgendaItemByBothCheckinIdAndCreateByid() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", agendaItem.getCheckinid(), agendaItem.getCreatedbyid()))
                .basicAuth(memberProfile.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }


    @Test
    void testFindAgendaItemByMemberIdForAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileForUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", agendaItem.getCreatedbyid()))
                .basicAuth(memberProfileForUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAgendaItemByCheckinIdForAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileForUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", agendaItem.getCheckinid()))
                .basicAuth(memberProfileForUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAgendaItemByCheckinIdForPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", agendaItem.getCheckinid()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAgendaItemByCheckinIdForUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfile1 = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", agendaItem.getCheckinid()))
                .basicAuth(memberProfile1.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindAgendaItemByCreatedByIdByMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", agendaItem.getCreatedbyid()))
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAgendaItemByCreatedByIdByUnrelatedUser() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfile1 = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfileForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", agendaItem.getCreatedbyid()))
                .basicAuth(memberProfile1.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }


    @Test
    void testUpdateAgendaItemByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.PUT("", agendaItem).basicAuth(memberProfileForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(agendaItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateAgendaItemByPDL() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.PUT("", agendaItem).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(agendaItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }


    @Test
    void testUpdateAgendaItemByMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.PUT("", agendaItem).basicAuth(memberProfileForPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(agendaItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateInvalidAgendaItem() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);
        agendaItem.setCreatedbyid(null);
        agendaItem.setCheckinid(null);

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem).basicAuth("test@test.com", PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));


        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("agendaItem.checkinid: must not be null", errorList.get(0));
        assertEquals("agendaItem.createdbyid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateNullAgendaItem() {
        final HttpRequest<?> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [agendaItem] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateUnAuthorized() {
        AgendaItem cItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", cItem);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
        assertEquals("Unauthorized", responseException.getMessage());
    }

    @Test
    void testUpdateNonExistingAgendaItem() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);
        agendaItem.setId(UUID.randomUUID());

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate agenda item to update with id %s", agendaItem.getId()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingAgendaItemForCheckInId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);
        agendaItem.setCheckinid(UUID.randomUUID());

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("CheckIn %s doesn't exist", agendaItem.getCheckinid()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateNonExistingAgendaItemForMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);
        agendaItem.setCreatedbyid(UUID.randomUUID());

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
                .basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", agendaItem.getCreatedbyid()), error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void testUpdateAgendaItemForUnrelatedUserByPdlWhenCompleted() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createACompletedCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
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
    void testUpdateAgendaItemForUnrelatedUserByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        MemberProfile memberProfileOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
                .basicAuth(memberProfileOfMrNobody.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(agendaItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteAgendaItemByAdmin() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteAgendaItemByPdl() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteAgendaItemByMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);

        CheckIn checkIn = createADefaultCheckIn(memberProfile, memberProfileForPDL);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfile);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileForPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteAAgendaItemByADMINIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteAAgendaItemByPDLIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileOfUser.getWorkEmail(),PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testDeleteAAgendaItemByMEMBERIdWhenCompleted() {
        MemberProfile memberProfileOfPDL = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileOfPDL, memberProfileOfUser);

        AgendaItem agendaItem = createADefaultAgendaItem(checkIn, memberProfileOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

}

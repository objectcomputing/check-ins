package com.objectcomputing.checkins.services.agenda_item;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.AgendaItemFixture;
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


public class AgendaItemControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, AgendaItemFixture {

    @Inject
    @Client("/services/agenda-item")
    HttpClient client;

    @Test
    void testCreateAgendaItemByAdmin() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(memberProfileEntityOfPDL.getId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), ADMIN_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(memberProfileEntityOfPDL.getId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileEntityOfUser.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(memberProfileEntityOfPDL.getId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
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
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [agendaItem] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateAAgendaItemForNonExistingCheckInId() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(UUID.randomUUID());
        agendaItemCreateDTO.setCreatedbyid(memberProfileEntity.getId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(UUID.randomUUID());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        AgendaItemCreateDTO agendaItemCreateDTO = new AgendaItemCreateDTO();
        agendaItemCreateDTO.setCheckinid(checkIn.getId());
        agendaItemCreateDTO.setCreatedbyid(checkIn.getPdlId());
        agendaItemCreateDTO.setDescription("test");

        final HttpRequest<AgendaItemCreateDTO> request = HttpRequest.POST("", agendaItemCreateDTO).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadAgendaItemByMember() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityForPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadAgendaItemByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadAgendaItemNotFound() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();

        UUID randomCheckinID = UUID.randomUUID();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", randomCheckinID)).basicAuth(memberProfileEntityOfPDL.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Invalid agenda item id %s", randomCheckinID), error);

    }

    @Test
    void testReadAgendaItemNotFoundByUnrelatedUser() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();


        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);


    }

    @Test
    void testFindAllAgendaItemByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET("/")
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllAgendaItemByNonAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

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
    void testFindAgendaItemByBothCheckinIdAndCreateByid() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s", agendaItem.getCheckinid(), agendaItem.getCreatedbyid()))
                .basicAuth(memberProfileEntity.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }


    @Test
    void testFindAgendaItemByMemberIdForAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityForUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", agendaItem.getCreatedbyid()))
                .basicAuth(memberProfileEntityForUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAgendaItemByCheckinIdForAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityForUnrelatedUser = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", agendaItem.getCheckinid()))
                .basicAuth(memberProfileEntityForUnrelatedUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAgendaItemByCheckinIdForPDL() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", agendaItem.getCheckinid()))
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAgendaItemByCheckinIdForUnrelatedUser() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntity1 = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s", agendaItem.getCheckinid()))
                .basicAuth(memberProfileEntity1.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }

    @Test
    void testFindAgendaItemByCreatedByIdByMember() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntityForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", agendaItem.getCreatedbyid()))
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<Set<AgendaItem>> response = client.toBlocking().exchange(request, Argument.setOf(AgendaItem.class));

        assertEquals(Set.of(agendaItem), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAgendaItemByCreatedByIdByUnrelatedUser() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntity1 = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntityForPDL);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s", agendaItem.getCreatedbyid()))
                .basicAuth(memberProfileEntity1.getWorkEmail(), PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();

        assertEquals("User is unauthorized to do this operation", error);

    }


    @Test
    void testUpdateAgendaItemByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.PUT("", agendaItem).basicAuth(memberProfileEntityForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(agendaItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateAgendaItemByPDL() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.PUT("", agendaItem).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(agendaItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }


    @Test
    void testUpdateAgendaItemByMember() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.PUT("", agendaItem).basicAuth(memberProfileEntityForPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(agendaItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateInvalidAgendaItem() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);
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
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [agendaItem] not specified", errors.asText());
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
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);
        agendaItem.setId(UUID.randomUUID());

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);
        agendaItem.setCheckinid(UUID.randomUUID());

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);
        agendaItem.setCreatedbyid(UUID.randomUUID());

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
                .basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
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
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createACompletedCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
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
    void testUpdateAgendaItemForUnrelatedUserByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);
        MemberProfileEntity memberProfileEntityOfMrNobody = createAnUnrelatedUser();

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<AgendaItem> request = HttpRequest.PUT("", agendaItem)
                .basicAuth(memberProfileEntityOfMrNobody.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<AgendaItem> response = client.toBlocking().exchange(request, AgendaItem.class);

        assertEquals(agendaItem, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteAgendaItemByAdmin() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityForPDL.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteAgendaItemByPdl() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityForPDL.getWorkEmail(), PDL_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteAgendaItemByMember() {
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityForPDL = createADefaultMemberProfileForPdl(memberProfileEntity);

        CheckIn checkIn = createADefaultCheckIn(memberProfileEntity, memberProfileEntityForPDL);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityForPDL.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteAAgendaItemByADMINIdWhenCompleted() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityOfUser.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteAAgendaItemByPDLIdWhenCompleted() {
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityOfUser.getWorkEmail(),PDL_ROLE);
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
        MemberProfileEntity memberProfileEntityOfPDL = createADefaultMemberProfile();
        MemberProfileEntity memberProfileEntityOfUser = createADefaultMemberProfileForPdl(memberProfileEntityOfPDL);

        CheckIn checkIn = createACompletedCheckIn(memberProfileEntityOfPDL, memberProfileEntityOfUser);

        AgendaItem agendaItem = createADeafultAgendaItem(checkIn, memberProfileEntityOfPDL);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", agendaItem.getId())).basicAuth(memberProfileEntityOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("User is unauthorized to do this operation", error);

    }

}

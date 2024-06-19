package com.objectcomputing.checkins.services.team.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.fixture.TeamMemberFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static org.junit.jupiter.api.Assertions.*;

class TeamMemberControllerTest extends TestContainersSuite implements TeamFixture, MemberProfileFixture, RoleFixture, TeamMemberFixture {

    @Inject
    @Client("/services/teams/members")
    HttpClient client;

    @Test
    void testCreateATeamMemberByAdmin() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO(team.getId(), memberProfile.getId(), false);
        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth("test@test.com", ADMIN_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        TeamMember teamMember = response.body();

        assertEquals(teamMemberCreateDTO.getMemberId(), teamMember.getMemberId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateATeamMemberByTeamLead() {
        Team team = createDefaultTeam();

        // Create a team lead and add him to the team
        MemberProfile memberProfileOfTeamLead = createADefaultMemberProfile();
        createLeadTeamMember(team, memberProfileOfTeamLead);

        // Create a member and add him to team
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO(team.getId(), memberProfileOfUser.getId(), false);
        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(memberProfileOfTeamLead.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        TeamMember teamMember = response.body();

        assertEquals(teamMemberCreateDTO.getMemberId(), teamMember.getMemberId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateATeamMemberThrowsExceptionForNotAdminAndNotTeamLead() {
        Team team = createDefaultTeam();

        // Create a user (not team lead) and add him to the team
        MemberProfile memberProfileOfTeammate = createADefaultMemberProfile();
        createLeadTeamMember(team, memberProfileOfTeammate);

        // Create a member and add him to team
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO(team.getId(), memberProfileOfUser.getId(), false);
        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(NOT_AUTHORIZED_MSG, error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateAnInvalidTeamMember() {
        TeamMemberCreateDTO dto = new TeamMemberCreateDTO(null, null, null);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("teamMember.memberId: must not be null", errorList.get(0));
        assertEquals("teamMember.teamId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateANullTeamMember() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [teamMember] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateATeamMemberWithNonExistingTeam() {

        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberResponseDTO = new TeamMemberCreateDTO(UUID.randomUUID(), memberProfile.getId(), false);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberResponseDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Team %s doesn't exist", teamMemberResponseDTO.getTeamId()), error);
    }

    @Test
    void testCreateATeamMemberWithNonExistingMember() {

        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMemberCreateDTO requestDTO = new TeamMemberCreateDTO(team.getId(), UUID.randomUUID(), false);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", requestDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist", requestDTO.getMemberId()), error);
    }

    @Test
    void testCreateATeamMemberWithExistingMemberAndTeam() {

        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        TeamMemberCreateDTO teamMemberResponseDTO = new TeamMemberCreateDTO(teamMember.getTeamId(), memberProfile.getId(), false);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberResponseDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s already exists in team %s", teamMemberResponseDTO.getMemberId(), teamMemberResponseDTO.getTeamId()), error);
    }

    @Test
    void testReadTeamMember() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", teamMember.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(teamMember, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadTeamMemberNotFound() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, TeamMember.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindAllTeamMembers() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByTeamId() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s", teamMember.getTeamId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByMemberId() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberId=%s", teamMember.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindTeamMembers() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamId=%s&memberId=%s", teamMember.getTeamId(),
                teamMember.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindTeamMembersAllParams() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamId=%s&memberId=%s&lead=%s", teamMember.getTeamId(),
                teamMember.getMemberId(), teamMember.isLead())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdateTeamMemberByAdmin() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        TeamMemberUpdateDTO teamMemberUpdateDTO = new TeamMemberUpdateDTO(teamMember.getId(), teamMember.getTeamId(), teamMember.getMemberId(), true);
        final MutableHttpRequest<TeamMemberUpdateDTO> request = HttpRequest.PUT("", teamMemberUpdateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        TeamMember result = response.body();
        assertNotNull(result);
        assertEquals(teamMember.getMemberId(), result.getMemberId());
        assertTrue(result.isLead());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateTeamMemberByTeamLead() {
        Team team = createDefaultTeam();

        // Create a team lead and add him to the team
        MemberProfile memberProfileOfTeamLead = createADefaultMemberProfile();
        createLeadTeamMember(team, memberProfileOfTeamLead);

        // Create a member and add him to team
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        TeamMember teamMember = createDefaultTeamMember(team, memberProfileOfUser);

        // Update member
        TeamMemberUpdateDTO teamMemberUpdateDTO = new TeamMemberUpdateDTO(teamMember.getId(), teamMember.getTeamId(), teamMember.getMemberId(), true);
        final MutableHttpRequest<TeamMemberUpdateDTO> request = HttpRequest.PUT("", teamMemberUpdateDTO).basicAuth(memberProfileOfTeamLead.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        TeamMember result = response.body();
        assertNotNull(result);
        assertEquals(teamMember.getMemberId(), result.getMemberId());
        assertTrue(result.isLead());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateTeamMemberThrowsExceptionForNotAdminAndNotTeamLead() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(NOT_AUTHORIZED_MSG, error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateAnInvalidTeamMember() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setMemberId(null);
        teamMember.setTeamId(null);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("teamMember.memberId: must not be null", errorList.get(0));
        assertEquals("teamMember.teamId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullTeamMember() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [teamMember] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }


    @Test
    void testUpdateTeamMemberThrowException() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setMemberId(UUID.randomUUID());
        teamMember.setTeamId(teamMember.getTeamId());

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", teamMember.getMemberId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateTeamMemberThrowExceptionWithNoTeam() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setMemberId(teamMember.getMemberId());
        teamMember.setTeamId(UUID.randomUUID());

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Team %s doesn't exist", teamMember.getTeamId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateTeamMemberThrowExceptionWithInvalidId() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setId(UUID.randomUUID());
        teamMember.setMemberId(teamMember.getMemberId());
        teamMember.setTeamId(teamMember.getTeamId());

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate teamMember to update with id %s", teamMember.getId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testDeleteTeamMemberAsAdmin() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", teamMember.getId())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteTeamMemberWithoutAdminPrivilege() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", teamMember.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testDeleteTeamMemberWithTeamLead() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile leadMemberProfile = createAnUnrelatedUser();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        TeamMember teamLead = createLeadTeamMember(team, leadMemberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", teamMember.getId())).basicAuth(leadMemberProfile.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteInvalidTeamMemberAsAdmin() {
        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setId(UUID.randomUUID());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", teamMember.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testMemberHistoryTableIsCreatedWhenTeamMemberIsAdded() {

        Team team = createDefaultTeam();

        long numHistoryRows = getMemberHistoryRepository().count();

        // Create a team lead and add him to the team
        MemberProfile memberProfileOfTeamLead = createADefaultMemberProfile();
        createLeadTeamMember(team, memberProfileOfTeamLead);

        // Create a member and add him to team
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO(team.getId(), memberProfileOfUser.getId(), false);
        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(memberProfileOfTeamLead.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        TeamMember teamMember = response.body();


        assertEquals(numHistoryRows + 1, getMemberHistoryRepository().count());

        final List<MemberHistory> actualEntries = getMemberHistoryRepository().findByTeamIdAndMemberId(team.getId(), memberProfileOfUser.getId());
        actualEntries.sort(Comparator.comparing(MemberHistory::getDate));
        MemberHistory last = actualEntries.get(actualEntries.size() - 1);

        assertEquals("Added", last.getChange());

        assertEquals(teamMemberCreateDTO.getMemberId(), teamMember.getMemberId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testMemberHistoryTableIsCreatedWhenTeamMemberIsUpdated() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        long numHistoryRows = getMemberHistoryRepository().count();

        TeamMemberUpdateDTO teamMemberUpdateDTO = new TeamMemberUpdateDTO(teamMember.getId(), teamMember.getTeamId(), teamMember.getMemberId(), true);
        final MutableHttpRequest<TeamMemberUpdateDTO> request = HttpRequest.PUT("", teamMemberUpdateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(numHistoryRows + 1, getMemberHistoryRepository().count());

        final List<MemberHistory> actualEntries = getMemberHistoryRepository().findByTeamIdAndMemberId(team.getId(), memberProfile.getId());
        actualEntries.sort(Comparator.comparing(MemberHistory::getDate));
        MemberHistory last = actualEntries.get(actualEntries.size() - 1);

        assertEquals("Updated", last.getChange());

        TeamMember result = response.body();
        assertNotNull(result);
        assertEquals(teamMember.getMemberId(), result.getMemberId());
        assertTrue(result.isLead());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testMemberHistoryTableIsCreatedWhenTeamMemberIsRemoved() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        Team team = createDefaultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        long numHistoryRows = getMemberHistoryRepository().count();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", teamMember.getId())).basicAuth(user.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(numHistoryRows + 1, getMemberHistoryRepository().count());

        final List<MemberHistory> actualEntries = getMemberHistoryRepository().findByTeamIdAndMemberId(team.getId(), memberProfile.getId());
        actualEntries.sort(Comparator.comparing(MemberHistory::getDate));
        MemberHistory last = actualEntries.get(actualEntries.size() - 1);

        assertEquals("Deleted", last.getChange());
    }

}

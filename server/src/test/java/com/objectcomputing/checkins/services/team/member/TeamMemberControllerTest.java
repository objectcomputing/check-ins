package com.objectcomputing.checkins.services.team.member;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
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

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamMemberControllerTest extends TestContainersSuite implements TeamFixture, MemberProfileFixture, TeamMemberFixture {

    @Inject
    @Client("/services/team/member")
    HttpClient client;

    @Test
    void testCreateATeamMember() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(team.getId());
        teamMemberCreateDTO.setMemberid(memberProfile.getId());
        teamMemberCreateDTO.setLead(false);
        teamMemberCreateDTO.setSupervisorid(memberProfile.getPdlId());

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        TeamMember teamMember = response.body();

        assertEquals(teamMemberCreateDTO.getMemberid(), teamMember.getMemberid());
        assertEquals(teamMemberCreateDTO.getSupervisorid(), teamMember.getSupervisorid());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAnInvalidTeamMember() {
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("teamMember.memberid: must not be null", errorList.get(0));
        assertEquals("teamMember.teamid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullTeamMember() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [teamMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateATeamMemberWithNonExistingTeam() {

        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.randomUUID());
        teamMemberCreateDTO.setMemberid(memberProfile.getId());
        teamMemberCreateDTO.setLead(false);
        teamMemberCreateDTO.setSupervisorid(memberProfile.getPdlId());

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Team %s doesn't exist",teamMemberCreateDTO.getTeamid()),error);
    }

    @Test
    void testCreateATeamMemberWithNonExistingMember() {

        Team team = createDeafultTeam();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(team.getId());
        teamMemberCreateDTO.setMemberid(UUID.randomUUID());
        teamMemberCreateDTO.setLead(false);
        teamMemberCreateDTO.setSupervisorid(UUID.randomUUID());

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist",teamMemberCreateDTO.getMemberid()),error);
    }

    @Test
    void testCreateATeamMemberWithExistingMemberAndTeam() {

        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();
        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(teamMember.getTeamid());
        teamMemberCreateDTO.setMemberid(teamMember.getMemberid());
        teamMemberCreateDTO.setLead(false);
        teamMemberCreateDTO.setSupervisorid(teamMember.getSupervisorid());

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO)
                                                         .basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s already exists in team %s",teamMemberCreateDTO.getMemberid(),teamMemberCreateDTO.getTeamid()),error);
    }

    @Test
    void testCreateInvalidSupervisorId() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();
        UUID supervisorId = UUID.randomUUID();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(team.getId());
        teamMemberCreateDTO.setMemberid(memberProfile.getId());
        teamMemberCreateDTO.setLead(false);
        teamMemberCreateDTO.setSupervisorid(supervisorId);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO)
                .basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Supervisor %s doesn't exist", supervisorId), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testLoadTeamMembers() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(team.getId());
        teamMemberCreateDTO.setMemberid(memberProfile.getId());
        teamMemberCreateDTO.setLead(true);
        teamMemberCreateDTO.setSupervisorid(memberProfile.getPdlId());

        MemberProfile memberProfile1 = createADefaultMemberProfileForPdl(memberProfile);
        Team team1 = createAnotherDeafultTeam();
        TeamMemberCreateDTO teamMemberCreateDTO2 = new TeamMemberCreateDTO();
        teamMemberCreateDTO2.setTeamid(team1.getId());
        teamMemberCreateDTO2.setMemberid(memberProfile1.getId());
        teamMemberCreateDTO2.setLead(true);
        teamMemberCreateDTO2.setSupervisorid(memberProfile1.getPdlId());

        List<TeamMemberCreateDTO> dtoList = List.of(teamMemberCreateDTO, teamMemberCreateDTO2);

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<List<TeamMember>> response = client.toBlocking().exchange(request, Argument.listOf(TeamMember.class));
        List<TeamMember> teamMember = response.body();

        assertEquals(teamMember.get(0).getMemberid(), teamMemberCreateDTO.getMemberid());
        assertEquals(teamMember.get(0).getSupervisorid(), teamMemberCreateDTO.getSupervisorid());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));
    }

    @Test
    void testLoadTeamMembersInvalidTeamMember() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(team.getId());
        teamMemberCreateDTO.setMemberid(memberProfile.getId());
        teamMemberCreateDTO.setLead(true);
        teamMemberCreateDTO.setSupervisorid(memberProfile.getPdlId());

        TeamMemberCreateDTO teamMemberCreateDTO2 = new TeamMemberCreateDTO();

        List<TeamMemberCreateDTO> dtoList = List.of(teamMemberCreateDTO, teamMemberCreateDTO2);

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("teamMembers.memberid: must not be null", errorList.get(0));
        assertEquals("teamMembers.teamid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testLoadTeamMembersThrowException() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(team.getId());
        teamMemberCreateDTO.setMemberid(memberProfile.getId());
        teamMemberCreateDTO.setLead(true);
        teamMemberCreateDTO.setSupervisorid(memberProfile.getPdlId());

        TeamMemberCreateDTO teamMemberCreateDTO2 = new TeamMemberCreateDTO();
        teamMemberCreateDTO2.setTeamid(team.getId());
        teamMemberCreateDTO2.setMemberid(memberProfile.getId());
        teamMemberCreateDTO2.setLead(true);
        teamMemberCreateDTO2.setSupervisorid(memberProfile.getPdlId());

        List<TeamMemberCreateDTO> dtoList = List.of(teamMemberCreateDTO, teamMemberCreateDTO2);

        final String errorMessage = String.format("Member %s already exists in team %s",teamMemberCreateDTO2.getMemberid(),teamMemberCreateDTO2.getTeamid());

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Member %s was not added to Team %s because: %s\"]",
                teamMemberCreateDTO2.getMemberid(), teamMemberCreateDTO2.getTeamid(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));
    }

    @Test
    void testReadTeamMember() {
        Team team = createDeafultTeam();
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
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByTeamId() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s", teamMember.getTeamid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByMemberId() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", teamMember.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindBySupervisorId() {
        Team team = createDeafultTeam();
        MemberProfile memberProfileOfPdl = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPdl);

        TeamMember teamMember = createDefaultTeamMember(team, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?supervisorid=%s", teamMember.getSupervisorid()))
                                       .basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindTeamMembers() {
        Team team = createDeafultTeam();
        MemberProfile memberProfileOfPdl = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPdl);
        TeamMember teamMember = createDefaultTeamMember(team, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s&memberid=%s&supervisorid=%s", teamMember.getTeamid(),
                teamMember.getMemberid(), teamMember.getSupervisorid())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindTeamMembersAllParams() {
        Team team = createDeafultTeam();
        MemberProfile memberProfileOfPdl = createADefaultMemberProfile();
        MemberProfile memberProfileOfUser = createADefaultMemberProfileForPdl(memberProfileOfPdl);
        TeamMember teamMember = createDefaultTeamMember(team, memberProfileOfUser);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s&memberid=%s&lead=%s&supervisorid=%s", teamMember.getTeamid(),
                teamMember.getMemberid(), teamMember.isLead(), teamMember.getSupervisorid())).basicAuth(memberProfileOfUser.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdateTeamMember() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(teamMember, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateAnInvalidTeamMember() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setMemberid(null);
        teamMember.setTeamid(null);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("teamMember.memberid: must not be null", errorList.get(0));
        assertEquals("teamMember.teamid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullTeamMember() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [teamMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }


    @Test
    void testUpdateTeamMemberThrowException() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setMemberid(UUID.randomUUID());
        teamMember.setTeamid(teamMember.getTeamid());

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist",teamMember.getMemberid()),error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateTeamMemberThrowExceptionWithNoTeam() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setMemberid(teamMember.getMemberid());
        teamMember.setTeamid(UUID.randomUUID());

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Team %s doesn't exist",teamMember.getTeamid()),error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateTeamMemberThrowExceptionWithInvalidId() {
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setId(UUID.randomUUID());
        teamMember.setMemberid(teamMember.getMemberid());
        teamMember.setTeamid(teamMember.getTeamid());

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate teamMember to update with id %s",teamMember.getId()),error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateInvalidSupervisorId() {
        UUID testSupervisorId = UUID.randomUUID();
        Team team = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();
        TeamMember teamMember = createDefaultTeamMember(team, memberProfile);
        teamMember.setSupervisorid(testSupervisorId);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", teamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Supervisor %s doesn't exist", testSupervisorId), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }
}

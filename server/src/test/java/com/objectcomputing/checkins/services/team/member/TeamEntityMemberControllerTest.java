package com.objectcomputing.checkins.services.team.member;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.fixture.TeamMemberFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import nu.studer.sample.tables.pojos.Team;
import nu.studer.sample.tables.pojos.TeamMember;
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
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.fromString(teamEntity.getId()));
        teamMemberCreateDTO.setMemberid(memberProfileEntity.getId());
        teamMemberCreateDTO.setLead(false);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        TeamMember teamMemberEntity = response.body();

        assertEquals(teamMemberCreateDTO.getMemberid(), teamMemberEntity.getMemberid());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMemberEntity.getId()), response.getHeaders().get("location"));

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

        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.randomUUID());
        teamMemberCreateDTO.setMemberid(memberProfileEntity.getId());
        teamMemberCreateDTO.setLead(false);

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

        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.fromString(teamEntity.getId()));
        teamMemberCreateDTO.setMemberid(UUID.randomUUID());
        teamMemberCreateDTO.setLead(false);

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

        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.fromString(teamMemberEntity.getTeamId()));
        teamMemberCreateDTO.setMemberid(UUID.fromString(teamMemberEntity.getMemberid()));
        teamMemberCreateDTO.setLead(false);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s already exists in team %s",teamMemberCreateDTO.getMemberid(),teamMemberCreateDTO.getTeamid()),error);
    }


    @Test
    void testLoadTeamMembers() {
        Team team = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.fromString(team.getId()));
        teamMemberCreateDTO.setMemberid(memberProfileEntity.getId());
        teamMemberCreateDTO.setLead(true);

        MemberProfileEntity memberProfileEntity1 = createADefaultMemberProfileForPdl(memberProfileEntity);
        Team teamEntity = createAnotherDeafultTeam();
        TeamMemberCreateDTO teamMemberCreateDTO2 = new TeamMemberCreateDTO();
        teamMemberCreateDTO2.setTeamid(UUID.fromString(teamEntity.getId()));
        teamMemberCreateDTO2.setMemberid(memberProfileEntity1.getId());
        teamMemberCreateDTO2.setLead(true);

        List<TeamMemberCreateDTO> dtoList = List.of(teamMemberCreateDTO, teamMemberCreateDTO2);

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<List<TeamMember>> response = client.toBlocking().exchange(request, Argument.listOf(TeamMember.class));
        List<TeamMember> teamMemberEntity = response.body();

        assertEquals(teamMemberEntity.get(0).getMemberid(), teamMemberCreateDTO.getMemberid());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));
    }

    @Test
    void testLoadTeamMembersInvalidTeamMember() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.fromString(teamEntity.getId()));
        teamMemberCreateDTO.setMemberid(memberProfileEntity.getId());
        teamMemberCreateDTO.setLead(true);

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
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.fromString(teamEntity.getId()));
        teamMemberCreateDTO.setMemberid(memberProfileEntity.getId());
        teamMemberCreateDTO.setLead(true);

        TeamMemberCreateDTO teamMemberCreateDTO2 = new TeamMemberCreateDTO();
        teamMemberCreateDTO2.setTeamid(UUID.fromString(teamEntity.getId()));
        teamMemberCreateDTO2.setMemberid(memberProfileEntity.getId());
        teamMemberCreateDTO2.setLead(true);

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
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", teamMemberEntity.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(teamMemberEntity, response.body());
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
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMemberEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByTeamId() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s", teamMemberEntity.getTeamId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMemberEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByMemberId() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", teamMemberEntity.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMemberEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindTeamMembers() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s&memberid=%s", teamMemberEntity.getTeamId(),
                teamMemberEntity.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMemberEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindTeamMembersAllParams() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s&memberid=%s&lead=%s", teamMemberEntity.getTeamId(),
                teamMemberEntity.getMemberid(), teamMemberEntity.getLead())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(Set.of(teamMemberEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdateTeamMember() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", teamMemberEntity).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(teamMemberEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamMemberEntity.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateAnInvalidTeamMember() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("",
                new TeamMember(teamMemberEntity.getId(), null, null, teamMemberEntity.getLead()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("",
                new TeamMember(teamMemberEntity.getId(), memberProfileEntity.getId().toString(), teamMemberEntity.getTeamId(), teamMemberEntity.getLead()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", teamMemberEntity.getMemberid()),error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateTeamMemberThrowExceptionWithNoTeam() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("",
                new TeamMember(teamMemberEntity.getId(), teamMemberEntity.getMemberid(), teamEntity.getId(), teamMemberEntity.getLead()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Team %s doesn't exist", teamMemberEntity.getTeamId()),error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateTeamMemberThrowExceptionWithInvalidId() {
        Team teamEntity = createDeafultTeam();
        MemberProfileEntity memberProfileEntity = createADefaultMemberProfile();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfileEntity);
        UUID requestId = UUID.randomUUID();

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("",
                new TeamMember(requestId.toString(), teamMemberEntity.getMemberid(), teamMemberEntity.getTeamId(), teamMemberEntity.getLead()))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate teamMember to update with id %s", requestId),error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

}

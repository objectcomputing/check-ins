package com.objectcomputing.checkins.services.team.member;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.team.TeamBadArgException;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;

@MicronautTest
class TeamMemberControllerTest {

    @Inject
    @Client("/services/team/member")
    HttpClient client;
    @Inject
    private TeamMemberServices teamMemberServices;

    @MockBean(TeamMemberServices.class)
    public TeamMemberServices teamMemberServices() {
        return mock(TeamMemberServices.class);
    }

    @Test
    void testCreateATeamMember() {
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.randomUUID());
        teamMemberCreateDTO.setMemberid(UUID.randomUUID());
        teamMemberCreateDTO.setLead(true);

        TeamMember tm = new TeamMember(teamMemberCreateDTO.getTeamid(), teamMemberCreateDTO.getMemberid(), teamMemberCreateDTO.isLead());

        when(teamMemberServices.save(eq(tm))).thenReturn(tm);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(tm, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), tm.getId()), response.getHeaders().get("location"));

        verify(teamMemberServices, times(1)).save(any(TeamMember.class));
    }

    @Test
    void testCreateAnInvalidTeamMember() {
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();

        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), true);
        when(teamMemberServices.save(any(TeamMember.class))).thenReturn(tm);

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

        verify(teamMemberServices, never()).save(any(TeamMember.class));
    }

    @Test
    void testCreateANullTeamMember() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), true);
        when(teamMemberServices.save(any(TeamMember.class))).thenReturn(tm);

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [teamMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamMemberServices, never()).save(any(TeamMember.class));
    }

    @Test
    void testLoadTeamMembers() {
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.randomUUID());
        teamMemberCreateDTO.setMemberid(UUID.randomUUID());
        teamMemberCreateDTO.setLead(true);

        TeamMemberCreateDTO teamMemberCreateDTO2 = new TeamMemberCreateDTO();
        teamMemberCreateDTO2.setTeamid(UUID.randomUUID());
        teamMemberCreateDTO2.setMemberid(UUID.randomUUID());
        teamMemberCreateDTO2.setLead(true);

        List<TeamMemberCreateDTO> dtoList = List.of(teamMemberCreateDTO, teamMemberCreateDTO2);

        TeamMember tm = new TeamMember(teamMemberCreateDTO.getTeamid(), teamMemberCreateDTO.getMemberid(), teamMemberCreateDTO.isLead());
        TeamMember tm2 = new TeamMember(teamMemberCreateDTO2.getTeamid(), teamMemberCreateDTO2.getMemberid(), teamMemberCreateDTO2.isLead());

        List<TeamMember> teamList = List.of(tm, tm2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(a -> {
            TeamMember thisG = teamList.get(i.getAndAdd(1));
            assertEquals(thisG, a.getArgument(0));
            return thisG;
        }).when(teamMemberServices).save(any(TeamMember.class));

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<List<TeamMember>> response = client.toBlocking().exchange(request, Argument.listOf(TeamMember.class));

        assertEquals(teamList, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

        verify(teamMemberServices, times(2)).save(any(TeamMember.class));
    }

    @Test
    void testLoadTeamMembersInvalidTeamMember() {
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.randomUUID());
        teamMemberCreateDTO.setMemberid(UUID.randomUUID());
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

        verify(teamMemberServices, never()).save(any(TeamMember.class));
    }

    @Test
    void testLoadTeamMembersThrowException() {
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();
        teamMemberCreateDTO.setTeamid(UUID.randomUUID());
        teamMemberCreateDTO.setMemberid(UUID.randomUUID());
        teamMemberCreateDTO.setLead(true);

        TeamMemberCreateDTO teamMemberCreateDTO2 = new TeamMemberCreateDTO();
        teamMemberCreateDTO2.setTeamid(UUID.randomUUID());
        teamMemberCreateDTO2.setMemberid(UUID.randomUUID());
        teamMemberCreateDTO2.setLead(true);

        List<TeamMemberCreateDTO> dtoList = List.of(teamMemberCreateDTO, teamMemberCreateDTO2);

        TeamMember tm = new TeamMember(teamMemberCreateDTO.getTeamid(), teamMemberCreateDTO.getMemberid(), teamMemberCreateDTO.isLead());
        TeamMember tm2 = new TeamMember(teamMemberCreateDTO2.getTeamid(), teamMemberCreateDTO2.getMemberid(), teamMemberCreateDTO2.isLead());

        final String errorMessage = "error message!";
        when(teamMemberServices.save(eq(tm))).thenReturn(tm);

        when(teamMemberServices.save(eq(tm2))).thenAnswer(a -> {
            throw new TeamBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Member %s was not added to Team %s because: %s\"]",
                tm2.getMemberid(), tm2.getTeamid(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(teamMemberServices, times(2)).save(any(TeamMember.class));
    }

    @Test
    void testReadTeamMember() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamMemberServices.read(eq(tm.getId()))).thenReturn(tm);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", tm.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(tm, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadTeamMemberNotFound() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamMemberServices.read(eq(tm.getTeamid()))).thenReturn(tm);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", tm.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);;
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, TeamMember.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(teamMemberServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindAllTeamMembers() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<TeamMember> teams = Collections.singleton(tm);

        when(teamMemberServices.findByFields(null, null, null)).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).findByFields(null, null, null);
    }

    @Test
    void testFindByTeamId() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<TeamMember> teams = Collections.singleton(tm);

        when(teamMemberServices.findByFields(tm.getTeamid(), null, null)).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s", tm.getTeamid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).findByFields(tm.getTeamid(), null, null);
    }

    @Test
    void testFindByMemberId() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<TeamMember> teams = Collections.singleton(tm);

        when(teamMemberServices.findByFields(null, tm.getMemberid(), null)).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", tm.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).findByFields(null, tm.getMemberid(), null);
    }

    @Test
    void testFindTeamMembers() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<TeamMember> teams = Collections.singleton(tm);

        when(teamMemberServices.findByFields(eq(tm.getTeamid()), eq(tm.getMemberid()), eq(null))).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s&memberid=%s", tm.getTeamid(),
                tm.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).findByFields(any(UUID.class), any(UUID.class), eq(null));
    }

    @Test
    void testFindTeamMembersAllParams() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<TeamMember> teams = Collections.singleton(tm);

        when(teamMemberServices.findByFields(eq(tm.getTeamid()), eq(tm.getMemberid()), eq(tm.isLead()))).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s&memberid=%s&lead=%s", tm.getTeamid(),
                tm.getMemberid(), tm.isLead())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).findByFields(any(UUID.class), any(UUID.class), anyBoolean());
    }


    @Test
    void testFindTeamMembersNull() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamMemberServices.findByFields(eq(tm.getTeamid()), eq(null), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s", tm.getTeamid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(TeamMember.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(teamMemberServices, times(1)).findByFields(any(UUID.class), eq(null), eq(null));
    }


    @Test
    void testUpdateTeamMember() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamMemberServices.update(eq(tm))).thenReturn(tm);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", tm).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(tm, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), tm.getId()), response.getHeaders().get("location"));

        verify(teamMemberServices, times(1)).update(any(TeamMember.class));
    }

    @Test
    void testUpdateAnInvalidTeamMember() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), null, null, true);

        when(teamMemberServices.update(any(TeamMember.class))).thenReturn(tm);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", tm).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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

        verify(teamMemberServices, never()).update(any(TeamMember.class));
    }

    @Test
    void testUpdateANullTeamMember() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        when(teamMemberServices.update(any(TeamMember.class))).thenReturn(tm);

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [teamMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamMemberServices, never()).update(any(TeamMember.class));
    }


    @Test
    void testUpdateTeamMemberThrowException() {
        TeamMember tm = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        final String errorMessage = "error message!";

        when(teamMemberServices.update(any(TeamMember.class))).thenAnswer(a -> {
            throw new TeamBadArgException(errorMessage);
        });

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("", tm).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamMemberServices, times(1)).update(any(TeamMember.class));
    }

}

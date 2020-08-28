package com.objectcomputing.checkins.services.team;


import com.fasterxml.jackson.databind.JsonNode;
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
class TeamControllerTest {

    @Inject
    @Client("/services/team")
    HttpClient client;
    @Inject
    private TeamServices teamService;

    @MockBean(TeamServices.class)
    public TeamServices teamService() {
        return mock(TeamServices.class);
    }

    @Test
    void testCreateATeam() {
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();
        teamCreateDTO.setName("name");
        teamCreateDTO.setDescription("description");

        Team tm = new Team(teamCreateDTO.getName(), teamCreateDTO.getDescription());

        when(teamService.save(eq(tm))).thenReturn(tm);

        final HttpRequest<TeamCreateDTO> request = HttpRequest.POST("", teamCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Team> response = client.toBlocking().exchange(request, Team.class);

        assertEquals(tm, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), tm.getId()), response.getHeaders().get("location"));

        verify(teamService, times(1)).save(any(Team.class));
    }

    @Test
    void testCreateAnInvalidTeam() {
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();

        Team tm = new Team(UUID.randomUUID(), "DNC", "DNC");
        when(teamService.save(any(Team.class))).thenReturn(tm);

        final HttpRequest<TeamCreateDTO> request = HttpRequest.POST("", teamCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("team.description: must not be blank", errorList.get(0));
        assertEquals("team.name: must not be blank", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamService, never()).save(any(Team.class));
    }

    @Test
    void testCreateANullTeam() {
        Team tm = new Team(UUID.randomUUID(), "DNC", "DNC");
        when(teamService.save(any(Team.class))).thenReturn(tm);

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [team] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamService, never()).save(any(Team.class));
    }

    @Test
    void testLoadTeams() {
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();
        teamCreateDTO.setName("name");
        teamCreateDTO.setDescription("description");

        TeamCreateDTO teamCreateDTO2 = new TeamCreateDTO();
        teamCreateDTO2.setName("name2");
        teamCreateDTO2.setDescription("description3");

        List<TeamCreateDTO> dtoList = List.of(teamCreateDTO, teamCreateDTO2);

        Team tm = new Team(teamCreateDTO.getName(), teamCreateDTO.getDescription());
        Team tm2 = new Team(teamCreateDTO2.getName(), teamCreateDTO2.getDescription());

        List<Team> teamList = List.of(tm, tm2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(a -> {
            Team thisG = teamList.get(i.getAndAdd(1));
            assertEquals(thisG, a.getArgument(0));
            return thisG;
        }).when(teamService).save(any(Team.class));

        final MutableHttpRequest<List<TeamCreateDTO>> request = HttpRequest.POST("teams", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<List<Team>> response = client.toBlocking().exchange(request, Argument.listOf(Team.class));

        assertEquals(teamList, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

        verify(teamService, times(2)).save(any(Team.class));
    }

    @Test
    void testLoadTeamsInvalidTeam() {
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();
        teamCreateDTO.setName("name2");
        teamCreateDTO.setDescription("description");

        TeamCreateDTO teamCreateDTO2 = new TeamCreateDTO();

        List<TeamCreateDTO> dtoList = List.of(teamCreateDTO, teamCreateDTO2);

        final MutableHttpRequest<List<TeamCreateDTO>> request = HttpRequest.POST("teams", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("teamsList.description: must not be blank", errorList.get(0));
        assertEquals("teamsList.name: must not be blank", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamService, never()).save(any(Team.class));
    }

    @Test
    void testLoadTeamsThrowException() {
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();
        teamCreateDTO.setName("name");
        teamCreateDTO.setDescription("description");

        TeamCreateDTO teamCreateDTO2 = new TeamCreateDTO();
        teamCreateDTO2.setName("name2");
        teamCreateDTO2.setDescription("description3");

        List<TeamCreateDTO> dtoList = List.of(teamCreateDTO, teamCreateDTO2);

        Team tm = new Team(teamCreateDTO.getName(), teamCreateDTO.getDescription());
        Team tm2 = new Team(teamCreateDTO2.getName(), teamCreateDTO2.getDescription());

        final String errorMessage = "error message!";
        when(teamService.save(eq(tm))).thenReturn(tm);

        when(teamService.save(eq(tm2))).thenAnswer(a -> {
            throw new TeamBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<TeamCreateDTO>> request = HttpRequest.POST("teams", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Team name2 was not added because: %s\"]", errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(teamService, times(2)).save(any(Team.class));
    }

    @Test
    void testReadTeam() {
        Team tm = new Team(UUID.randomUUID(), "Hello", "World");

        when(teamService.read(eq(tm.getId()))).thenReturn(tm);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", tm.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Team> response = client.toBlocking().exchange(request, Team.class);

        assertEquals(tm, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamService, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadTeamNotFound() {
        Team tm = new Team(UUID.randomUUID(), "Hello", "World");

        when(teamService.read(eq(tm.getId()))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", tm.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Team.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(teamService, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindAllTeams() {
        Team tm = new Team(UUID.randomUUID(), "Hello", "World");
        Set<Team> teams = Collections.singleton(tm);

        when(teamService.findByFields(null, null)).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Team>> response = client.toBlocking().exchange(request, Argument.setOf(Team.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamService, times(1)).findByFields(null, null);
    }



    @Test
    void testFindByName() {
        Team tm = new Team(UUID.randomUUID(), "Hello", "World");
        Set<Team> teams = Collections.singleton(tm);

        when(teamService.findByFields(tm.getName(), null)).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", tm.getName())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Team>> response = client.toBlocking().exchange(request, Argument.setOf(Team.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamService, times(1)).findByFields(tm.getName(), null);
    }

    @Test
    void testFindByMemberId() {
        Team tm = new Team(UUID.randomUUID(), "Hello", "World");
        Set<Team> teams = Collections.singleton(tm);
        UUID member = UUID.randomUUID();

        when(teamService.findByFields(null,member)).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", member.toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Team>> response = client.toBlocking().exchange(request, Argument.setOf(Team.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamService, times(1)).findByFields(null,member);
    }

    @Test
    void testFindTeams() {
        Team tm = new Team(UUID.randomUUID(), "Hello", "World");
        Set<Team> teams = Collections.singleton(tm);
        UUID member = UUID.randomUUID();

        when(teamService.findByFields(eq(tm.getName()), eq(member))).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s&memberid=%s", tm.getName(),
                member.toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Team>> response = client.toBlocking().exchange(request, Argument.setOf(Team.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamService, times(1)).findByFields(any(String.class), any(UUID.class));
    }

    @Test
    void testFindTeamsNull() {
        Team tm = new Team(UUID.randomUUID(), "Hello", "World");

        when(teamService.findByFields(eq(tm.getName()), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", tm.getName())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(Team.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(teamService, times(1)).findByFields(any(String.class), eq(null));
    }


    @Test
    void testUpdateTeam() {
        Team tm = new Team(UUID.randomUUID(), "name", "description");

        when(teamService.update(eq(tm))).thenReturn(tm);

        final HttpRequest<Team> request = HttpRequest.PUT("", tm).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Team> response = client.toBlocking().exchange(request, Team.class);

        assertEquals(tm, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), tm.getId()), response.getHeaders().get("location"));

        verify(teamService, times(1)).update(any(Team.class));
    }

    @Test
    void testUpdateAnInvalidTeam() {
        Team tm = new Team(UUID.randomUUID(), null, "");

        when(teamService.update(any(Team.class))).thenReturn(tm);

        final HttpRequest<Team> request = HttpRequest.PUT("", tm).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("team.description: must not be blank", errorList.get(0));
        assertEquals("team.name: must not be blank", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamService, never()).update(any(Team.class));
    }

    @Test
    void testUpdateANullTeam() {
        Team tm = new Team(UUID.randomUUID(), "DNC", "DNC");
        when(teamService.update(any(Team.class))).thenReturn(tm);

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [team] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamService, never()).update(any(Team.class));
    }


    @Test
    void testUpdateTeamThrowException() {
        Team tm = new Team(UUID.randomUUID(), "name", "description");

        final String errorMessage = "error message!";

        when(teamService.update(any(Team.class))).thenAnswer(a -> {
            throw new TeamBadArgException(errorMessage);
        });

        final MutableHttpRequest<Team> request = HttpRequest.PUT("", tm).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(teamService, times(1)).update(any(Team.class));
    }

}

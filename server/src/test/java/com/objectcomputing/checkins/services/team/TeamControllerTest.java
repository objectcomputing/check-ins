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

        Team g = new Team(teamCreateDTO.getName(), teamCreateDTO.getDescription());

        when(teamService.save(eq(g))).thenReturn(g);

        final HttpRequest<TeamCreateDTO> request = HttpRequest.POST("", teamCreateDTO);
        final HttpResponse<Team> response = client.toBlocking().exchange(request, Team.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getTeamid()), response.getHeaders().get("location"));

        verify(teamService, times(1)).save(any(Team.class));
    }

    @Test
    void testCreateAnInvalidTeam() {
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();

        Team g = new Team(UUID.randomUUID(), "DNC", "DNC");
        when(teamService.save(any(Team.class))).thenReturn(g);

        final HttpRequest<TeamCreateDTO> request = HttpRequest.POST("", teamCreateDTO);
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
        Team g = new Team(UUID.randomUUID(), "DNC", "DNC");
        when(teamService.save(any(Team.class))).thenReturn(g);

        final HttpRequest<String> request = HttpRequest.POST("", "");
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

        Team g = new Team(teamCreateDTO.getName(), teamCreateDTO.getDescription());
        Team g2 = new Team(teamCreateDTO2.getName(), teamCreateDTO2.getDescription());

        List<Team> teamList = List.of(g, g2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(a -> {
            Team thisG = teamList.get(i.getAndAdd(1));
            assertEquals(thisG, a.getArgument(0));
            return thisG;
        }).when(teamService).save(any(Team.class));

        final MutableHttpRequest<List<TeamCreateDTO>> request = HttpRequest.POST("teams", dtoList);
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

        final MutableHttpRequest<List<TeamCreateDTO>> request = HttpRequest.POST("teams", dtoList);
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

        Team g = new Team(teamCreateDTO.getName(), teamCreateDTO.getDescription());
        Team g2 = new Team(teamCreateDTO2.getName(), teamCreateDTO2.getDescription());

        final String errorMessage = "error message!";
        when(teamService.save(eq(g))).thenReturn(g);

        when(teamService.save(eq(g2))).thenAnswer(a -> {
            throw new TeamBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<TeamCreateDTO>> request = HttpRequest.POST("teams", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Team name2 was not added because: %s\"]", errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(teamService, times(2)).save(any(Team.class));
    }

    @Test
    void testReadTeam() {
        Team g = new Team(UUID.randomUUID(), "Hello", "World");

        when(teamService.read(eq(g.getTeamid()))).thenReturn(g);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", g.getTeamid().toString()));
        final HttpResponse<Team> response = client.toBlocking().exchange(request, Team.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamService, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadTeamNotFound() {
        Team g = new Team(UUID.randomUUID(), "Hello", "World");

        when(teamService.read(eq(g.getTeamid()))).thenReturn(null);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", g.getTeamid().toString()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Team.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(teamService, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindTeams() {
        Team g = new Team(UUID.randomUUID(), "Hello", "World");
        Set<Team> teams = Collections.singleton(g);
        UUID member = UUID.randomUUID();

        when(teamService.findByFields(eq(g.getName()), eq(member))).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s&memberid=%s", g.getName(),
                member.toString()));
        final HttpResponse<Set<Team>> response = client.toBlocking().exchange(request, Argument.setOf(Team.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamService, times(1)).findByFields(any(String.class), any(UUID.class));
    }

    @Test
    void testFindTeamsNull() {
        Team g = new Team(UUID.randomUUID(), "Hello", "World");

        when(teamService.findByFields(eq(g.getName()), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", g.getName()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(Team.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(teamService, times(1)).findByFields(any(String.class), eq(null));
    }


    @Test
    void testUpdateTeam() {
        Team g = new Team(UUID.randomUUID(), "name", "description");

        when(teamService.update(eq(g))).thenReturn(g);

        final HttpRequest<Team> request = HttpRequest.PUT("", g);
        final HttpResponse<Team> response = client.toBlocking().exchange(request, Team.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getTeamid()), response.getHeaders().get("location"));

        verify(teamService, times(1)).update(any(Team.class));
    }

    @Test
    void testUpdateAnInvalidTeam() {
        Team g = new Team(UUID.randomUUID(), null, "");

        when(teamService.update(any(Team.class))).thenReturn(g);

        final HttpRequest<Team> request = HttpRequest.PUT("", g);
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
        Team g = new Team(UUID.randomUUID(), "DNC", "DNC");
        when(teamService.update(any(Team.class))).thenReturn(g);

        final HttpRequest<String> request = HttpRequest.PUT("", "");
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
        Team g = new Team(UUID.randomUUID(), "name", "description");

        final String errorMessage = "error message!";

        when(teamService.update(any(Team.class))).thenAnswer(a -> {
            throw new TeamBadArgException(errorMessage);
        });

        final MutableHttpRequest<Team> request = HttpRequest.PUT("", g);
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

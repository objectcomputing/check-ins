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

        TeamMember g = new TeamMember(teamMemberCreateDTO.getTeamid(), teamMemberCreateDTO.getMemberid(), teamMemberCreateDTO.getLead());

        when(teamMemberServices.save(eq(g))).thenReturn(g);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getId()), response.getHeaders().get("location"));

        verify(teamMemberServices, times(1)).save(any(TeamMember.class));
    }

    @Test
    void testCreateAnInvalidTeamMember() {
        TeamMemberCreateDTO teamMemberCreateDTO = new TeamMemberCreateDTO();

        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), true);
        when(teamMemberServices.save(any(TeamMember.class))).thenReturn(g);

        final HttpRequest<TeamMemberCreateDTO> request = HttpRequest.POST("", teamMemberCreateDTO);
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
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), true);
        when(teamMemberServices.save(any(TeamMember.class))).thenReturn(g);

        final HttpRequest<String> request = HttpRequest.POST("", "");
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

        TeamMember g = new TeamMember(teamMemberCreateDTO.getTeamid(), teamMemberCreateDTO.getMemberid(), teamMemberCreateDTO.getLead());
        TeamMember g2 = new TeamMember(teamMemberCreateDTO2.getTeamid(), teamMemberCreateDTO2.getMemberid(), teamMemberCreateDTO2.getLead());

        List<TeamMember> teamList = List.of(g, g2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(a -> {
            TeamMember thisG = teamList.get(i.getAndAdd(1));
            assertEquals(thisG, a.getArgument(0));
            return thisG;
        }).when(teamMemberServices).save(any(TeamMember.class));

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList);
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

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList);
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

        TeamMember g = new TeamMember(teamMemberCreateDTO.getTeamid(), teamMemberCreateDTO.getMemberid(), teamMemberCreateDTO.getLead());
        TeamMember g2 = new TeamMember(teamMemberCreateDTO2.getTeamid(), teamMemberCreateDTO2.getMemberid(), teamMemberCreateDTO2.getLead());

        final String errorMessage = "error message!";
        when(teamMemberServices.save(eq(g))).thenReturn(g);

        when(teamMemberServices.save(eq(g2))).thenAnswer(a -> {
            throw new TeamBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<TeamMemberCreateDTO>> request = HttpRequest.POST("members", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Member %s was not added to Team %s because: %s\"]",
                g2.getMemberid(), g2.getTeamid(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(teamMemberServices, times(2)).save(any(TeamMember.class));
    }

    @Test
    void testReadTeamMember() {
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamMemberServices.read(eq(g.getId()))).thenReturn(g);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", g.getId().toString()));
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadTeamMemberNotFound() {
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamMemberServices.read(eq(g.getTeamid()))).thenReturn(g);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", g.getId().toString()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, TeamMember.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(teamMemberServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindTeamMembers() {
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<TeamMember> teams = Collections.singleton(g);

        when(teamMemberServices.findByFields(eq(g.getTeamid()), eq(g.getMemberid()), eq(null))).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s&memberid=%s", g.getTeamid(),
                g.getMemberid()));
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).findByFields(any(UUID.class), any(UUID.class), eq(null));
    }

    @Test
    void testFindTeamMembersAllParams() {
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<TeamMember> teams = Collections.singleton(g);

        when(teamMemberServices.findByFields(eq(g.getTeamid()), eq(g.getMemberid()), eq(g.isLead()))).thenReturn(teams);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s&memberid=%s&lead=%s", g.getTeamid(),
                g.getMemberid(), g.isLead()));
        final HttpResponse<Set<TeamMember>> response = client.toBlocking().exchange(request, Argument.setOf(TeamMember.class));

        assertEquals(teams, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(teamMemberServices, times(1)).findByFields(any(UUID.class), any(UUID.class), anyBoolean());
    }


    @Test
    void testFindTeamMembersNull() {
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamMemberServices.findByFields(eq(g.getTeamid()), eq(null), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?teamid=%s", g.getTeamid()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(TeamMember.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(teamMemberServices, times(1)).findByFields(any(UUID.class), eq(null), eq(null));
    }


    @Test
    void testUpdateTeamMember() {
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamMemberServices.update(eq(g))).thenReturn(g);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", g);
        final HttpResponse<TeamMember> response = client.toBlocking().exchange(request, TeamMember.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getId()), response.getHeaders().get("location"));

        verify(teamMemberServices, times(1)).update(any(TeamMember.class));
    }

    @Test
    void testUpdateAnInvalidTeamMember() {
        TeamMember g = new TeamMember(UUID.randomUUID(), null, null, true);

        when(teamMemberServices.update(any(TeamMember.class))).thenReturn(g);

        final HttpRequest<TeamMember> request = HttpRequest.PUT("", g);
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
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        when(teamMemberServices.update(any(TeamMember.class))).thenReturn(g);

        final HttpRequest<String> request = HttpRequest.PUT("", "");
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
        TeamMember g = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        final String errorMessage = "error message!";

        when(teamMemberServices.update(any(TeamMember.class))).thenAnswer(a -> {
            throw new TeamBadArgException(errorMessage);
        });

        final MutableHttpRequest<TeamMember> request = HttpRequest.PUT("", g);
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

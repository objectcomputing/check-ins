package com.objectcomputing.checkins.services.guild.member;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.guild.GuildBadArgException;
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
class GuildMemberControllerTest {

    @Inject
    @Client("/services/guild/member")
    HttpClient client;
    @Inject
    private GuildMemberServices guildMemberServices;

    @MockBean(GuildMemberServices.class)
    public GuildMemberServices guildMemberServices() {
        return mock(GuildMemberServices.class);
    }

    @Test
    void testCreateAGuildMember() {
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(UUID.randomUUID());
        guildMemberCreateDTO.setMemberid(UUID.randomUUID());
        guildMemberCreateDTO.setLead(true);

        GuildMember g = new GuildMember(guildMemberCreateDTO.getGuildid(), guildMemberCreateDTO.getMemberid(), guildMemberCreateDTO.isLead());

        when(guildMemberServices.save(eq(g))).thenReturn(g);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getId()), response.getHeaders().get("location"));

        verify(guildMemberServices, times(1)).save(any(GuildMember.class));
    }

    @Test
    void testCreateAnInvalidGuildMember() {
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();

        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), true);
        when(guildMemberServices.save(any(GuildMember.class))).thenReturn(g);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("guildMember.guildid: must not be null", errorList.get(0));
        assertEquals("guildMember.memberid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildMemberServices, never()).save(any(GuildMember.class));
    }

    @Test
    void testCreateANullGuildMember() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), true);
        when(guildMemberServices.save(any(GuildMember.class))).thenReturn(g);

        final HttpRequest<String> request = HttpRequest.POST("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guildMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildMemberServices, never()).save(any(GuildMember.class));
    }

    @Test
    void testLoadGuildMembers() {
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(UUID.randomUUID());
        guildMemberCreateDTO.setMemberid(UUID.randomUUID());
        guildMemberCreateDTO.setLead(true);

        GuildMemberCreateDTO guildMemberCreateDTO2 = new GuildMemberCreateDTO();
        guildMemberCreateDTO2.setGuildid(UUID.randomUUID());
        guildMemberCreateDTO2.setMemberid(UUID.randomUUID());
        guildMemberCreateDTO2.setLead(true);

        List<GuildMemberCreateDTO> dtoList = List.of(guildMemberCreateDTO, guildMemberCreateDTO2);

        GuildMember g = new GuildMember(guildMemberCreateDTO.getGuildid(), guildMemberCreateDTO.getMemberid(), guildMemberCreateDTO.isLead());
        GuildMember g2 = new GuildMember(guildMemberCreateDTO2.getGuildid(), guildMemberCreateDTO2.getMemberid(), guildMemberCreateDTO2.isLead());

        List<GuildMember> guildList = List.of(g, g2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(a -> {
            GuildMember thisG = guildList.get(i.getAndAdd(1));
            assertEquals(thisG, a.getArgument(0));
            return thisG;
        }).when(guildMemberServices).save(any(GuildMember.class));

        final MutableHttpRequest<List<GuildMemberCreateDTO>> request = HttpRequest.POST("members", dtoList);
        final HttpResponse<List<GuildMember>> response = client.toBlocking().exchange(request, Argument.listOf(GuildMember.class));

        assertEquals(guildList, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

        verify(guildMemberServices, times(2)).save(any(GuildMember.class));
    }

    @Test
    void testLoadGuildMembersInvalidGuildMember() {
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(UUID.randomUUID());
        guildMemberCreateDTO.setMemberid(UUID.randomUUID());
        guildMemberCreateDTO.setLead(true);

        GuildMemberCreateDTO guildMemberCreateDTO2 = new GuildMemberCreateDTO();

        List<GuildMemberCreateDTO> dtoList = List.of(guildMemberCreateDTO, guildMemberCreateDTO2);

        final MutableHttpRequest<List<GuildMemberCreateDTO>> request = HttpRequest.POST("members", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("guildMembers.guildid: must not be null", errorList.get(0));
        assertEquals("guildMembers.memberid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildMemberServices, never()).save(any(GuildMember.class));
    }

    @Test
    void testLoadGuildMembersThrowException() {
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(UUID.randomUUID());
        guildMemberCreateDTO.setMemberid(UUID.randomUUID());
        guildMemberCreateDTO.setLead(true);

        GuildMemberCreateDTO guildMemberCreateDTO2 = new GuildMemberCreateDTO();
        guildMemberCreateDTO2.setGuildid(UUID.randomUUID());
        guildMemberCreateDTO2.setMemberid(UUID.randomUUID());
        guildMemberCreateDTO2.setLead(true);

        List<GuildMemberCreateDTO> dtoList = List.of(guildMemberCreateDTO, guildMemberCreateDTO2);

        GuildMember g = new GuildMember(guildMemberCreateDTO.getGuildid(), guildMemberCreateDTO.getMemberid(), guildMemberCreateDTO.isLead());
        GuildMember g2 = new GuildMember(guildMemberCreateDTO2.getGuildid(), guildMemberCreateDTO2.getMemberid(), guildMemberCreateDTO2.isLead());

        final String errorMessage = "error message!";
        when(guildMemberServices.save(eq(g))).thenReturn(g);

        when(guildMemberServices.save(eq(g2))).thenAnswer(a -> {
            throw new GuildBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<GuildMemberCreateDTO>> request = HttpRequest.POST("members", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Member %s was not added to Guild %s because: %s\"]",
                g2.getMemberid(), g2.getGuildid(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(guildMemberServices, times(2)).save(any(GuildMember.class));
    }

    @Test
    void testReadGuildMember() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(guildMemberServices.read(eq(g.getId()))).thenReturn(g);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", g.getId().toString()));
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(guildMemberServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadGuildMemberNotFound() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(guildMemberServices.read(eq(g.getGuildid()))).thenReturn(g);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", g.getId().toString()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, GuildMember.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(guildMemberServices, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindGuildMembers() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<GuildMember> guilds = Collections.singleton(g);

        when(guildMemberServices.findByFields(eq(g.getGuildid()), eq(g.getMemberid()), eq(null))).thenReturn(guilds);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?guildid=%s&memberid=%s", g.getGuildid(),
                g.getMemberid()));
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(guilds, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(guildMemberServices, times(1)).findByFields(any(UUID.class), any(UUID.class), eq(null));
    }

    @Test
    void testFindGuildMembersAllParams() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        Set<GuildMember> guilds = Collections.singleton(g);

        when(guildMemberServices.findByFields(eq(g.getGuildid()), eq(g.getMemberid()), eq(g.isLead()))).thenReturn(guilds);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?guildid=%s&memberid=%s&lead=%s", g.getGuildid(),
                g.getMemberid(), g.isLead()));
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(guilds, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(guildMemberServices, times(1)).findByFields(any(UUID.class), any(UUID.class), anyBoolean());
    }


    @Test
    void testFindGuildMembersNull() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(guildMemberServices.findByFields(eq(g.getGuildid()), eq(null), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?guildid=%s", g.getGuildid()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(GuildMember.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(guildMemberServices, times(1)).findByFields(any(UUID.class), eq(null), eq(null));
    }


    @Test
    void testUpdateGuildMember() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        when(guildMemberServices.update(eq(g))).thenReturn(g);

        final HttpRequest<GuildMember> request = HttpRequest.PUT("", g);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getId()), response.getHeaders().get("location"));

        verify(guildMemberServices, times(1)).update(any(GuildMember.class));
    }

    @Test
    void testUpdateAnInvalidGuildMember() {
        GuildMember g = new GuildMember(UUID.randomUUID(), null, null, true);

        when(guildMemberServices.update(any(GuildMember.class))).thenReturn(g);

        final HttpRequest<GuildMember> request = HttpRequest.PUT("", g);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("guildMember.guildid: must not be null", errorList.get(0));
        assertEquals("guildMember.memberid: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildMemberServices, never()).update(any(GuildMember.class));
    }

    @Test
    void testUpdateANullGuildMember() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);
        when(guildMemberServices.update(any(GuildMember.class))).thenReturn(g);

        final HttpRequest<String> request = HttpRequest.PUT("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guildMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildMemberServices, never()).update(any(GuildMember.class));
    }


    @Test
    void testUpdateGuildMemberThrowException() {
        GuildMember g = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), true);

        final String errorMessage = "error message!";

        when(guildMemberServices.update(any(GuildMember.class))).thenAnswer(a -> {
            throw new GuildBadArgException(errorMessage);
        });

        final MutableHttpRequest<GuildMember> request = HttpRequest.PUT("", g);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildMemberServices, times(1)).update(any(GuildMember.class));
    }

}

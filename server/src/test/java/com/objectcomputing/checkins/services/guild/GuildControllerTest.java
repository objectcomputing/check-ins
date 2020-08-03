package com.objectcomputing.checkins.services.guild;


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
class GuildControllerTest {

    @Inject
    @Client("/services/guild")
    HttpClient client;
    @Inject
    private GuildServices guildService;

    @MockBean(GuildServices.class)
    public GuildServices guildService() {
        return mock(GuildServices.class);
    }

    @Test
    void testCreateAGuild() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name");
        guildCreateDTO.setDescription("description");

        Guild g = new Guild(guildCreateDTO.getName(), guildCreateDTO.getDescription());

        when(guildService.save(eq(g))).thenReturn(g);

        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO);
        final HttpResponse<Guild> response = client.toBlocking().exchange(request, Guild.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getGuildid()), response.getHeaders().get("location"));

        verify(guildService, times(1)).save(any(Guild.class));
    }

    @Test
    void testCreateAnInvalidGuild() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();

        Guild g = new Guild(UUID.randomUUID(), "DNC", "DNC");
        when(guildService.save(any(Guild.class))).thenReturn(g);

        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("guild.description: must not be blank", errorList.get(0));
        assertEquals("guild.name: must not be blank", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildService, never()).save(any(Guild.class));
    }

    @Test
    void testCreateANullGuild() {
        Guild g = new Guild(UUID.randomUUID(), "DNC", "DNC");
        when(guildService.save(any(Guild.class))).thenReturn(g);

        final HttpRequest<String> request = HttpRequest.POST("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guild] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildService, never()).save(any(Guild.class));
    }

    @Test
    void testLoadGuilds() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name");
        guildCreateDTO.setDescription("description");

        GuildCreateDTO guildCreateDTO2 = new GuildCreateDTO();
        guildCreateDTO2.setName("name2");
        guildCreateDTO2.setDescription("description3");

        List<GuildCreateDTO> dtoList = List.of(guildCreateDTO, guildCreateDTO2);

        Guild g = new Guild(guildCreateDTO.getName(), guildCreateDTO.getDescription());
        Guild g2 = new Guild(guildCreateDTO2.getName(), guildCreateDTO2.getDescription());

        List<Guild> guildList = List.of(g, g2);
        AtomicInteger i = new AtomicInteger(0);
        doAnswer(a -> {
            Guild thisG = guildList.get(i.getAndAdd(1));
            assertEquals(thisG, a.getArgument(0));
            return thisG;
        }).when(guildService).save(any(Guild.class));

        final MutableHttpRequest<List<GuildCreateDTO>> request = HttpRequest.POST("guilds", dtoList);
        final HttpResponse<List<Guild>> response = client.toBlocking().exchange(request, Argument.listOf(Guild.class));

        assertEquals(guildList, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

        verify(guildService, times(2)).save(any(Guild.class));
    }

    @Test
    void testLoadGuildsInvalidGuild() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name2");
        guildCreateDTO.setDescription("description");

        GuildCreateDTO guildCreateDTO2 = new GuildCreateDTO();

        List<GuildCreateDTO> dtoList = List.of(guildCreateDTO, guildCreateDTO2);

        final MutableHttpRequest<List<GuildCreateDTO>> request = HttpRequest.POST("guilds", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("guildsList.description: must not be blank", errorList.get(0));
        assertEquals("guildsList.name: must not be blank", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildService, never()).save(any(Guild.class));
    }

    @Test
    void testLoadGuildsThrowException() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name");
        guildCreateDTO.setDescription("description");

        GuildCreateDTO guildCreateDTO2 = new GuildCreateDTO();
        guildCreateDTO2.setName("name2");
        guildCreateDTO2.setDescription("description3");

        List<GuildCreateDTO> dtoList = List.of(guildCreateDTO, guildCreateDTO2);

        Guild g = new Guild(guildCreateDTO.getName(), guildCreateDTO.getDescription());
        Guild g2 = new Guild(guildCreateDTO2.getName(), guildCreateDTO2.getDescription());

        final String errorMessage = "error message!";
        when(guildService.save(eq(g))).thenReturn(g);

        when(guildService.save(eq(g2))).thenAnswer(a -> {
            throw new GuildBadArgException(errorMessage);
        });

        final MutableHttpRequest<List<GuildCreateDTO>> request = HttpRequest.POST("guilds", dtoList);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Guild name2 was not added because: %s\"]", errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

        verify(guildService, times(2)).save(any(Guild.class));
    }

    @Test
    void testReadGuild() {
        Guild g = new Guild(UUID.randomUUID(), "Hello", "World");

        when(guildService.read(eq(g.getGuildid()))).thenReturn(g);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", g.getGuildid().toString()));
        final HttpResponse<Guild> response = client.toBlocking().exchange(request, Guild.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(guildService, times(1)).read(any(UUID.class));
    }

    @Test
    void testReadGuildNotFound() {
        Guild g = new Guild(UUID.randomUUID(), "Hello", "World");

        when(guildService.read(eq(g.getGuildid()))).thenReturn(null);

        final HttpRequest<UUID> request = HttpRequest.GET(String.format("/%s", g.getGuildid().toString()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Guild.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(guildService, times(1)).read(any(UUID.class));
    }

    @Test
    void testFindGuilds() {
        Guild g = new Guild(UUID.randomUUID(), "Hello", "World");
        Set<Guild> guilds = Collections.singleton(g);
        UUID member = UUID.randomUUID();

        when(guildService.findByFields(eq(g.getName()), eq(member))).thenReturn(guilds);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s&memberid=%s", g.getName(),
                member.toString()));
        final HttpResponse<Set<Guild>> response = client.toBlocking().exchange(request, Argument.setOf(Guild.class));

        assertEquals(guilds, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(guildService, times(1)).findByFields(any(String.class), any(UUID.class));
    }

    @Test
    void testFindGuildsNull() {
        Guild g = new Guild(UUID.randomUUID(), "Hello", "World");

        when(guildService.findByFields(eq(g.getName()), eq(null))).thenReturn(null);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", g.getName()));
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Argument.setOf(Guild.class)));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

        verify(guildService, times(1)).findByFields(any(String.class), eq(null));
    }


    @Test
    void testUpdateGuild() {
        Guild g = new Guild(UUID.randomUUID(), "name", "description");

        when(guildService.update(eq(g))).thenReturn(g);

        final HttpRequest<Guild> request = HttpRequest.PUT("", g);
        final HttpResponse<Guild> response = client.toBlocking().exchange(request, Guild.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getGuildid()), response.getHeaders().get("location"));

        verify(guildService, times(1)).update(any(Guild.class));
    }

    @Test
    void testUpdateAnInvalidGuild() {
        Guild g = new Guild(UUID.randomUUID(), null, "");

        when(guildService.update(any(Guild.class))).thenReturn(g);

        final HttpRequest<Guild> request = HttpRequest.PUT("", g);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("guild.description: must not be blank", errorList.get(0));
        assertEquals("guild.name: must not be blank", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildService, never()).update(any(Guild.class));
    }

    @Test
    void testUpdateANullGuild() {
        Guild g = new Guild(UUID.randomUUID(), "DNC", "DNC");
        when(guildService.update(any(Guild.class))).thenReturn(g);

        final HttpRequest<String> request = HttpRequest.PUT("", "");
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guild] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildService, never()).update(any(Guild.class));
    }


    @Test
    void testUpdateGuildThrowException() {
        Guild g = new Guild(UUID.randomUUID(), "name", "description");

        final String errorMessage = "error message!";

        when(guildService.update(any(Guild.class))).thenAnswer(a -> {
            throw new GuildBadArgException(errorMessage);
        });

        final MutableHttpRequest<Guild> request = HttpRequest.PUT("", g);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(errorMessage, errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

        verify(guildService, times(1)).update(any(Guild.class));
    }

}

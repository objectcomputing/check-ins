package com.objectcomputing.checkins.services.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.GuildFixture;
import com.objectcomputing.checkins.services.fixture.GuildMemberFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
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

class GuildControllerTest extends TestContainersSuite implements GuildFixture,
        MemberProfileFixture, GuildMemberFixture {

    @Inject
    @Client("/services/guild")
    HttpClient client;

    @Test
    void testCreateAGuild() {

        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name");
        guildCreateDTO.setDescription("description");

        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Guild> response = client.toBlocking().exchange(request, Guild.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(guildCreateDTO.getDescription(), response.body().getDescription());
        assertEquals(guildCreateDTO.getName(),response.body().getName());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), response.getHeaders().get("location"));

    }

    @Test
    void testCreateAnInvalidGuild() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();

        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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

    }

    @Test
    void testCreateANullGuild() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guild] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

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

        final MutableHttpRequest<List<GuildCreateDTO>> request = HttpRequest.POST("guilds", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<List<Guild>> response = client.toBlocking().exchange(request, Argument.listOf(Guild.class));

        assertEquals(response.body().get(0).getDescription(), guildCreateDTO.getDescription());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

    }

    @Test
    void testLoadGuildsInvalidGuild() {

        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name2");
        guildCreateDTO.setDescription("description");

        GuildCreateDTO guildCreateDTO2 = new GuildCreateDTO();

        List<GuildCreateDTO> dtoList = List.of(guildCreateDTO, guildCreateDTO2);

        final MutableHttpRequest<List<GuildCreateDTO>> request = HttpRequest.POST("guilds", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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

    }

    @Test
    void testLoadGuildsThrowException() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name");
        guildCreateDTO.setDescription("description");

        GuildCreateDTO guildCreateDTO2 = new GuildCreateDTO();
        guildCreateDTO2.setName("name");
        guildCreateDTO2.setDescription("description3");

        List<GuildCreateDTO> dtoList = List.of(guildCreateDTO, guildCreateDTO2);

        final String errorMessage = "Guild with name name already exists";

        final MutableHttpRequest<List<GuildCreateDTO>> request = HttpRequest.POST("guilds", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Guild name was not added because: %s\"]", errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

    }

    @Test
    void testReadGuild() {

        Guild g = createDeafultGuild();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", g.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Guild> response = client.toBlocking().exchange(request, Guild.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadGuildNotFound() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Guild.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

    }

    @Test
    void testFindGuildS() {

        Guild g = createDeafultGuild();
        MemberProfile mp = createADefaultMemberProfile();
        GuildMember gm = createDeafultGuildMember(g, mp);
        Set<Guild> guilds = Collections.singleton(g);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s&memberid=%s", g.getName(),
                mp.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Guild>> response = client.toBlocking().exchange(request, Argument.setOf(Guild.class));

        assertEquals(guilds, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindGuildSByMemeberid() {

        Guild g = createDeafultGuild();
        MemberProfile mp = createADefaultMemberProfile();
        GuildMember gm = createDeafultGuildMember(g, mp);
        Set<Guild> guilds = Collections.singleton(g);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", mp.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Guild>> response = client.toBlocking().exchange(request, Argument.setOf(Guild.class));

        assertEquals(guilds, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindAllGuilds() {

        Guild g = createDeafultGuild();
        MemberProfile mp = createADefaultMemberProfile();

        final HttpRequest<?> request = HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Guild>> response = client.toBlocking().exchange(request, Argument.setOf(Guild.class));

        assertEquals(Set.of(g), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateGuild() {

        Guild g = createDeafultGuild();

        final HttpRequest<Guild> request = HttpRequest.PUT("", g).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Guild> response = client.toBlocking().exchange(request, Guild.class);

        assertEquals(g, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), g.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testUpdateAnInvalidGuild() {

        Guild g = createDeafultGuild();
        Guild g2 = new Guild(g.getId(), null, "");

        final HttpRequest<Guild> request = HttpRequest.PUT("", g2).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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

    }

    @Test
    void testUpdateANullGuild() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guild] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateGuildThrowException() {

        Guild g = createDeafultGuild();
        g.setId(UUID.randomUUID());
        final String errorMessage = "Guild %s does not exist, can't update.";

        final MutableHttpRequest<Guild> request = HttpRequest.PUT("", g).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals(String.format("Guild %s does not exist, can't update.",g.getId()), errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

}

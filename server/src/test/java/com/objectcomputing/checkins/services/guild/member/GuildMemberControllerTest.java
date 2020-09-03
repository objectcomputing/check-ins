package com.objectcomputing.checkins.services.guild.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.GuildFixture;
import com.objectcomputing.checkins.services.fixture.GuildMemberFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.guild.Guild;
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

class GuildMemberControllerTest extends TestContainersSuite implements GuildFixture, MemberProfileFixture, GuildMemberFixture {

    @Inject
    @Client("/services/guild/member")
    HttpClient client;

    @Test
    void testCreateAGuildMember() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(guild.getId());
        guildMemberCreateDTO.setMemberid(memberProfile.getUuid());
        guildMemberCreateDTO.setLead(true);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        assertEquals(guildMemberCreateDTO.getGuildid(), response.body().getGuildid());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), response.getHeaders().get("location"));

    }

    @Test
    void testCreateAnInvalidGuildMember() {

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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

    }

    @Test
    void testCreateANullGuildMember() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guildMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateDuplicateGuildMember() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(guild.getId());
        guildMemberCreateDTO.setMemberid(memberProfile.getUuid());

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals(String.format("Member %s already exists in guild %s",guildMemberCreateDTO.getMemberid(), guildMemberCreateDTO.getGuildid()), errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateAGuildMemberWithNonExistingGuild() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(UUID.randomUUID());
        guildMemberCreateDTO.setMemberid(memberProfile.getUuid());
        guildMemberCreateDTO.setLead(false);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Guild %s doesn't exist",guildMemberCreateDTO.getGuildid()),error);
    }

    @Test
    void testCreateAGuildMemberWithNonExistingMember() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(guild.getId());
        guildMemberCreateDTO.setMemberid(UUID.randomUUID());
        guildMemberCreateDTO.setLead(false);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist",guildMemberCreateDTO.getMemberid()),error);
    }


    @Test
    void testLoadGuildMembers() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(guild.getId());
        guildMemberCreateDTO.setMemberid(memberProfile.getUuid());
        guildMemberCreateDTO.setLead(true);

        MemberProfile memberProfile1 = createADefaultMemberProfileForPdl(memberProfile);

        GuildMemberCreateDTO guildMemberCreateDTO2 = new GuildMemberCreateDTO();
        guildMemberCreateDTO2.setGuildid(guild.getId());
        guildMemberCreateDTO2.setMemberid(memberProfile1.getUuid());
        guildMemberCreateDTO2.setLead(true);

        List<GuildMemberCreateDTO> dtoList = List.of(guildMemberCreateDTO, guildMemberCreateDTO2);

        final MutableHttpRequest<List<GuildMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<List<GuildMember>> response = client.toBlocking().exchange(request, Argument.listOf(GuildMember.class));

        assertEquals(guildMemberCreateDTO.getMemberid(), response.body().get(0).getMemberid());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(request.getPath(), response.getHeaders().get("location"));

    }

    @Test
    void testLoadGuildMembersInvalidGuildMember() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(guild.getId());
        guildMemberCreateDTO.setMemberid(memberProfile.getUuid());
        guildMemberCreateDTO.setLead(true);

        GuildMemberCreateDTO guildMemberCreateDTO2 = new GuildMemberCreateDTO();
        List<GuildMemberCreateDTO> dtoList = List.of(guildMemberCreateDTO, guildMemberCreateDTO2);

        final MutableHttpRequest<List<GuildMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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

    }

    @Test
    void testLoadGuildMembersThrowException() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO();
        guildMemberCreateDTO.setGuildid(guild.getId());
        guildMemberCreateDTO.setMemberid(memberProfile.getUuid());
        guildMemberCreateDTO.setLead(true);

        GuildMemberCreateDTO guildMemberCreateDTO2 = new GuildMemberCreateDTO();
        guildMemberCreateDTO2.setGuildid(guild.getId());
        guildMemberCreateDTO2.setMemberid(memberProfile.getUuid());
        guildMemberCreateDTO2.setLead(true);

        List<GuildMemberCreateDTO> dtoList = List.of(guildMemberCreateDTO, guildMemberCreateDTO2);

        final String errorMessage = String.format("Member %s already exists in guild %s",guildMemberCreateDTO2.getMemberid(),guildMemberCreateDTO2.getGuildid());

        final MutableHttpRequest<List<GuildMemberCreateDTO>> request = HttpRequest.POST("members", dtoList).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class));

        assertEquals(String.format("[\"Member %s was not added to Guild %s because: %s\"]",
                guildMemberCreateDTO2.getMemberid(), guildMemberCreateDTO2.getGuildid(), errorMessage), responseException.getResponse().body());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), responseException.getResponse().getHeaders().get("location"));

    }

    @Test
    void testReadGuildMember() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", guildMember.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        assertEquals(guildMember, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadGuildMemberNotFound() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, GuildMember.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());

    }

    @Test
    void testFindGuildMembers() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);

        Set<GuildMember> guilds = Collections.singleton(guildMember);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?guildid=%s&memberid=%s", guildMember.getGuildid(),
                guildMember.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(guilds, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindGuildMembersAllParams() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);

        Set<GuildMember> guilds = Collections.singleton(guildMember);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?guildid=%s&memberid=%s&lead=%s", guildMember.getGuildid(),
                guildMember.getMemberid(), guildMember.isLead())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(guilds, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdateGuildMember() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);

        final HttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        assertEquals(guildMember, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), guildMember.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testUpdateAnInvalidGuildMember() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);
        guildMember.setGuildid(null);
        guildMember.setMemberid(null);

        final HttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
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

    }

    @Test
    void testUpdateANullGuildMember() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guildMember] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateGuildMemberThrowException() {

        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);
        guildMember.setMemberid(UUID.randomUUID());
        guildMember.setGuildid(guildMember.getGuildid());

        final MutableHttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals(String.format("Member %s doesn't exist",guildMember.getMemberid()), errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateGuildMemberThrowExceptionWithNoGuild() {
        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);
        guildMember.setMemberid(guildMember.getMemberid());
        guildMember.setGuildid(UUID.randomUUID());

        final MutableHttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Guild %s doesn't exist",guildMember.getGuildid()),error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateGuildMemberThrowExceptionWithInvalidId() {
        Guild guild = createDeafultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDeafultGuildMember(guild, memberProfile);
        guildMember.setId(UUID.randomUUID());
        guildMember.setMemberid(guildMember.getMemberid());
        guildMember.setGuildid(guildMember.getGuildid());

        final MutableHttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate guildMember to update with id %s",guildMember.getId()),error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

}

package com.objectcomputing.checkins.services.guild.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.GuildFixture;
import com.objectcomputing.checkins.services.fixture.GuildMemberFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
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
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GuildMemberControllerTest extends TestContainersSuite implements GuildFixture, MemberProfileFixture, RoleFixture, GuildMemberFixture {

    @Inject
    @Client("/services/guilds/members")
    HttpClient client;

    @Mock
    private EmailSender emailSender = mock(EmailSender.class);

    @Inject
    private GuildMemberServicesImpl guildMemberServices;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(emailSender);
        guildMemberServices.setEmailSender(emailSender);
    }

    @Test
    void testEmailSentWhenMemberRemovesThemselves() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        createLeadGuildMember(guild, createAnUnrelatedUser());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", guildMember.getId())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        client.toBlocking().exchange(request, GuildMember.class);

        verify(emailSender).sendEmail(null, null,
                "Membership Changes have been made to the " +guild.getName()+" guild",
                "<h3>Bill Charles has left the Ninja guild.</h3><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.",
                "nobody@objectcomputing.com"
        );
    }

    @Test
    void testEmailSentWhenMemberAddsThemselves() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO(guild.getId(), memberProfile.getId(), false);

        createLeadGuildMember(guild, createAnUnrelatedUser());

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO)
                .basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        client.toBlocking().exchange(request, GuildMember.class);

        verify(emailSender).sendEmail(null, null,
                "Membership changes have been made to the " +guild.getName()+" guild",
                "<h3>Bill Charles has joined the Ninja guild.</h3><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.",
                "nobody@objectcomputing.com"
        );
    }


    @Test
    void testEmailSentToMultipleRecipientsWhenMemberAddsThemselves() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO(guild.getId(), memberProfile.getId(), false);

        createLeadGuildMember(guild, createAnUnrelatedUser());
        createLeadGuildMember(guild, createANewHireProfile());

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO)
                .basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        client.toBlocking().exchange(request, GuildMember.class);

        verify(emailSender).sendEmail(null, null,
                "Membership changes have been made to the " +guild.getName()+" guild",
                "<h3>Bill Charles has joined the Ninja guild.</h3><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.",
                "nobody@objectcomputing.com",
                "billm@objectcomputing.com"
        );
    }


    @Test
    void noEmailSentToGuildLeadsThatRemoveThemselves() {
        Guild guild = createDefaultGuild();

        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember leavingGuildMember = createLeadGuildMember(guild, memberProfile);

        createLeadGuildMember(guild, createAnUnrelatedUser());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", leavingGuildMember.getId())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        client.toBlocking().exchange(request, GuildMember.class);
        // only sends email to the guild lead that is still in the guild
        verify(emailSender).sendEmail(null, null,
                "Membership Changes have been made to the " +guild.getName()+" guild",
                "<h3>Bill Charles has left the Ninja guild.</h3><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.",
                "nobody@objectcomputing.com"
        );
    }

    @Test
    void guildLeadCantLeaveGuildIfNoOtherGuildLeadPresent() {
        Guild guild = createDefaultGuild();

        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember leavingGuildMember = createLeadGuildMember(guild, memberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", leavingGuildMember.getId())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request));

        assertEquals("At least one guild lead must be present in the guild at all times", responseException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateAGuildMemberByAdmin() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO(guild.getId(), memberProfile.getId(), false);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        GuildMember guildMember = response.body();

        assertEquals(guildMemberCreateDTO.getMemberId(), guildMember.getMemberId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), guildMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAGuildMemberByGuildLead() {
        Guild guild = createDefaultGuild();

        // Create a guild lead and add him to the guild
        MemberProfile memberProfileOfGuildLead = createADefaultMemberProfile();
        createLeadGuildMember(guild, memberProfileOfGuildLead);

        // Create a member and add him to guild
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO(guild.getId(), memberProfileOfUser.getId(), false);
        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(memberProfileOfGuildLead.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        GuildMember guildMember = response.body();

        assertEquals(guildMemberCreateDTO.getMemberId(), guildMember.getMemberId());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), guildMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testCreateAGuildMemberThrowsExceptionForNotAdminAndNotGuildLead() {
        Guild guild = createDefaultGuild();

        // Create a user (not guild lead) and add him to the guild
        MemberProfile memberProfileOfGuildmate = createADefaultMemberProfile();
        createDefaultGuildMember(guild, memberProfileOfGuildmate);

        // Create a member and add him to guild
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO(guild.getId(), memberProfileOfUser.getId(), false);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(memberProfileOfGuildmate.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("You are not authorized to perform this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testCreateAnInvalidGuildMember() {
        GuildMemberCreateDTO dto = new GuildMemberCreateDTO(null, null, null);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("guildMember.guildId: must not be null", errorList.get(0));
        assertEquals("guildMember.memberId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testCreateANullGuildMember() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guildMember] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateAGuildMemberWithNonExistingGuild() {

        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberResponseDTO = new GuildMemberCreateDTO(UUID.randomUUID(), memberProfile.getId(), false);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberResponseDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Guild %s doesn't exist", guildMemberResponseDTO.getGuildId()), error);
    }

    @Test
    void testCreateAGuildMemberWithNonExistingMember() {

        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO requestDTO = new GuildMemberCreateDTO(guild.getId(), UUID.randomUUID(), false);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", requestDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s doesn't exist", requestDTO.getMemberId()), error);
    }

    @Test
    void testCreateAGuildMemberWithExistingMemberAndGuild() {

        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        GuildMemberCreateDTO guildMemberResponseDTO = new GuildMemberCreateDTO(guildMember.getGuildId(), memberProfile.getId(), false);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberResponseDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Member %s already exists in guild %s", guildMemberResponseDTO.getMemberId(), guildMemberResponseDTO.getGuildId()), error);
    }

    @Test
    void testReadGuildMember() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);
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
    void testFindAllGuildMembers() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(Set.of(guildMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByGuildId() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?guildid=%s", guildMember.getGuildId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(Set.of(guildMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByMemberId() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", guildMember.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(Set.of(guildMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindGuildMembers() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?guildid=%s&memberid=%s", guildMember.getGuildId(),
                guildMember.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(Set.of(guildMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testFindGuildMembersAllParams() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?guildid=%s&memberid=%s&lead=%s", guildMember.getGuildId(),
                guildMember.getMemberId(), guildMember.getLead())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildMember>> response = client.toBlocking().exchange(request, Argument.setOf(GuildMember.class));

        assertEquals(Set.of(guildMember), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdateGuildMemberByAdmin() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        GuildMemberUpdateDTO guildMemberUpdateDTO = new GuildMemberUpdateDTO(guildMember.getId(), guildMember.getGuildId(), guildMember.getMemberId(), true);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final MutableHttpRequest<GuildMemberUpdateDTO> request = HttpRequest.PUT("", guildMemberUpdateDTO).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        GuildMember result = response.body();
        assertNotNull(result);
        assertEquals(guildMember.getMemberId(), result.getMemberId());
        assertTrue(result.getLead());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), guildMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateGuildMemberByGuildLead() {
        Guild guild = createDefaultGuild();

        // Create a guild lead and add him to the guild
        MemberProfile memberProfileOfGuildLead = createADefaultMemberProfile();
        createLeadGuildMember(guild, memberProfileOfGuildLead);

        // Create a member and add him to guild
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        GuildMember guildMember = createDefaultGuildMember(guild, memberProfileOfUser);

        // Update member
        GuildMemberUpdateDTO guildMemberUpdateDTO = new GuildMemberUpdateDTO(guildMember.getId(), guildMember.getGuildId(), guildMember.getMemberId(), true);
        final MutableHttpRequest<GuildMemberUpdateDTO> request = HttpRequest.PUT("", guildMemberUpdateDTO).basicAuth(memberProfileOfGuildLead.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        GuildMember result = response.body();
        assertNotNull(result);
        assertEquals(guildMember.getMemberId(), result.getMemberId());
        assertTrue(result.getLead());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), guildMember.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateGuildMemberThrowsExceptionForNotAdminAndNotGuildLead() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        final HttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals("You are not authorized to perform this operation", error);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateAnInvalidGuildMember() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);
        guildMember.setMemberId(null);
        guildMember.setGuildId(null);

        final HttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("guildMember.guildId: must not be null", errorList.get(0));
        assertEquals("guildMember.memberId: must not be null", errorList.get(1));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullGuildMember() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guildMember] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }


    @Test
    void testUpdateGuildMemberThrowException() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);
        guildMember.setMemberId(UUID.randomUUID());
        guildMember.setGuildId(guildMember.getGuildId());

        final MutableHttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Member %s doesn't exist", guildMember.getMemberId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateGuildMemberThrowExceptionWithNoGuild() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);
        guildMember.setMemberId(guildMember.getMemberId());
        guildMember.setGuildId(UUID.randomUUID());

        final MutableHttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Guild %s doesn't exist", guildMember.getGuildId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testUpdateGuildMemberThrowExceptionWithInvalidId() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);
        guildMember.setId(UUID.randomUUID());
        guildMember.setMemberId(guildMember.getMemberId());
        guildMember.setGuildId(guildMember.getGuildId());

        final MutableHttpRequest<GuildMember> request = HttpRequest.PUT("", guildMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Unable to locate guildMember to update with id %s", guildMember.getId()), error);
        assertEquals(request.getPath(), href);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void testDeleteGuildMemberAsAdmin() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", guildMember.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testMemberCanRemoveThemselfFromGuild() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", guildMember.getId())).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteGuildMemberWithUnrelatedUserThrowsException() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        MemberProfile unrelatedUser = createAnUnrelatedUser();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", guildMember.getId())).basicAuth(unrelatedUser.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

    }

    @Test
    void testDeleteGuildMemberWithGuildLead() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile leadMemberProfile = createAnUnrelatedUser();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);
        GuildMember guildLead = createLeadGuildMember(guild, leadMemberProfile);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", guildMember.getId())).basicAuth(leadMemberProfile.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteInvalidGuildMemberAsAdmin() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);
        guildMember.setId(UUID.randomUUID());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", guildMember.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testGuildMemberHistoryWhenAGuildMemberIsSavedByAdmin() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMemberCreateDTO guildMemberCreateDTO = new GuildMemberCreateDTO(guild.getId(), memberProfile.getId(), false);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest<GuildMemberCreateDTO> request = HttpRequest.POST("", guildMemberCreateDTO).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        GuildMember guildMember = response.body();

        final List<GuildMemberHistory> entries = (List<GuildMemberHistory>) getGuildMemberHistoryRepository().findAll();

        assertEquals("Added", entries.get(0).getChange());
    }

    @Test
    void testGuildMemberHistoryWhenUpdateGuildMemberByGuildLead() {
        Guild guild = createDefaultGuild();

        // Create a guild lead and add him to the guild
        MemberProfile memberProfileOfGuildLead = createADefaultMemberProfile();
        createLeadGuildMember(guild, memberProfileOfGuildLead);

        // Create a member and add him to guild
        MemberProfile memberProfileOfUser = createAnUnrelatedUser();
        GuildMember guildMember = createDefaultGuildMember(guild, memberProfileOfUser);

        // Update member
        GuildMemberUpdateDTO guildMemberUpdateDTO = new GuildMemberUpdateDTO(guildMember.getId(), guildMember.getGuildId(), guildMember.getMemberId(), true);
        final MutableHttpRequest<GuildMemberUpdateDTO> request = HttpRequest.PUT("", guildMemberUpdateDTO).basicAuth(memberProfileOfGuildLead.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        final List<GuildMemberHistory> entries = (List<GuildMemberHistory>) getGuildMemberHistoryRepository().findAll();

        assertEquals("Updated", entries.get(0).getChange());
    }

    @Test
    void testGuildMemberHistoryWhenDeletingGuildMemberAsAdmin() {
        Guild guild = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();

        GuildMember guildMember = createDefaultGuildMember(guild, memberProfile);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", guildMember.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);

        final HttpResponse<GuildMember> response = client.toBlocking().exchange(request, GuildMember.class);

        final List<GuildMemberHistory> entries = (List<GuildMemberHistory>) getGuildMemberHistoryRepository().findAll();

        assertEquals("Deleted", entries.get(0).getChange());
    }


}

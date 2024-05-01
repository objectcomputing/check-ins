package com.objectcomputing.checkins.services.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.GuildFixture;
import com.objectcomputing.checkins.services.fixture.GuildMemberFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.guild.member.GuildMemberServicesImpl;
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

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GuildControllerTest extends TestContainersSuite implements GuildFixture,
        MemberProfileFixture, RoleFixture, GuildMemberFixture {

    @Inject
    @Client("/services/guilds")
    HttpClient client;

    @Mock
    private final EmailSender emailSender = mock(EmailSender.class);

    @Inject
    private GuildServicesImpl guildServicesImpl;

    @Inject
    private GuildMemberServicesImpl guildMemberServicesImpl;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(emailSender);
        guildServicesImpl.setEmailSender(emailSender);
        guildMemberServicesImpl.setEmailSender(emailSender);
    }

    @Test
    void testEmailSentToGuildLeadWhenGuildMembersAdded() {
        // create a guild and guild lead
        Guild guildEntity = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildLead = createLeadGuildMember(guildEntity, memberProfile);

        // Create an admin to request the changes
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        // create member and a DTO for the request to add them to the guild
        MemberProfile newMember = createADefaultMemberProfileWithBirthDay();
        GuildUpdateDTO.GuildMemberUpdateDTO newMemberDTO = guildMemberUpdateDTOFromNonExistingMember(newMember, false);

        // create a guildUpdateDTO from existing guild
        GuildUpdateDTO requestBody = updateFromEntity(guildEntity);
        // create list of existing guild lead and new member and add it to the request
        List<GuildUpdateDTO.GuildMemberUpdateDTO> newAndExistingMembers = new ArrayList<>();
        newAndExistingMembers.add(newMemberDTO);
        newAndExistingMembers.add(updateDefaultGuildMemberDto(guildLead, guildLead.isLead()));
        requestBody.setGuildMembers(newAndExistingMembers);

        final HttpRequest<GuildUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        client.toBlocking().exchange(request, GuildResponseDTO.class);

        verify(emailSender).sendEmail(null, null,
                "Membership Changes have been made to the Ninja guild",
                "<h3>Changes have been made to the Ninja guild.</h3><h4>The following members have been added:</h4><ul><li>Bill Charles</li></ul><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.",
                "billm@objectcomputing.com"
        );
    }

    @Test
    void testEmailSentToGuildLeadWhenGuildMembersRemoved() {
        // create a guild and guild lead
        Guild guildEntity = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildLead = createLeadGuildMember(guildEntity, memberProfile);

        // Create and add a member to the guild
        createDefaultGuildMember(guildEntity, createADefaultMemberProfileWithBirthDay());

        // Create an admin to request the changes
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        // create a guildUpdateDTO from existing guild
        GuildUpdateDTO requestBody = updateFromEntity(guildEntity);
        // only include the guild lead in the request (effectively removes the guild member)
        requestBody.setGuildMembers(Collections.singletonList(updateDefaultGuildMemberDto(guildLead, guildLead.isLead())));

        final HttpRequest<GuildUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        client.toBlocking().exchange(request, GuildResponseDTO.class);

        verify(emailSender).sendEmail(null, null,
                "Membership Changes have been made to the Ninja guild",
                "<h3>Changes have been made to the Ninja guild.</h3><h4>The following members have been removed:</h4><ul><li>Bill Charles</li></ul><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.",
                "billm@objectcomputing.com"
        );
    }
    
    @Test
    void testCreateAGuild() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name");
        guildCreateDTO.setDescription("description");
        guildCreateDTO.setLink("https://www.compass.objectcomputing.com/guilds/name");
        guildCreateDTO.setGuildMembers(List.of(createDefaultGuildMemberDto(createADefaultMemberProfile(), true)));
        guildCreateDTO.setCommunity(false);

        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<GuildResponseDTO> response = client.toBlocking().exchange(request, GuildResponseDTO.class);

        assertTrue(response.getBody().isPresent());
        GuildResponseDTO guildEntity = response.getBody().get();
        assertEquals(guildCreateDTO.getDescription(), guildEntity.getDescription());
        assertEquals(guildCreateDTO.getName(), guildEntity.getName());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), guildEntity.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testCreateGuildFakeLink() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name");
        guildCreateDTO.setDescription("description");
        guildCreateDTO.setLink("wwwu.fakelink.com");
        guildCreateDTO.setGuildMembers(List.of(createDefaultGuildMemberDto(createADefaultMemberProfile(), true)));
        guildCreateDTO.setCommunity(false);

        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);

        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Link is invalid", errors.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), href.asText());

    }
    @Test
    void testCreateGuildNoLeads() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setName("name");
        guildCreateDTO.setDescription("description");
        guildCreateDTO.setLink("https://www.compass.objectcomputing.com/guilds/name");
        guildCreateDTO.setGuildMembers(new ArrayList<>());
        guildCreateDTO.setCommunity(false);


        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);

        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Guild must include at least one guild lead", errors.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), href.asText());
    }

    @Test
    void testCreateAnInvalidGuild() {
        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setCommunity(false);

        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("guild.name: must not be blank", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullGuild() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guild] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateAGuildWithExistingName() {

        Guild guildEntity = createDefaultGuild();

        GuildCreateDTO guildCreateDTO = new GuildCreateDTO();
        guildCreateDTO.setDescription("test");
        guildCreateDTO.setName(guildEntity.getName());
        guildCreateDTO.setLink("https://www.compass.objectcomputing.com/guilds/name");
        guildCreateDTO.setGuildMembers(new ArrayList<>());
        guildCreateDTO.setGuildMembers(List.of(createDefaultGuildMemberDto(createADefaultMemberProfile(), true)));
        guildCreateDTO.setCommunity(false);

        final HttpRequest<GuildCreateDTO> request = HttpRequest.POST("", guildCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Guild with name %s already exists", guildCreateDTO.getName()), error);
    }

    @Test
    void testReadGuild() {
        Guild guildEntity = createDefaultGuild() ;
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", guildEntity.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<GuildResponseDTO> response = client.toBlocking().exchange(request, GuildResponseDTO.class);
        assertEntityDTOEqual(guildEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadGuildNotFound() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Guild.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindAllGuilds() {

        Guild guildEntity = createDefaultGuild();

        final HttpRequest<?> request = HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(GuildResponseDTO.class));

        assertEntityDTOEqual(Set.of(guildEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByName() {
        Guild guildEntity = createDefaultGuild() ;

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", guildEntity.getName())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(GuildResponseDTO.class));

        assertEntityDTOEqual(Set.of(guildEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Guild guildEntity = createDefaultGuild();

        GuildMember guildMemberEntity = createDefaultGuildMember(guildEntity, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", guildMemberEntity.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(GuildResponseDTO.class));

        assertEntityDTOEqual(Set.of(guildEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindGuilds() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Guild guildEntity = createDefaultGuild();

        GuildMember guildMemberEntity = createDefaultGuildMember(guildEntity, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s&memberid=%s", guildEntity.getName(),
                guildMemberEntity.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<GuildResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(GuildResponseDTO.class));

        assertEntityDTOEqual(Set.of(guildEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdatePermissionDenied() {
        Guild guildEntity = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDefaultGuildMember(guildEntity, memberProfile);

        GuildUpdateDTO requestBody = updateFromEntity(guildEntity);
        GuildUpdateDTO.GuildMemberUpdateDTO newMember = updateDefaultGuildMemberDto(guildMember,true);
        newMember.setLead(true);
        requestBody.setGuildMembers(Collections.singletonList(newMember));

        final HttpRequest<GuildUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals(request.getPath(), href);
        assertEquals("You are not authorized to perform this operation", error);
    }

    @Test
    void testUpdateGuildSuccess() {
        Guild guildEntity = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDefaultGuildMember(guildEntity, memberProfile);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        GuildUpdateDTO requestBody = updateFromEntity(guildEntity);
        GuildUpdateDTO.GuildMemberUpdateDTO newMember = updateDefaultGuildMemberDto(guildMember,true);
        newMember.setLead(true);
        requestBody.setGuildMembers(Collections.singletonList(newMember));

        final HttpRequest<GuildUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<GuildResponseDTO> response = client.toBlocking().exchange(request, GuildResponseDTO.class);

        assertEntityDTOEqual(guildEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), guildEntity.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateGuildWithExistingMembers() {
        Guild guildEntity = createDefaultGuild();
        MemberProfile memberProfile = createADefaultMemberProfile();
        GuildMember guildMember = createDefaultGuildMember(guildEntity, memberProfile);
        String newName = "New Name";
        guildEntity.setName(newName);

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        GuildUpdateDTO requestBody = updateFromEntity(guildEntity);

        GuildUpdateDTO.GuildMemberUpdateDTO newMember = updateDefaultGuildMemberDto(guildMember,true);
        requestBody.setGuildMembers(Collections.singletonList(newMember));

        final HttpRequest<GuildUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<GuildResponseDTO> response = client.toBlocking().exchange(request, GuildResponseDTO.class);

        assertEntityDTOEqual(guildEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), guildEntity.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateGuildNullName() {
        Guild guildEntity = createDefaultGuild();

        GuildUpdateDTO requestBody = new GuildUpdateDTO(guildEntity.getId(), null, null,null, false);
        requestBody.setGuildMembers(new ArrayList<>());

        final HttpRequest<GuildUpdateDTO> request = HttpRequest.PUT("", requestBody)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("guild.name: must not be blank", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullGuild() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [guild] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }


    @Test
    void testUpdateGuildNotExist() {
        Guild guildEntity = createDefaultGuild();
        UUID requestId = UUID.randomUUID();
        GuildUpdateDTO requestBody = new GuildUpdateDTO(requestId.toString(), guildEntity.getName(),
                guildEntity.getDescription(), guildEntity.getLink(), guildEntity.isCommunity());
        requestBody.setGuildMembers(new ArrayList<>());

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        final MutableHttpRequest<GuildUpdateDTO> request = HttpRequest.PUT("", requestBody)
                .basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Guild ID %s does not exist, can't update.", requestId), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void deleteGuildByMember() {
        // setup guild
        Guild guildEntity = createDefaultGuild();
        // create members
        MemberProfile memberProfileofGuildLeadEntity = createADefaultMemberProfile();
        MemberProfile memberProfileOfGuildMember = createADefaultMemberProfileForPdl(memberProfileofGuildLeadEntity);
        //add members to guild
        createLeadGuildMember(guildEntity, memberProfileofGuildLeadEntity);
        createDefaultGuildMember(guildEntity, memberProfileOfGuildMember);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", guildEntity.getId())).basicAuth(memberProfileOfGuildMember.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        assertEquals("You are not authorized to perform this operation", errors.asText());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void deleteGuildByAdmin() {
        // setup guild
        Guild guildEntity = createDefaultGuild();
        // create members
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);
        //add members to guild
        createDefaultGuildMember(guildEntity, memberProfileOfAdmin);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", guildEntity.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteGuildByGuildLead() {
        // setup guild
        Guild guildEntity = createDefaultGuild();
        // create members
        MemberProfile memberProfileofGuildLeadEntity = createADefaultMemberProfile();
        //add members to guild
        createLeadGuildMember(guildEntity, memberProfileofGuildLeadEntity);
        // createDefaultGuildMember(guild, memberProfileOfGuildMember);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", guildEntity.getId())).basicAuth(memberProfileofGuildLeadEntity.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteGuildByUnrelatedUser() {
        // setup guild
        Guild guildEntity = createDefaultGuild();
        // create members
        MemberProfile user = createAnUnrelatedUser();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", guildEntity.getId())).basicAuth(user.getWorkEmail(), MEMBER_ROLE);

        //throw error
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        assertEquals("You are not authorized to perform this operation", errors.asText());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    private void assertEntityDTOEqual(Collection<Guild> entities, Collection<GuildResponseDTO> dtos) {
        assertEquals(entities.size(), dtos.size());
        Iterator<Guild> iEntity = entities.iterator();
        Iterator<GuildResponseDTO> iDTO = dtos.iterator();
        while (iEntity.hasNext() && iDTO.hasNext()) {
            assertEntityDTOEqual(iEntity.next(), iDTO.next());
        }
    }

    private void assertEntityDTOEqual(Guild entity, GuildResponseDTO dto) {
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getDescription(), dto.getDescription());
    }
}

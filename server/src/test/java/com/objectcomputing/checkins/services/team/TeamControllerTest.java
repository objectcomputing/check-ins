package com.objectcomputing.checkins.services.team;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.fixture.TeamMemberFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.member.TeamMember;
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

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamControllerTest extends TestContainersSuite implements TeamFixture, MemberProfileFixture, TeamMemberFixture {

    @Inject
    @Client("/services/team")
    HttpClient client;

    @Test
    void testCreateATeam() {
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();
        teamCreateDTO.setName("name");
        teamCreateDTO.setDescription("description");
        teamCreateDTO.setTeamMembers(new ArrayList<>());

        final HttpRequest<TeamCreateDTO> request = HttpRequest.POST("", teamCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamResponseDTO> response = client.toBlocking().exchange(request, TeamResponseDTO.class);

        TeamResponseDTO teamEntity = response.body();

        assertEquals(teamCreateDTO.getDescription(), teamEntity.getDescription());
        assertEquals(teamCreateDTO.getName(), teamEntity.getName());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamEntity.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testCreateAnInvalidTeam() {
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();

        final HttpRequest<TeamCreateDTO> request = HttpRequest.POST("", teamCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("team.name: must not be blank", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullTeam() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [team] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateATeamWithExistingName() {

        Team teamEntity = createDeafultTeam();

        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();
        teamCreateDTO.setDescription("test");
        teamCreateDTO.setName(teamEntity.getName());
        teamCreateDTO.setTeamMembers(new ArrayList<>());

        final HttpRequest<TeamCreateDTO> request = HttpRequest.POST("", teamCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Team with name %s already exists",teamCreateDTO.getName()),error);
    }

    @Test
    void testReadTeam() {
        Team teamEntity = createDeafultTeam() ;

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", teamEntity.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamResponseDTO> response = client.toBlocking().exchange(request, TeamResponseDTO.class);

        assertEntityDTOEqual(teamEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadTeamNotFound() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Team.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindAllTeams() {

        Team teamEntity = createDeafultTeam();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(TeamResponseDTO.class));

        assertEntityDTOEqual(Set.of(teamEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByName() {

        Team teamEntity = createDeafultTeam() ;

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s", teamEntity.getName())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(TeamResponseDTO.class));

        assertEntityDTOEqual(Set.of(teamEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Team teamEntity = createDeafultTeam();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", teamMemberEntity.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(TeamResponseDTO.class));

        assertEntityDTOEqual(Set.of(teamEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindTeams() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Team teamEntity = createDeafultTeam();

        TeamMember teamMemberEntity = createDeafultTeamMember(teamEntity, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?name=%s&memberid=%s", teamEntity.getName(),
                teamMemberEntity.getMemberid())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<TeamResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(TeamResponseDTO.class));

        assertEntityDTOEqual(Set.of(teamEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdateTeam() {
        Team teamEntity = createDeafultTeam();
        MemberProfile memberProfile = createADefaultMemberProfile();

        TeamUpdateDTO requestBody = updateFromEntity(teamEntity);
        requestBody.setTeamMembers(Collections.singletonList(createDefaultTeamMemberDto(teamEntity, memberProfile)));

        final HttpRequest<TeamUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<TeamResponseDTO> response = client.toBlocking().exchange(request, TeamResponseDTO.class);

        assertEntityDTOEqual(teamEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), teamEntity.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateAnInvalidTeam() {
        Team teamEntity = createDeafultTeam();

        TeamUpdateDTO requestBody = new TeamUpdateDTO(teamEntity.getId(), null, null);
        requestBody.setTeamMembers(new ArrayList<>());

        final HttpRequest<TeamUpdateDTO> request = HttpRequest.PUT("", requestBody)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("team.name: must not be blank", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullTeam() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [team] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }


    @Test
    void testUpdateTeamThrowException() {
        Team teamEntity = createDeafultTeam();
        UUID requestId = UUID.randomUUID();
        TeamUpdateDTO requestBody = new TeamUpdateDTO(requestId.toString(), teamEntity.getName(), teamEntity.getDescription());
        requestBody.setTeamMembers(new ArrayList<>());

        final MutableHttpRequest<TeamUpdateDTO> request = HttpRequest.PUT("", requestBody)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Team %s does not exist, can't update.", requestId),error);
        assertEquals(request.getPath(), href);

    }

    @Test
    void deleteTeamByMember() {
        // setup team
        Team teamEntity = createDeafultTeam();
        // create members
        MemberProfile memberProfileofTeamLeadEntity = createADefaultMemberProfile();
        MemberProfile memberProfileOfTeamMember = createADefaultMemberProfileForPdl(memberProfileofTeamLeadEntity);
        //add members to team
        createLeadTeamMember(teamEntity, memberProfileofTeamLeadEntity);
        createDeafultTeamMember(teamEntity, memberProfileOfTeamMember);

        final MutableHttpRequest<?> request =  HttpRequest.DELETE(String.format("/%s", teamEntity.getId())).basicAuth(memberProfileOfTeamMember.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
        () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        assertEquals("You are not authorized to perform this operation", errors.asText());
        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    void deleteTeamByAdmin() {
        // setup team
        Team teamEntity = createDeafultTeam();
        // create members
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        //add members to team
        createDeafultTeamMember(teamEntity, memberProfileOfAdmin);

        final MutableHttpRequest<?> request =  HttpRequest.DELETE(String.format("/%s", teamEntity.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteTeamByTeamLead() {
        // setup team
        Team teamEntity = createDeafultTeam();
        // create members
        MemberProfile memberProfileofTeamLeadEntity = createADefaultMemberProfile();
        //add members to team
        createLeadTeamMember(teamEntity, memberProfileofTeamLeadEntity);
        // createDeafultTeamMember(team, memberProfileOfTeamMember);

        final MutableHttpRequest<?> request =  HttpRequest.DELETE(String.format("/%s", teamEntity.getId())).basicAuth(memberProfileofTeamLeadEntity.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteTeamByUnrelatedUser() {
        // setup team
        Team teamEntity = createDeafultTeam();
        // create members
        MemberProfile user = createAnUnrelatedUser();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", teamEntity.getId())).basicAuth(user.getWorkEmail(), MEMBER_ROLE);

        //throw error
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
        () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        assertEquals("You are not authorized to perform this operation", errors.asText());
        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    private void assertEntityDTOEqual(Collection<Team> entities, Collection<TeamResponseDTO> dtos) {
        assertEquals(entities.size(), dtos.size());
        Iterator<Team> iEntity = entities.iterator();
        Iterator<TeamResponseDTO> iDTO = dtos.iterator();
        while (iEntity.hasNext() && iDTO.hasNext()) {
            assertEntityDTOEqual(iEntity.next(), iDTO.next());
        }
    }

    private void assertEntityDTOEqual(Team entity, TeamResponseDTO dto) {
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getDescription(), dto.getDescription());
    }
}

package com.objectcomputing.checkins.services.teammembers;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest
public class TeamMemberControllerTest {

    @Inject
    TeamMemberServices mockTeamMemberServices = mock(TeamMemberServices.class);

    @MockBean(TeamMemberServices.class)
    public TeamMemberServices roleServices() {
        return mock(TeamMemberServices.class);
    }

    TeamMemberDTO mockTeamMember = mock(TeamMemberDTO.class);

    private static UUID testTeamMemberId = UUID.fromString("44bd3ea6-ade7-428d-9630-e7e701f67d85");
    private static UUID testTeamId = UUID.fromString("cdcd949c-d2cb-4f6a-b13b-08ed4836c608");
    private static UUID testUuid = UUID.fromString("73e754f8-ae11-4467-acfc-20da64295d5b");
    private static boolean isLead = false;

    @Inject
    @Client("/services/team-member")
    private HttpClient client;

    @BeforeEach
    void setup() {
        reset(mockTeamMemberServices);
        reset(mockTeamMember);
    }

    @Test
    public void testPOSTValidator() {
        TeamMemberDTO invalid = new TeamMemberDTO(null, null, testTeamMemberId, isLead);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("", invalid).basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals("teamMember.teamId: must not be null", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPUTValidator() {
        TeamMemberDTO invalid = new TeamMemberDTO(null, testTeamId, null, isLead);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", invalid).basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals("teamMember.memberId: must not be null", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testFindNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/99").basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By TeamMemberId - when no user data exists
    @Test
    public void testGetFindByTeamMemberIdReturnsEmptyBody() {

        UUID testTeamMemberId = UUID.randomUUID();

        //when(mockTeamMemberRepository.findByMemberId(testTeamMemberId)).thenReturn(result);
        when(mockTeamMemberServices.findByTeamAndMember(null, testTeamMemberId)).thenReturn(EMPTY_LIST);
        HttpRequest request = HttpRequest.GET(String.format("/?memberId=%s", testTeamMemberId))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<TeamMemberDTO> response = client.toBlocking().retrieve(request, Argument.listOf(TeamMemberDTO.class));

        assertEquals(0, response.size());
    }

    // Find By TeamMId - when no user data exists
    @Test
    public void testGetFindByTeamIdReturnsEmptyBody() {

        when(mockTeamMemberServices.findByTeamAndMember(testTeamId, null))
                .thenReturn(EMPTY_LIST);

        HttpRequest request = HttpRequest.GET(String.format("/?teamId=%s", testTeamId))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<TeamMemberDTO> response = client.toBlocking().retrieve(request, Argument.listOf(TeamMemberDTO.class));

        assertEquals(0, response.size());
    }

    // test Find All
    @Test
    public void testGetFindAll() {

        //setupTestData();
        when (mockTeamMemberServices.findByTeamAndMember(null, null))
                .thenReturn(List.of(new TeamMember(testUuid, testTeamId, testTeamMemberId, isLead)));
        HttpRequest requestFindAll = HttpRequest.GET("")
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<TeamMember> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, mockTeamMember.getClass()));
        assertTrue(responseFindAll.size() > 0);
    }

    // test Find By TeamMemberId
    @Test
    public void testGetFindByTeamMemberId() {

        //setupTestData();
        when(mockTeamMemberServices.findByTeamAndMember(null, testTeamMemberId))
                .thenReturn(List.of(new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead)));
        HttpRequest requestFindByTeamMemberId = HttpRequest.GET(String.format("/?memberId=%s", testTeamMemberId))
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        List<TeamMemberDTO> responseFindByName = client.toBlocking().retrieve(requestFindByTeamMemberId, Argument.listOf(TeamMemberDTO.class));

        assertEquals(1, responseFindByName.size());
        assertEquals(testTeamMemberId, responseFindByName.get(0).getMemberId());
        assertEquals(testTeamId, responseFindByName.get(0).getTeamId());
    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        TeamMemberDTO testTeamMember = new TeamMemberDTO(null, testTeamId, testTeamMemberId, isLead);

        when(mockTeamMemberServices.saveTeamMember(any(TeamMember.class)))
                .thenReturn(new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead));

        final HttpResponse<TeamMemberDTO> response = client.toBlocking().exchange(HttpRequest.POST("", testTeamMember)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE), TeamMemberDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertNotNull(response.body().getUuid());
        assertEquals(testTeamMemberId, response.body().getMemberId());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {
        TeamMemberDTO testTeamMemberPut = new TeamMemberDTO(testUuid, testTeamId, testTeamMemberId, true);
        when(mockTeamMemberServices.updateTeamMember(any()))
                .thenReturn(new TeamMember(testTeamId, testTeamMemberId, testUuid, true));
        final HttpResponse<TeamMemberDTO> responseFromPut = client.toBlocking().exchange(
                HttpRequest.PUT("", testTeamMemberPut).basicAuth(MEMBER_ROLE, MEMBER_ROLE), TeamMemberDTO.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatus());
        assertNotNull(responseFromPut.body());
        assertEquals(testUuid, responseFromPut.body().getUuid());
        assertEquals(true, responseFromPut.body().isLead());
    }

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        TeamMember testTeamMember = new TeamMember();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testTeamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Request with invalid body - missing ID
    @Test
    public void testPutUpdateWithMissingField() {

        TeamMember TeamMember = new TeamMember(testTeamId, testTeamMemberId, isLead);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", TeamMember).basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

}

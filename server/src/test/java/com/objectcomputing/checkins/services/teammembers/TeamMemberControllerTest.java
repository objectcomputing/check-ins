package com.objectcomputing.checkins.services.teammembers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileController;

import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamController;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest
public class TeamMemberControllerTest {

    @Inject
    @Client("/services/team-member")
    private HttpClient client;

    @Inject
    MemberProfileController memberProfileController;

    @Inject
    TeamController teamController;

    TeamMemberRepository mockTeamMemberRepository = mock(TeamMemberRepository.class);
    TeamMember mockTeamMember = mock(TeamMember.class);

    private static UUID testId;
    private static UUID testTeamMemberId;
    private static UUID testTeamId;
    private static boolean isLead = false;
    private static boolean isDataSetupForTest = false;

    @BeforeAll
    void setupMemberProfileRecord() {
    
        // setup a record in Member-Profile to satisfy foreign key constraint
        if(memberProfileController != null) {
            MemberProfile testMemberProfile = new MemberProfile("TestName", 
                                                                "TestRole", 
                                                                UUID.randomUUID(), 
                                                                "TestLocation", 
                                                                "TestEmail", 
                                                                "TestInsperityId", 
                                                                LocalDate.now(), 
                                                                "TestBio");

            final HttpResponse<?> response = memberProfileController.save(testMemberProfile);
            assertEquals(HttpStatus.CREATED, response.getStatus());
            assertNotNull(response.body());
            testTeamMemberId = ((MemberProfile) response.body()).getUuid();
        }
    
        // setup a record in Team to satisfy foreign key constraint
        if(teamController != null) {
            Team testTeam = new Team("TestTeam", "TestDescription");

            final HttpResponse<?> response = teamController.save(testTeam);
            assertEquals(HttpStatus.CREATED, response.getStatus());
            assertNotNull(response.body());
            testTeamId = ((Team) response.body()).getUuid();
        }
    }

    @BeforeEach
    void setup() {
        reset(mockTeamMemberRepository);
        reset(mockTeamMember);
    }

    @Test
    public void testFindNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/99"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    // Find By TeamMemberId - when no user data exists
    @Test
    public void testGetFindByTeamMemberIdReturnsEmptyBody() {

        UUID testTeamMemberId = UUID.randomUUID();
        TeamMember teammember = new TeamMember();
        List<TeamMember> result = new ArrayList<TeamMember>();
        result.add(teammember);

        when(mockTeamMemberRepository.findByMemberId(testTeamMemberId)).thenReturn(result);

        HttpRequest request = HttpRequest.GET(String.format("/?memberId=%s", testTeamMemberId));
        List<TeamMember> response = client.toBlocking().retrieve(request, Argument.of(List.class, mockTeamMember.getClass()));

        assertEquals(0, response.size());
    }

    // Find By TeamMId - when no user data exists
    @Test
    public void testGetFindByTeamIdReturnsEmptyBody() {

        UUID testTeamId = UUID.randomUUID();
        TeamMember teammember = new TeamMember();
        List<TeamMember> result = new ArrayList<TeamMember>();
        result.add(teammember);

        when(mockTeamMemberRepository.findByMemberId(testTeamId)).thenReturn(result);

        HttpRequest request = HttpRequest.GET(String.format("/?teamId=%s", testTeamId));
        List<TeamMember> response = client.toBlocking().retrieve(request, Argument.of(List.class, mockTeamMember.getClass()));

        assertEquals(0, response.size());
    }

    // test Find All
    @Test
    public void testGetFindAll() {

        setupTestData();

        HttpRequest requestFindAll = HttpRequest.GET("");
        List<TeamMember> responseFindAll = client.toBlocking().retrieve(requestFindAll, Argument.of(List.class, mockTeamMember.getClass()));
        assertTrue(responseFindAll.size()>0);
    }

    // test Find By TeamMemberId
    @Test
    public void testGetFindByTeamMemberId() {

        setupTestData();

        HttpRequest requestFindByTeamMemberId = HttpRequest.GET(String.format("/?memberId=%s", testTeamMemberId));
        List<TeamMember> responseFindByName = client.toBlocking().retrieve(requestFindByTeamMemberId, Argument.of(List.class, mockTeamMember.getClass()));

        assertEquals(1, responseFindByName.size());
        assertEquals(testTeamMemberId, responseFindByName.get(0).getMemberId());
        assertEquals(testTeamId, responseFindByName.get(0).getTeamId());
    }

    // POST - Valid Body
    @Test
    public void testPostSave() {

        TeamMember testTeamMember = new TeamMember(testTeamId ,testTeamMemberId, isLead);

        final HttpResponse<TeamMember> response = client.toBlocking().exchange(HttpRequest.POST("", testTeamMember), TeamMember.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertNotNull(response.body().getUuid());
        assertEquals(testTeamMemberId, response.body().getMemberId());
    }

    // PUT - Valid Body
    @Test
    public void testPutUpdate() {

        setupTestData();

        TeamMember testTeamMemberPut = new TeamMember(testTeamId ,testTeamMemberId, true);
        testTeamMemberPut.setUuid(testId);

        final HttpResponse<TeamMember> responseFromPut = client.toBlocking().exchange(HttpRequest.PUT("", testTeamMemberPut), TeamMember.class);
        assertEquals(HttpStatus.OK, responseFromPut.getStatus());
        assertNotNull(responseFromPut.body());
        assertEquals(testId, responseFromPut.body().getUuid());
        assertEquals(true, responseFromPut.body().getIsLead());
    }

    // PUT - Request with empty body
    @Test
    public void testPutUpdateForEmptyInput() {
        TeamMember testTeamMember = new TeamMember();
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", testTeamMember));
        });
        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    // PUT - Request with invalid body - missing ID
    @Test
    public void testPutUpdateWithMissingField() {

        TeamMember TeamMember = new TeamMember(testTeamId ,testTeamMemberId, isLead);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("", TeamMember));
        });

        assertNotNull(thrown);
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    private void setupTestData() {
        if(!isDataSetupForTest) {
            TeamMember testTeamMember = new TeamMember(testTeamId ,testTeamMemberId, isLead);
            final HttpResponse<TeamMember> responseFromPost = client.toBlocking().exchange(HttpRequest.POST("", testTeamMember), TeamMember.class);

            assertEquals(HttpStatus.CREATED, responseFromPost.getStatus());
            assertNotNull(responseFromPost.body());
            testId = responseFromPost.body().getUuid();

            isDataSetupForTest = true;
        }
    }

}

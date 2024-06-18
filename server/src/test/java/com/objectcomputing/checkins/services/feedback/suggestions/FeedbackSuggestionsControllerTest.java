package com.objectcomputing.checkins.services.feedback.suggestions;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackSuggestionsControllerTest extends TestContainersSuite implements MemberProfileFixture, TeamMemberFixture, TeamFixture, FeedbackFixture, RoleFixture {
    private static final Logger LOG = LoggerFactory.getLogger(FeedbackSuggestionsControllerTest.class);
    private final String supervisorReason = "Supervisor of requestee";
    private final String teamLeadReason = "Team lead for requestee";
    private final String teamMemberReason = "Team member for requestee";
    private final String pdlReason = "PDL of requestee";


    @Inject
    @Client("/services/feedback/suggestions")
    HttpClient client;

    void assertContentEqualsEntity(FeedbackSuggestionDTO ideal, FeedbackSuggestionDTO actualResponse) {
        assertEquals(ideal.getReason(), actualResponse.getReason());
        assertEquals(ideal.getId(), actualResponse.getId());
    }
    @Test
    void testGetRecsIfPdl() {
        Team team = createDefaultTeam();
        MemberProfile pdlProfile = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, pdlProfile);
        MemberProfile supervisor = createADefaultSupervisor();
        createAndAssignRole(RoleType.ADMIN, supervisor);
        MemberProfile requestee = createAProfileWithSupervisorAndPDL(supervisor, pdlProfile);
        MemberProfile requesteeTeamLead = createAnUnrelatedUser();
        TeamMember requesteeTeamLeadMember = createLeadTeamMember(team, requesteeTeamLead);
        TeamMember requesteeTeamMember = createDefaultTeamMember(team, requestee);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", requestee.getId()))
                .basicAuth(pdlProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackSuggestionDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackSuggestionDTO.class));


        FeedbackSuggestionDTO idealOne = createFeedbackSuggestion(supervisorReason, supervisor.getId());
        FeedbackSuggestionDTO idealTwo = createFeedbackSuggestion(teamLeadReason, requesteeTeamLead.getId());

        assertNotNull(response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getBody().get().size());
        assertContentEqualsEntity(idealOne, response.getBody().get().get(0));
        assertContentEqualsEntity(idealTwo, response.getBody().get().get(1));

    }
    @Test
    void testGetRecsIfSupervisor() {
        Team team = createDefaultTeam();
        MemberProfile pdlProfile = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, pdlProfile);
        MemberProfile supervisor = createADefaultSupervisor();
        createAndAssignRole(RoleType.ADMIN, supervisor);
        MemberProfile requestee = createAProfileWithSupervisorAndPDL(supervisor, pdlProfile);
        MemberProfile requesteeTeamLead = createAnUnrelatedUser();
        TeamMember requesteeTeamLeadMember = createLeadTeamMember(team, requesteeTeamLead);
        TeamMember requesteeTeamMember = createDefaultTeamMember(team, requestee);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", requestee.getId()))
                .basicAuth(supervisor.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<FeedbackSuggestionDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackSuggestionDTO.class));


        FeedbackSuggestionDTO idealOne = createFeedbackSuggestion(pdlReason, pdlProfile.getId());
        FeedbackSuggestionDTO idealTwo = createFeedbackSuggestion(teamLeadReason, requesteeTeamLead.getId());

        assertNotNull(response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getBody().get().size());
        assertContentEqualsEntity(idealOne, response.getBody().get().get(0));
        assertContentEqualsEntity(idealTwo, response.getBody().get().get(1));


    }

    @Test
    void testGetRecsIfTeamLead() {
        Team team = createDefaultTeam();
        MemberProfile pdlProfile = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, pdlProfile);
        MemberProfile supervisor = createADefaultSupervisor();
        createAndAssignRole(RoleType.ADMIN, supervisor);
        MemberProfile requestee = createAProfileWithSupervisorAndPDL(supervisor, pdlProfile);
        MemberProfile requesteeTeamLead = createAnUnrelatedUser();
        TeamMember requesteeTeamLeadMember = createLeadTeamMember(team, requesteeTeamLead);
        TeamMember requesteeTeamMember = createDefaultTeamMember(team, requestee);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", requestee.getId()))
                .basicAuth(requesteeTeamLead.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackSuggestionDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackSuggestionDTO.class));


        FeedbackSuggestionDTO idealOne = createFeedbackSuggestion(supervisorReason, supervisor.getId());
        FeedbackSuggestionDTO idealTwo = createFeedbackSuggestion(pdlReason, pdlProfile.getId());

        assertNotNull(response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getBody().get().size());
        assertContentEqualsEntity(idealOne, response.getBody().get().get(0));
        assertContentEqualsEntity(idealTwo, response.getBody().get().get(1));

    }

    @Test
    void testGetRecsIfTeamMember() {
        Team team = createDefaultTeam();
        MemberProfile pdlProfile = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, pdlProfile);
        MemberProfile supervisor = createADefaultSupervisor();
        createAndAssignRole(RoleType.ADMIN, supervisor);
        MemberProfile requestee = createAProfileWithSupervisorAndPDL(supervisor, pdlProfile);
        MemberProfile requesteeTeamLead = createAnUnrelatedUser();
        MemberProfile teamMemberofRequestee = createASecondDefaultMemberProfile();
        createLeadTeamMember(team, requesteeTeamLead);
        createDefaultTeamMember(team, requestee);
        createDefaultTeamMember(team, teamMemberofRequestee);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", requestee.getId()))
                .basicAuth(teamMemberofRequestee.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackSuggestionDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackSuggestionDTO.class));


        FeedbackSuggestionDTO idealOne = createFeedbackSuggestion(supervisorReason, supervisor.getId());
        FeedbackSuggestionDTO idealTwo = createFeedbackSuggestion(pdlReason, pdlProfile.getId());
        FeedbackSuggestionDTO idealThree = createFeedbackSuggestion(teamLeadReason, requesteeTeamLead.getId());

        assertNotNull(response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals( 3, response.getBody().get().size() );
        assertContentEqualsEntity(idealOne, response.getBody().get().get(0));
        assertContentEqualsEntity(idealTwo, response.getBody().get().get(1));
        assertContentEqualsEntity(idealThree, response.getBody().get().get(2));

    }

    @Test
    void testDoesNotIncludeTerminatedTeamMembers() {
        Team team = createDefaultTeam();
        MemberProfile pdlProfile = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, pdlProfile);
        MemberProfile supervisor = createADefaultSupervisor();
        createAndAssignRole(RoleType.ADMIN, supervisor);
        MemberProfile requestee = createAProfileWithSupervisorAndPDL(supervisor, pdlProfile);
        MemberProfile requesteeTeamLead = createAnUnrelatedUser();
        MemberProfile teamMemberofRequestee = createASecondDefaultMemberProfile();
        MemberProfile termedTeamMemberofRequestee = createAPastTerminatedMemberProfile();
        createLeadTeamMember(team, requesteeTeamLead);
        createDefaultTeamMember(team, requestee);
        createDefaultTeamMember(team, teamMemberofRequestee);
        createDefaultTeamMember(team, termedTeamMemberofRequestee);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", requestee.getId()))
                .basicAuth(teamMemberofRequestee.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackSuggestionDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackSuggestionDTO.class));


        FeedbackSuggestionDTO idealOne = createFeedbackSuggestion(supervisorReason, supervisor.getId());
        FeedbackSuggestionDTO idealTwo = createFeedbackSuggestion(pdlReason, pdlProfile.getId());
        FeedbackSuggestionDTO idealThree = createFeedbackSuggestion(teamLeadReason, requesteeTeamLead.getId());

        assertNotNull(response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals( 3, response.getBody().get().size() );
        assertContentEqualsEntity(idealOne, response.getBody().get().get(0));
        assertContentEqualsEntity(idealTwo, response.getBody().get().get(1));
        assertContentEqualsEntity(idealThree, response.getBody().get().get(2));
    }
}
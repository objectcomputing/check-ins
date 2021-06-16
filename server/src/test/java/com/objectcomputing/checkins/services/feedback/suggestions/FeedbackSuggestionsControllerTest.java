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
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackSuggestionsControllerTest extends TestContainersSuite implements MemberProfileFixture, TeamMemberFixture, TeamFixture, FeedbackFixture, RoleFixture {
    private static final Logger LOG = LoggerFactory.getLogger(FeedbackSuggestionsControllerTest.class);

    @Inject
    @Client("/services/feedback")
    HttpClient client;

    @Test
    void testGetRecsIfPdl() {
        Team team = createDefaultTeam();
        MemberProfile pdlProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlProfile);
        MemberProfile supervisor = createADefaultSupervisor();
        createDefaultRole(RoleType.ADMIN, supervisor);
        MemberProfile requestee = createASupervisedAndPDLUser(supervisor, pdlProfile);
        MemberProfile requesteeTeamLead = createAnUnrelatedUser();
        TeamMember requesteeTeamLeadMember = createLeadTeamMember(team, requesteeTeamLead);
        TeamMember requesteeTeamMember = createDefaultTeamMember(team, requestee);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", pdlProfile.getId()))
                .basicAuth(pdlProfile.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<FeedbackSuggestionDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackSuggestionDTO.class));



        assertNotNull(response.getBody().get());

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.getBody().get().size(), 2 );
        assertContentEqualsEntity(idealOne, response.getBody().get().get(0));
        assertContentEqualsEntity(idealTwo, response.getBody().get().get(1));

    }
    @Test
    void testGetRecsIfSupervisor() {
        Team team = createDefaultTeam();
        MemberProfile pdlProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlProfile);
        MemberProfile supervisor = createADefaultSupervisor();
        createDefaultRole(RoleType.ADMIN, supervisor);
        MemberProfile requestee = createASupervisedAndPDLUser(supervisor, pdlProfile);
        MemberProfile requesteeTeamLead = createAnUnrelatedUser();
        TeamMember requesteeTeamLeadMember = createLeadTeamMember(team, requesteeTeamLead);
        TeamMember requesteeTeamMember = createDefaultTeamMember(team, requestee);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", requestee.getId()))
                .basicAuth(supervisor.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<FeedbackSuggestionDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackSuggestionDTO.class));


        FeedbackSuggestionDTO idealOne = createFeedbackSuggestion(pdlReason, pdlProfile.getId());
        FeedbackSuggestionDTO idealTwo = createFeedbackSuggestion(teamLeadReason, requesteeTeamLead.getId());

        assertNotNull(JSON.toString(response.getBody().get()));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.getBody().get().size(), 2 );
        assertContentEqualsEntity(idealOne, response.getBody().get().get(0));
        assertContentEqualsEntity(idealTwo, response.getBody().get().get(1));


    }

    @Test
    void testGetRecsIfTeamLead() {

    }

    @Test
    void testGetRecsIfTeamMember() {

    }

    @Test
    void testGetRecsIfIdNull() {

    }


}
package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestController;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.lang.reflect.Member;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;

public class FeedbackRequestControllerTest extends TestContainersSuite implements RepositoryFixture, MemberProfileFixture, FeedbackRequestFixture, RoleFixture {
    @Inject
    @Client("/services/feedback/request")
    HttpClient client;

    @Test
    void testCreateFeedbackRequestByAdmin() {
        //create two member profiles: one for normal employee, one for pdl of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = createDefaultRole(RoleType.PDL, memberProfileForPDL);

        //create feedebakc request


        //send feedback request

        //assert that content of some feedback request equals the test


    }

    @Test
    void testCreateFeedbackRequestByAssignedPdl() {

    }

    @Test
    void testCreateFeedbackRequestByUnassignedPdl() {

    }

    @Test
    void testCreateFeedbackRequestByMember() {

    }


}

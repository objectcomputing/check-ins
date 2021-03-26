package com.objectcomputing.checkins.services.feedback;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.FeedbackFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;

public class FeedbackControllerTest extends TestContainersSuite implements FeedbackFixture {

    @Inject
    @Client("/services/feedback")
    private HttpClient client;

    @Test
    public void testGetSucceedAdmin() {
        final MemberProfile from = mkMemberProfile("1");
        final MemberProfile to = mkMemberProfile("2");
        final Feedback feedback = createFeedback("Some constructive feedback", to, from, false);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s")).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
    }

    @Test
    public void testGetSucceedNotAdminPublicFeedback() {

    }

    @Test
    public void testGetSucceedNotAdminPrivateFeedback() {

    }

    @Test
    public void testGetIdNotFound() {

    }

    @Test
    public void testGetPermissionDenied() {

    }

    @Test
    public void testGetNullId() {

    }

    @Test
    public void testFindAdmin() {

    }

    @Test
    public void testFindBySentBy() {

    }

    @Test
    public void testFindBySentTo() {

    }

    @Test
    public void testFindByConfidential() {

    }

    @Test
    public void testFindAll() {

    }

    @Test
    public void testFindEmptyResponseBody() {

    }

    @Test
    public void testPostSucceed() {

    }

    @Test
    public void testPostValidationFailed() {

    }

    @Test
    public void testPutSucceedAdmin() {

    }

    @Test
    public void testPutSucceedOwner() {

    }

    @Test
    public void testPutIdNotFound() {

    }

    @Test
    public void testPutPermissionDenied() {

    }

    @Test
    public void testDeleteSucceedAdmin() {

    }

    @Test
    public void testDeleteSucceedOwner() {

    }

    @Test
    public void testDeleteIdNotFound() {

    }

    @Test
    public void testDeletePermissionDenied() {

    }
}

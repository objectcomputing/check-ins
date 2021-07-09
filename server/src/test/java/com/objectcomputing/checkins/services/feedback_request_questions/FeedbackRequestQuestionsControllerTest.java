package com.objectcomputing.checkins.services.feedback_request_questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestCreateDTO;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestResponseDTO;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeedbackRequestQuestionsControllerTest extends TestContainersSuite implements RepositoryFixture, FeedbackRequestFixture, FeedbackRequestQuestionFixture, MemberProfileFixture, RoleFixture {
    @Inject
    @Client("/services/feedback/requests/questions")
    HttpClient client;

    private FeedbackRequest createSampleFeedbackRequest(MemberProfile pdlMember, MemberProfile requestee, MemberProfile recipient) {
        createDefaultRole(RoleType.PDL, pdlMember);
        return createFeedbackRequest(pdlMember, requestee, recipient);
    }
    private void assertUnauthorized(HttpClientResponseException responseException) {
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
    private void assertResponseEqualsEntity(FeedbackRequestQuestion feedbackRequestQ, FeedbackRequestQuestionResponseDTO dto) {
        if (feedbackRequestQ == null || dto == null) {
            assertEquals(feedbackRequestQ, dto);
        } else {
            assertEquals(feedbackRequestQ.getId(), dto.getId());
            assertEquals(feedbackRequestQ.getRequestId(), dto.getRequestId());
            assertEquals(feedbackRequestQ.getQuestionContent(), dto.getQuestionContent());
            assertEquals(feedbackRequestQ.getAnswerContent(), dto.getAnswerContent());
            assertEquals(feedbackRequestQ.getOrderNum(), dto.getOrderNum());

        }
    }

    @Test
    void testSaveByCreator() {
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest req = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(req.getId());
        final HttpRequest<?> request = HttpRequest.POST("", questionOne)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestQuestionResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
       assertResponseEqualsEntity(questionOne, response.getBody().get());

    }

    @Test
    void testSaveUnauthorized() {

    }

    @Test
    void testSaveAdmin() {

    }

    @Test
    void testSaveRequestDoesNotExist(){

    }
}

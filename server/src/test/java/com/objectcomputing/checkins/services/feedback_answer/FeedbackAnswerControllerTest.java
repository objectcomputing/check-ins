package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request_questions.FeedbackRequestQuestion;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackAnswerControllerTest extends TestContainersSuite implements FeedbackAnswerFixture, MemberProfileFixture, RoleFixture, FeedbackRequestFixture, FeedbackRequestQuestionFixture {

    @Inject
    @Client("/services/feedback/answers")
    HttpClient client;

    public FeedbackAnswerCreateDTO saveSampleAnswer(MemberProfile sender, MemberProfile recipient) {
        MemberProfile requestee = createAnUnrelatedUser();
        FeedbackRequest feedbackRequest = createFeedbackRequest(sender, requestee, recipient);
        FeedbackRequestQuestion question = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());

        FeedbackAnswer answer = createFeedbackAnswer(question.getId());
        return createDTO(answer);
    }

    public void assertContentEqualsResponse(FeedbackAnswerCreateDTO content, FeedbackAnswerResponseDTO response) {
        assertEquals(content.getAnswer(), response.getAnswer());
        assertEquals(content.getQuestionId(), response.getQuestionId());
        assertEquals(content.getSentiment(), response.getSentiment());
    }

    @Test
    void testPostAnswerByAdmin() {
        MemberProfile admin = createADefaultMemberProfile();
        createDefaultAdminRole(admin);

        MemberProfile sender = createASecondDefaultMemberProfile();
        MemberProfile recipient = createAThirdDefaultMemberProfile();

        FeedbackAnswerCreateDTO dto = saveSampleAnswer(sender, recipient);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not authorized to do this operation", exception.getMessage());
    }

    @Test
    void testPostAnswerByRecipient() {
        MemberProfile sender = createASecondDefaultMemberProfile();
        MemberProfile recipient = createAThirdDefaultMemberProfile();

        FeedbackAnswerCreateDTO dto = saveSampleAnswer(sender, recipient);
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackAnswerResponseDTO> response = client.toBlocking().exchange(request, FeedbackAnswerResponseDTO.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsResponse(dto, response.getBody().get());
    }

    FeedbackAnswerCreateDTO createDTO(FeedbackAnswer feedbackAnswer) {
        FeedbackAnswerCreateDTO dto = new FeedbackAnswerCreateDTO();
        dto.setAnswer(feedbackAnswer.getAnswer());
        dto.setQuestionId(feedbackAnswer.getQuestionId());
        dto.setSentiment(feedbackAnswer.getSentiment());
        return dto;
    }

}

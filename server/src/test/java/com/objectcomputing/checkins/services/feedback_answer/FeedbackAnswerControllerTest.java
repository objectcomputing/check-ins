package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
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

public class FeedbackAnswerControllerTest extends TestContainersSuite implements FeedbackAnswerFixture, MemberProfileFixture, RoleFixture, FeedbackRequestFixture, FeedbackTemplateFixture, TemplateQuestionFixture {

    @Inject
    @Client("/services/feedback/answers")
    HttpClient client;

    public FeedbackAnswer createSampleAnswer(MemberProfile sender, MemberProfile recipient) {
        createDefaultRole(RoleType.PDL, sender);
        MemberProfile requestee = createADefaultMemberProfileForPdl(sender);
        MemberProfile templateCreator = createADefaultSupervisor();
        FeedbackTemplate template = createFeedbackTemplate(templateCreator.getId());
        getFeedbackTemplateRepository().save(template);
        TemplateQuestion question = saveTemplateQuestion(template, 1);
        FeedbackRequest feedbackRequest = saveSampleFeedbackRequest(sender, requestee, recipient, template.getId());
        return createSampleFeedbackAnswer(question.getId(), feedbackRequest.getId());
    }

    public FeedbackAnswer saveSampleAnswer(MemberProfile sender, MemberProfile recipient) {
        FeedbackAnswer answer = createSampleAnswer(sender, recipient);
        return getFeedbackAnswerRepository().save(answer);
    }

    FeedbackAnswerCreateDTO createDTO(FeedbackAnswer feedbackAnswer) {
        FeedbackAnswerCreateDTO dto = new FeedbackAnswerCreateDTO();
        dto.setAnswer(feedbackAnswer.getAnswer());
        dto.setQuestionId(feedbackAnswer.getQuestionId());
        dto.setRequestId(feedbackAnswer.getRequestId());
        dto.setSentiment(feedbackAnswer.getSentiment());
        return dto;
    }

    FeedbackAnswerUpdateDTO updateDTO(FeedbackAnswer feedbackAnswer) {
        FeedbackAnswerUpdateDTO dto = new FeedbackAnswerUpdateDTO();
        dto.setId(feedbackAnswer.getId());
        dto.setAnswer(feedbackAnswer.getAnswer());
        dto.setSentiment(feedbackAnswer.getSentiment());
        return dto;
    }

    public void assertContentEqualsResponse(FeedbackAnswer content, FeedbackAnswerResponseDTO response) {
        assertEquals(content.getAnswer(), response.getAnswer());
        assertEquals(content.getQuestionId(), response.getQuestionId());
        assertEquals(content.getSentiment(), response.getSentiment());
    }

    public void assertUnauthorized(HttpClientResponseException exception) {
        assertEquals("You are not authorized to do this operation", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testPostAnswerByAdminUnauthorized() {
        MemberProfile admin = createADefaultMemberProfile();
        createDefaultAdminRole(admin);

        MemberProfile sender = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswerCreateDTO dto = createDTO(createSampleAnswer(sender, recipient));

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    void testPostAnswerByRecipient() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = createSampleAnswer(sender, recipient);
        FeedbackAnswerCreateDTO dto = createDTO(feedbackAnswer);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackAnswerResponseDTO> response = client.toBlocking().exchange(request, FeedbackAnswerResponseDTO.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsResponse(feedbackAnswer, response.getBody().get());
    }

    @Test
    void testPostBySenderUnauthorized() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = createSampleAnswer(sender, recipient);
        FeedbackAnswerCreateDTO dto = createDTO(feedbackAnswer);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(sender.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    void testUpdateByRecipient() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = saveSampleAnswer(sender, recipient);
        feedbackAnswer.setAnswer(":p");
        feedbackAnswer.setSentiment(1.0);
        FeedbackAnswerUpdateDTO dto = updateDTO(feedbackAnswer);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackAnswerResponseDTO> response = client.toBlocking()
                .exchange(request, FeedbackAnswerResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(feedbackAnswer, response.getBody().get());
    }

    @Test
    void testUpdateByAdminUnauthorized() {
        MemberProfile admin = createADefaultMemberProfile();
        createDefaultAdminRole(admin);
        MemberProfile sender = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = saveSampleAnswer(sender, recipient);
        FeedbackAnswerUpdateDTO dto = updateDTO(feedbackAnswer);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    void testGetByIdSender() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = saveSampleAnswer(sender, recipient);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", feedbackAnswer.getId()))
                .basicAuth(sender.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackAnswerResponseDTO> response = client.toBlocking().exchange(request, FeedbackAnswerResponseDTO.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsResponse(feedbackAnswer, response.getBody().get());
    }

    @Test
    void testGetByIdSubmitter() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = saveSampleAnswer(sender, recipient);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", feedbackAnswer.getId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackAnswerResponseDTO> response = client.toBlocking().exchange(request, FeedbackAnswerResponseDTO.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsResponse(feedbackAnswer, response.getBody().get());
    }

    @Test
    void testGetByIdUnauthorized() {
        MemberProfile random = createADefaultMemberProfile();
        MemberProfile sender = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = saveSampleAnswer(sender, recipient);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", feedbackAnswer.getId()))
                .basicAuth(random.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    void testGetByRequestAndQuestionIdAuthorized() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = saveSampleAnswer(sender, recipient);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?questionId=%s&requestId=%s", feedbackAnswer.getQuestionId(), feedbackAnswer.getRequestId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackAnswerResponseDTO> response = client.toBlocking().exchange(request, FeedbackAnswerResponseDTO.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsResponse(feedbackAnswer, response.getBody().get());
    }

    @Test
    void testGetByRequestAndQuestionIdUnauthorized() {

    }

    @Test
    void testGetByRequestAndQuestionIdRequestNotExists() {

    }

    @Test
    void testGetByRequestAndQuestionIdNull() {

    }
}

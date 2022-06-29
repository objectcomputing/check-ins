package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.fixture.FeedbackAnswerFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.TemplateQuestionFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeedbackAnswerControllerTest extends TestContainersSuite implements FeedbackAnswerFixture, MemberProfileFixture, RoleFixture, FeedbackRequestFixture, FeedbackTemplateFixture, TemplateQuestionFixture {

    @Inject
    @Client("/services/feedback/answers")
    HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    public FeedbackAnswer createSampleAnswer(MemberProfile sender, MemberProfile recipient) {
        assignPdlRole(sender);
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
        assignAdminRole(admin);

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
    void testPostAnswerByRecipientForCanceledRequest() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignPdlRole(sender);
        MemberProfile requestee = createADefaultMemberProfileForPdl(sender);

        MemberProfile templateCreator = createADefaultSupervisor();
        FeedbackTemplate template = createFeedbackTemplate(templateCreator.getId());
        getFeedbackTemplateRepository().save(template);

        TemplateQuestion question = saveTemplateQuestion(template, 1);
        FeedbackRequest canceledRequest = saveSampleFeedbackRequestWithStatus(sender, requestee, recipient, template.getId(), "canceled");

        FeedbackAnswer feedbackAnswer = createSampleFeedbackAnswer(question.getId(), canceledRequest.getId());
        FeedbackAnswerCreateDTO dto = createDTO(feedbackAnswer);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Attempted to save an answer for a canceled feedback request", responseException.getMessage());
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
        assignAdminRole(admin);
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
        final HttpResponse<List<FeedbackAnswerResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackAnswerResponseDTO.class));

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsResponse(feedbackAnswer, response.getBody().get().get(0));
    }

    @Test
    void testGetByRequestAndQuestionIdAuthorizedRequestOnly() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignPdlRole(sender);
        MemberProfile requestee = createADefaultMemberProfileForPdl(sender);
        MemberProfile templateCreator = createADefaultSupervisor();
        FeedbackTemplate template = createFeedbackTemplate(templateCreator.getId());
        getFeedbackTemplateRepository().save(template);
        TemplateQuestion question = saveTemplateQuestion(template, 1);
        TemplateQuestion questionTwo = saveAnotherTemplateQuestion(template, 2);
        FeedbackRequest feedbackRequest = saveSampleFeedbackRequest(sender, requestee, recipient, template.getId());
        FeedbackAnswer answerOne = new FeedbackAnswer("Sample answer 1", question.getId(), feedbackRequest.getId(), 0.5);
        getFeedbackAnswerRepository().save(answerOne);
        FeedbackAnswer answerTwo = new FeedbackAnswer("Sample answer 2", questionTwo.getId(), feedbackRequest.getId(), 0.5);
        getFeedbackAnswerRepository().save(answerTwo);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?requestId=%s", answerOne.getRequestId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackAnswerResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackAnswerResponseDTO.class));

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsResponse(answerOne, response.getBody().get().get(0));
        assertContentEqualsResponse(answerTwo, response.getBody().get().get(1));
    }

    @Test
    void testGetByRequestAndQuestionIdUnauthorized() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile random = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackAnswer feedbackAnswer = saveSampleAnswer(sender, recipient);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?questionId=%s&requestId=%s", feedbackAnswer.getQuestionId(), feedbackAnswer.getRequestId()))
                .basicAuth(random.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        assertUnauthorized(exception);


    }

    @Test
    void testGetByRequestAndQuestionIdRequestNotExists() {
        MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        UUID random = UUID.randomUUID();
        FeedbackAnswer feedbackAnswer = saveSampleAnswer(sender, recipient);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?questionId=%s&requestId=%s", feedbackAnswer.getQuestionId(), random))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        assertEquals("Cannot find attached request for search", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }

}

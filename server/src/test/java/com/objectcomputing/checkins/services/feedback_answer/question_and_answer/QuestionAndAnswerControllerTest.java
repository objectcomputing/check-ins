package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionAndAnswerControllerTest extends TestContainersSuite implements FeedbackAnswerFixture, TemplateQuestionFixture, MemberProfileFixture, FeedbackTemplateFixture, FeedbackRequestFixture, RoleFixture {

    @Inject
    @Client("/services/feedback/questions-and-answers")
    public HttpClient client;

    public void assertTupleEqualsEntity(QuestionAndAnswerServices.Tuple actual, QuestionAndAnswerServices.Tuple response) {
        assertEquals(actual.getQuestion().getQuestion(), response.getQuestion().getQuestion());
        assertEquals(actual.getQuestion().getQuestionNumber(), response.getQuestion().getQuestionNumber());
        assertEquals(actual.getQuestion().getTemplateId(), response.getQuestion().getTemplateId());
        assertEquals(actual.getAnswer().getQuestionId(), response.getAnswer().getQuestionId());
        assertEquals(actual.getAnswer().getRequestId(), response.getAnswer().getRequestId());
        assertEquals(actual.getAnswer().getAnswer(), response.getAnswer().getAnswer());
        assertEquals(actual.getAnswer().getSentiment(), response.getAnswer().getSentiment());
        if (actual.getRequest() != null && response.getRequest() != null) {
            assertEquals(actual.getRequest().getRecipientId(), response.getRequest().getRecipientId());
            assertEquals(actual.getRequest().getCreatorId(), response.getRequest().getCreatorId());
            assertEquals(actual.getRequest().getRequesteeId(), response.getRequest().getRequesteeId());
            assertEquals(actual.getRequest().getTemplateId(), response.getRequest().getTemplateId());
            assertEquals(actual.getRequest().getDueDate(), response.getRequest().getDueDate());
            assertEquals(actual.getRequest().getSendDate(), response.getRequest().getSendDate());
            assertEquals(actual.getRequest().getStatus(), response.getRequest().getStatus());
            assertEquals(actual.getRequest().getSubmitDate(), response.getRequest().getSubmitDate());
        }

    }

    public QuestionAndAnswerServices.Tuple saveSampleTuple(MemberProfile sender, MemberProfile recipient) {
        MemberProfile requestee = createADefaultMemberProfileForPdl(sender);
        MemberProfile templateCreator = createADefaultSupervisor();
        FeedbackTemplate template = createFeedbackTemplate(templateCreator.getId());
        getFeedbackTemplateRepository().save(template);
        TemplateQuestion question = saveTemplateQuestion(template, 1);
        FeedbackRequest feedbackRequest = saveSampleFeedbackRequest(sender, requestee, recipient, template.getId());
        FeedbackAnswer answer = createSampleFeedbackAnswer(question.getId(), feedbackRequest.getId());
        getFeedbackAnswerRepository().save(answer);
        return new QuestionAndAnswerServices.Tuple(question, answer);
    }

    public QuestionAndAnswerServices.Tuple saveAnotherSampleTuple(MemberProfile sender, MemberProfile recipient) {
        MemberProfile requestee = createASecondDefaultMemberProfileForPdl(sender);
        MemberProfile templateCreator = createANewHireProfile();
        FeedbackTemplate template = createFeedbackTemplate(templateCreator.getId());
        getFeedbackTemplateRepository().save(template);
        TemplateQuestion question = saveAnotherTemplateQuestion(template, 2);
        FeedbackRequest feedbackRequest = saveSampleFeedbackRequest(sender, requestee, recipient, template.getId());
        FeedbackAnswer answer = createSampleFeedbackAnswer(question.getId(), feedbackRequest.getId());
        getFeedbackAnswerRepository().save(answer);
        return new QuestionAndAnswerServices.Tuple(question, answer);
    }

    @Test
    public void testGetExistingQuestionAndAnswerPermitted() {
        MemberProfile sender = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, sender);
        MemberProfile recipient = createADefaultRecipient();
        QuestionAndAnswerServices.Tuple tuple = saveSampleTuple(sender, recipient);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?questionId=%s&requestId=%s", tuple.getQuestion().getId(), tuple.getAnswer().getRequestId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<QuestionAndAnswerServices.Tuple> response = client.toBlocking()
                .exchange(request, QuestionAndAnswerServices.Tuple.class);
        assertTrue(response.getBody().isPresent());

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTupleEqualsEntity(tuple, response.getBody().get());
    }

    @Test
    public void testGetQuestionAndRequestWhereAnswerNotSavedPermitted() {
        final MemberProfile sender = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        createAndAssignRole(RoleType.PDL, sender);
        MemberProfile requestee = createADefaultMemberProfileForPdl(sender);
        MemberProfile templateCreator = createADefaultSupervisor();
        FeedbackTemplate template = createFeedbackTemplate(templateCreator.getId());
        getFeedbackTemplateRepository().save(template);
        TemplateQuestion question = saveAnotherTemplateQuestion(template, 2);
        FeedbackRequest feedbackRequest = saveSampleFeedbackRequest(sender, requestee, recipient, template.getId());
        FeedbackAnswer newAnswerObject = new FeedbackAnswer();
        newAnswerObject.setAnswer(null);
        newAnswerObject.setQuestionId(question.getId());
        newAnswerObject.setRequestId(feedbackRequest.getId());
        newAnswerObject.setSentiment(null);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?questionId=%s&requestId=%s", question.getId(), feedbackRequest.getId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<QuestionAndAnswerServices.Tuple> response = client.toBlocking()
                .exchange(request, QuestionAndAnswerServices.Tuple.class);
//????? now this seems to pass idk--i guess not maybe it is due to null feedback request??
        // the feedback request should be nullable within the tuple right?
        assertTrue(response.getBody().isPresent());
        assertTupleEqualsEntity(new QuestionAndAnswerServices.Tuple(question, newAnswerObject), response.getBody().get());
    }

    @Test
    public void testGetQuestionAndAnswerNotPermitted() {
        MemberProfile sender = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, sender);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile random = createAnUnrelatedUser();

        QuestionAndAnswerServices.Tuple tuple = saveSampleTuple(sender, recipient);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?questionId=%s&requestId=%s", tuple.getQuestion().getId(), tuple.getAnswer().getRequestId()))
                .basicAuth(random.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        assertEquals("You are not authorized to do this operation", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    public void testGetAllQuestionsAndAnswersPermitted() {
        MemberProfile sender = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, sender);
        MemberProfile recipient = createADefaultRecipient();
        QuestionAndAnswerServices.Tuple tuple = saveSampleTuple(sender, recipient);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", tuple.getAnswer().getRequestId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<QuestionAndAnswerServices.Tuple>> response = client.toBlocking()
                .exchange(request, Argument.listOf(QuestionAndAnswerServices.Tuple.class));
        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, response.getBody().get().size());
        assertTupleEqualsEntity(tuple, response.getBody().get().get(0));
    }

    @Test
    public void testGetAllQuestionsAndAnswersNotPermitted() {
        MemberProfile sender = createADefaultMemberProfile();
        createAndAssignRole(RoleType.PDL, sender);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile random = createAnUnrelatedUser();
        QuestionAndAnswerServices.Tuple tuple = saveSampleTuple(sender, recipient);
        saveAnotherSampleTuple(sender,recipient);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", tuple.getAnswer().getRequestId()))
                .basicAuth(random.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        assertEquals("You are not authorized to do this operation", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

}

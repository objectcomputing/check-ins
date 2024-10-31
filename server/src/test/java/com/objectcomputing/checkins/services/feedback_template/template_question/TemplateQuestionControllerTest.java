package com.objectcomputing.checkins.services.feedback_template.template_question;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.fixture.TemplateQuestionFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;
import java.util.*;

class TemplateQuestionControllerTest extends TestContainersSuite implements MemberProfileFixture, FeedbackTemplateFixture, TemplateQuestionFixture, RoleFixture {

    @Inject
    @Client("/services/feedback/template_questions")
    HttpClient client;

    TemplateQuestionCreateDTO createDTO(TemplateQuestion templateQuestion) {
        TemplateQuestionCreateDTO dto = new TemplateQuestionCreateDTO();
        dto.setQuestion(templateQuestion.getQuestion());
        dto.setTemplateId(templateQuestion.getTemplateId());
        dto.setQuestionNumber(templateQuestion.getQuestionNumber());
        dto.setInputType(templateQuestion.getInputType());
        return dto;
    }

    TemplateQuestionUpdateDTO updateDTO(TemplateQuestion templateQuestion) {
        TemplateQuestionUpdateDTO dto = new TemplateQuestionUpdateDTO();
        dto.setId(templateQuestion.getId());
        dto.setQuestion(templateQuestion.getQuestion());
        dto.setQuestionNumber(templateQuestion.getQuestionNumber());
        dto.setInputType(templateQuestion.getInputType());
        return dto;
    }

    void assertContentEqualsResponse(TemplateQuestion content, TemplateQuestionResponseDTO dto) {
        assertEquals(content.getQuestion(), dto.getQuestion());
        assertEquals(content.getTemplateId(), dto.getTemplateId());
        assertEquals(content.getQuestionNumber(), dto.getQuestionNumber());
        assertEquals(content.getInputType(), dto.getInputType());
    }

    @Test
    void testPostAuthorizedByTemplateCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate feedbackTemplate = saveFeedbackTemplate(memberOne.getId());

        final TemplateQuestion feedbackQuestion = createDefaultTemplateQuestion();
        feedbackQuestion.setTemplateId(feedbackTemplate.getId());
        final TemplateQuestionCreateDTO dto = createDTO(feedbackQuestion);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<TemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(feedbackQuestion, response.getBody().get());
    }

    @Test
    void testPostAuthorizedByAdmin() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile admin = createASecondDefaultMemberProfile();
        createAndAssignAdminRole(admin);

        final FeedbackTemplate feedbackTemplate = saveFeedbackTemplate(memberOne.getId());

        final TemplateQuestion feedbackQuestion = createDefaultTemplateQuestion();
        feedbackQuestion.setTemplateId(feedbackTemplate.getId());
        final TemplateQuestionCreateDTO dto = createDTO(feedbackQuestion);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<TemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(feedbackQuestion, response.getBody().get());
    }

    @Test
    void testPostUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();

        final FeedbackTemplate feedbackTemplate = saveFeedbackTemplate(memberOne.getId());

        final TemplateQuestion feedbackQuestion = createDefaultTemplateQuestion();
        feedbackQuestion.setTemplateId(feedbackTemplate.getId());
        final TemplateQuestionCreateDTO dto = createDTO(feedbackQuestion);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals(NOT_AUTHORIZED_MSG, exception.getMessage());
    }

    @Test
    void testPostDuplicateQuestionNumberNotAllowed() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());

        // Save a question as question number 1
        saveTemplateQuestion(template, 1);

        final TemplateQuestion question = createSecondDefaultTemplateQuestion();
        question.setQuestionNumber(1);
        question.setTemplateId(template.getId());
        final TemplateQuestionCreateDTO createDTO = createDTO(question);

        // Attempt to save another question, which is also question number 1
        final HttpRequest<?> request = HttpRequest.POST("", createDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Attempted to save question on template " + template.getId() + " with duplicate question number 1", exception.getMessage());
    }

    @Test
    void testPostOnNonexistentTemplate() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final UUID nonexistentTemplateId = UUID.randomUUID();
        final TemplateQuestion question = createDefaultTemplateQuestion();
        question.setTemplateId(nonexistentTemplateId);
        final TemplateQuestionCreateDTO createDTO = createDTO(question);

        final HttpRequest<?> request = HttpRequest.POST("", createDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Template ID " + nonexistentTemplateId + " does not exist", exception.getMessage());
    }

    @Test
    void testGetByIdAuthorizedByAdmin() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile admin = createASecondDefaultMemberProfile();
        createAndAssignAdminRole(admin);

        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveTemplateQuestion(template, 1);

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", question1.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<TemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(question1, response.getBody().get());
    }

    @Test
    void testGetByIdAuthorizedByTemplateCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveTemplateQuestion(template, 1);
        final TemplateQuestion question2 = saveAnotherTemplateQuestion(template, 2);

        final HttpRequest<?> request1 = HttpRequest.GET(String.format("%s", question1.getId()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<TemplateQuestionResponseDTO> response1 = client.toBlocking().exchange(request1, TemplateQuestionResponseDTO.class);

        final HttpRequest<?> request2 = HttpRequest.GET(String.format("%s", question2.getId()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<TemplateQuestionResponseDTO> response2 = client.toBlocking().exchange(request2, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.OK, response1.getStatus());
        assertTrue(response1.getBody().isPresent());
        assertContentEqualsResponse(question1, response1.getBody().get());

        assertEquals(HttpStatus.OK, response2.getStatus());
        assertTrue(response2.getBody().isPresent());
        assertContentEqualsResponse(question2, response2.getBody().get());
    }

    @Test
    void testGetByIdUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveTemplateQuestion(template, 1);

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", question1.getId()));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void testGetByIdNotFound() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        saveTemplateQuestion(template, 1);
        final UUID nonExistentId = UUID.randomUUID();

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", nonExistentId))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("No feedback question with ID " + nonExistentId, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testUpdateQuestionAndOrderAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question = saveTemplateQuestion(template, 1);

        // Update by the creator
        question.setQuestion("Do you think opossums are misunderstood creatures?");
        question.setQuestionNumber(2);
        final TemplateQuestionUpdateDTO updateDTO = updateDTO(question);

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<TemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(question, response.getBody().get());
    }

    @Test
    void testUpdateQuestionAndOrderNotAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question = saveTemplateQuestion(template, 1);

        // Update by non-creator
        question.setQuestion("Do you think opossums are misunderstood creatures?");
        question.setQuestionNumber(2);
        final TemplateQuestionUpdateDTO updateDTO = updateDTO(question);

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals(NOT_AUTHORIZED_MSG, exception.getMessage());
    }

    @Test
    void testUpdateQuestionDoesNotExist() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question = saveTemplateQuestion(template, 1);

        // Update by the creator with invalid ID
        UUID randomId = UUID.randomUUID();
        question.setId(randomId);
        question.setQuestion("Do you think opossums are misunderstood creatures?");
        question.setQuestionNumber(2);
        final TemplateQuestionUpdateDTO updateDTO = updateDTO(question);

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No feedback question with ID " + randomId, exception.getMessage());
    }

    @Test
    void testUpdateByAdmin() {
        final MemberProfile admin = createADefaultMemberProfile();
        createAndAssignAdminRole(admin);
        final MemberProfile memberOne = createASecondDefaultMemberProfile();
        FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question = saveTemplateQuestion(template, 1);

        // Update by an admin to another user's question
        question.setQuestion("Do you think opossums are misunderstood creatures?");
        question.setQuestionNumber(2);
        question.setInputType("RADIO");
        final TemplateQuestionUpdateDTO updateDTO = updateDTO(question);

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<TemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(question, response.getBody().get());
    }

    @Test
    void testUpdateDuplicateQuestionNumberNotAllowed() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());

        // Save a question as question number 1
        saveTemplateQuestion(template, 1);

        final TemplateQuestion question = saveAnotherTemplateQuestion(template, 2);
        question.setQuestionNumber(1);
        final TemplateQuestionUpdateDTO updateDTO = updateDTO(question);

        // Attempt to save another question, which is also question number 1
        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Attempted to update question on template " + template.getId() + " with duplicate question number 1", exception.getMessage());
    }

    @Test
    void testDeleteQuestionByTemplateCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question = saveTemplateQuestion(template, 1);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", question.getId()))
                .basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());

        final HttpRequest<?> getRequest = HttpRequest.GET(String.format("/%s", question.getId()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(getRequest, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No feedback question with ID " + question.getId(), exception.getMessage());
    }

    @Test
    void testDeleteQuestionByAdmin() {
        final MemberProfile admin = createADefaultMemberProfile();
        createAndAssignAdminRole(admin);
        final MemberProfile memberOne = createASecondDefaultMemberProfile();

        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question = saveTemplateQuestion(template, 1);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", question.getId()))
                .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());

        final HttpRequest<?> getRequest = HttpRequest.GET(String.format("/%s", question.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(getRequest, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No feedback question with ID " + question.getId(), exception.getMessage());
    }

    @Test
    void testDeleteQuestionUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveTemplateQuestion(template, 1);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", question1.getId())).basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals(NOT_AUTHORIZED_MSG, exception.getMessage());

    }

    @Test
    void testDeleteQuestionDoesNotExist() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        saveTemplateQuestion(template, 1);
        UUID randomId = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", randomId)).basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Could not find template question with ID " + randomId, exception.getMessage());
    }

    @Test
    void testFindByTemplateIdAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question = saveTemplateQuestion(template, 1);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", template.getId()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<TemplateQuestionResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(TemplateQuestionResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().size());
        assertContentEqualsResponse(question, response.getBody().get().get(0));

    }

    @Test
    void testFindByTemplateIdMultipleAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        final TemplateQuestion questionOne = saveTemplateQuestion(template, 1);
        final TemplateQuestion questionTwo = saveAnotherTemplateQuestion(template, 2);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", template.getId()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<TemplateQuestionResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(TemplateQuestionResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertContentEqualsResponse(questionOne, response.getBody().get().get(0));
        assertContentEqualsResponse(questionTwo, response.getBody().get().get(1));
    }

    @Test
    void testFindByTemplateIdUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());
        saveTemplateQuestion(template, 1);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", template.getId()));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

}

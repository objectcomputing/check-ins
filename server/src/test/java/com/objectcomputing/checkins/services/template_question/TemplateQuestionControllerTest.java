package com.objectcomputing.checkins.services.template_question;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateResponseDTO;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateUpdateDTO;
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

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;
import java.util.*;

public class TemplateQuestionControllerTest extends TestContainersSuite implements MemberProfileFixture, FeedbackTemplateFixture, TemplateQuestionFixture, RoleFixture {

    @Inject
    @Client("/services/feedback/template_questions")
    HttpClient client;

    FeedbackTemplate saveDefaultFeedbackTemplate(UUID createdBy) {
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(createdBy);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }

   TemplateQuestion saveQuestion(String question, UUID templateId, Integer orderNum) {
        TemplateQuestion feedbackQuestion = new TemplateQuestion(question, templateId, orderNum );
        getTemplateQuestionRepository().save(feedbackQuestion);
        return feedbackQuestion;
    }

    TemplateQuestionCreateDTO createDTO(TemplateQuestion feedbackQuestion) {
        TemplateQuestionCreateDTO dto = new TemplateQuestionCreateDTO();
        dto.setQuestion(feedbackQuestion.getQuestion());
        dto.setTemplateId(feedbackQuestion.getTemplateId());
        dto.setOrderNum(feedbackQuestion.getOrderNum());
        return dto;
    }

    void assertContentEqualsResponse(TemplateQuestion content, TemplateQuestionResponseDTO dto) {
        assertEquals(content.getQuestion(), dto.getQuestion());
        assertEquals(content.getTemplateId(), dto.getTemplateId());
        assertEquals(content.getOrderNum(), dto.getOrderNum());
    }

    @Test
    void testPostAuthorizedByTemplateCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        final TemplateQuestion feedbackQuestion = createDefaultFeedbackQuestion(feedbackTemplate.getId());
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
        createDefaultAdminRole(admin);

        final FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        final TemplateQuestion feedbackQuestion = createDefaultFeedbackQuestion(feedbackTemplate.getId());
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

        final FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        final TemplateQuestion feedbackQuestion = createDefaultFeedbackQuestion(feedbackTemplate.getId());
        final TemplateQuestionCreateDTO dto = createDTO(feedbackQuestion);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not authorized to do this operation", exception.getMessage());
    }

    @Test
    void testGetByIdAuthorizedByAdmin() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile admin = createASecondDefaultMemberProfile();
        createDefaultAdminRole(admin);

        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);

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

        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final TemplateQuestion question2 = saveQuestion("How has the project been so far?", template.getId(), 2);

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

        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", question1.getId()));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);

        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void testGetByIdNotFound() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
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
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final TemplateQuestionUpdateDTO updateDTO = new TemplateQuestionUpdateDTO();

        // Update by the creator
        updateDTO.setId(question1.getId());
        updateDTO.setQuestion("Do you think opossums are misunderstood creatures?");
        updateDTO.setOrderNum(2);
        question1.setQuestion("Do you think opossums are misunderstood creatures?");
        question1.setOrderNum(2);
        HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        HttpResponse<TemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsResponse(question1, response.getBody().get());

    }

    @Test
    void testUpdateQuestionAndOrderNotAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final TemplateQuestionUpdateDTO updateDTO = new TemplateQuestionUpdateDTO();

        // Update by the creator
        updateDTO.setId(question1.getId());
        updateDTO.setQuestion("Do you think opossums are misunderstood creatures?");
        updateDTO.setOrderNum(2);
        question1.setQuestion("Do you think opossums are misunderstood creatures?");
        question1.setOrderNum(2);
        HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());

    }

    @Test
    void testUpdateQuestionDoesNotExist() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final TemplateQuestionUpdateDTO updateDTO = new TemplateQuestionUpdateDTO();

        // Update by the creator
        UUID randomId = UUID.randomUUID();
        updateDTO.setId(randomId);
        updateDTO.setQuestion("Do you think opossums are misunderstood creatures?");
        updateDTO.setOrderNum(2);
        question1.setQuestion("Do you think opossums are misunderstood creatures?");
        question1.setOrderNum(2);
        HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("No feedback question with ID " + randomId, error);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }

    @Test
    void testUpdateAdmin() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        createDefaultAdminRole(memberOne);
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final TemplateQuestionUpdateDTO updateDTO = new TemplateQuestionUpdateDTO();

        // Update by the creator
        updateDTO.setId(question1.getId());
        updateDTO.setQuestion("Do you think opossums are misunderstood creatures?");
        updateDTO.setOrderNum(2);
        question1.setQuestion("Do you think opossums are misunderstood creatures?");
        question1.setOrderNum(2);
        HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        HttpResponse<TemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsResponse(question1, response.getBody().get());

    }


    @Test
    void testDeleteQuestionAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template =saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", question1.getId())).basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        final HttpRequest<?> getRequest = HttpRequest.GET(String.format("/%s", question1.getId()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(getRequest, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("No feedback question with ID " + question1.getId(), error);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }

    @Test
    void testDeleteQuestionUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo =createASecondDefaultMemberProfile();
        final FeedbackTemplate template =saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", question1.getId())).basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not authorized to do this operation", error);

    }

    @Test
    void testDeleteQuestionDoesNotExist() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        UUID randomId = UUID.randomUUID();
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", randomId)).basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Question with that ID does not exist", error);

    }

    @Test
    void testFindByTemplateIdAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        UUID templateId = question1.getTemplateId();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", templateId))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<TemplateQuestionResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(TemplateQuestionResponseDTO.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals( 1, response.body().size());
        assertContentEqualsResponse(question1, response.body().get(0));

    }

    @Test
    void testFindByTemplateIdMultipleAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final TemplateQuestion question2 = saveQuestion("What is your favorite animal?", template.getId(), 2);
        UUID templateId = question1.getTemplateId();
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", templateId))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<TemplateQuestionResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(TemplateQuestionResponseDTO.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(2, response.body().size());
        assertContentEqualsResponse(question1, response.body().get(0));
        assertContentEqualsResponse(question2, response.body().get(1));

    }

    @Test
    void testFindByTemplateIdUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final TemplateQuestion question1 = saveQuestion("How are you today?", template.getId(), 1);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", memberOne.getId(), question1.getTemplateId()));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }


}

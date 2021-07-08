package com.objectcomputing.checkins.services.feedback_question;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.fixture.FeedbackQuestionFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class TemplateQuestionControllerTest extends TestContainersSuite implements MemberProfileFixture, FeedbackTemplateFixture, FeedbackQuestionFixture, RoleFixture {

    @Inject
    @Client("/services/feedback/questions")
    HttpClient client;

    FeedbackTemplate saveDefaultFeedbackTemplate(UUID createdBy) {
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(createdBy);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }

    FeedbackQuestion saveQuestion(String question, UUID templateId) {
        FeedbackQuestion feedbackQuestion = new FeedbackQuestion(question, templateId);
        getFeedbackQuestionRepository().save(feedbackQuestion);
        return feedbackQuestion;
    }

    FeedbackQuestionCreateDTO createDTO(FeedbackQuestion feedbackQuestion) {
        FeedbackQuestionCreateDTO dto = new FeedbackQuestionCreateDTO();
        dto.setQuestion(feedbackQuestion.getQuestion());
        dto.setTemplateId(feedbackQuestion.getTemplateId());
        return dto;
    }

    void assertContentEqualsResponse(FeedbackQuestion content, TemplateQuestionResponseDTO dto) {
        assertEquals(content.getQuestion(), dto.getQuestion());
        assertEquals(content.getTemplateId(), dto.getTemplateId());
    }

    @Test
    void testPostAuthorizedByTemplateCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        final FeedbackQuestion feedbackQuestion = createDefaultFeedbackQuestion(feedbackTemplate.getId());
        final FeedbackQuestionCreateDTO dto = createDTO(feedbackQuestion);

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
        final MemberProfile admin = createASecondMemberProfile();
        createDefaultAdminRole(admin);

        final FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        final FeedbackQuestion feedbackQuestion = createDefaultFeedbackQuestion(feedbackTemplate.getId());
        final FeedbackQuestionCreateDTO dto = createDTO(feedbackQuestion);

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
        final MemberProfile memberTwo = createASecondMemberProfile();

        final FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        final FeedbackQuestion feedbackQuestion = createDefaultFeedbackQuestion(feedbackTemplate.getId());
        final FeedbackQuestionCreateDTO dto = createDTO(feedbackQuestion);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not authorized to do this operation", exception.getMessage());
    }

    @Test
    void testGetAuthorizedByAdmin() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile admin = createASecondMemberProfile();
        createDefaultAdminRole(admin);

        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackQuestion question1 = saveQuestion("How are you today?", template.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", question1.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<TemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, TemplateQuestionResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(question1, response.getBody().get());
    }

    @Test
    void testGetAuthorizedByTemplateCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackQuestion question1 = saveQuestion("How are you today?", template.getId());
        final FeedbackQuestion question2 = saveQuestion("How has the project been so far?", template.getId());

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
    void testGetUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackQuestion question1 = saveQuestion("How are you today?", template.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", question1.getId()));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);

        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void testGetNotFound() {
        final MemberProfile memberOne = createADefaultMemberProfile();

        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackQuestion question1 = saveQuestion("How are you today?", template.getId());
        final UUID nonExistentId = UUID.randomUUID();

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", nonExistentId))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("No feedback question with ID " + nonExistentId, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

}

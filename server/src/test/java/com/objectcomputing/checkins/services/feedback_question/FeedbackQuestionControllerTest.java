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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class FeedbackQuestionControllerTest extends TestContainersSuite implements MemberProfileFixture, FeedbackTemplateFixture, FeedbackQuestionFixture, RoleFixture {

    @Inject
    @Client("/services/feedback/questions")
    HttpClient client;

    FeedbackTemplate saveDefaultFeedbackTemplate(UUID createdBy) {
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(createdBy);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }

    FeedbackQuestionCreateDTO createDTO(FeedbackQuestion feedbackQuestion) {
        FeedbackQuestionCreateDTO dto = new FeedbackQuestionCreateDTO();
        dto.setQuestion(feedbackQuestion.getQuestion());
        dto.setTemplateId(feedbackQuestion.getTemplateId());
        return dto;
    }

    void assertContentEqualsResponse(FeedbackQuestion content, FeedbackQuestionResponseDTO dto) {
        assertEquals(content.getQuestion(), dto.getQuestion());
        assertEquals(content.getTemplateId(), dto.getTemplateId());
    }

    @Test
    void testPostAuthorizedByTemplateCreator() {
        MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        FeedbackQuestion feedbackQuestion = createFeedbackQuestion(feedbackTemplate.getId());
        FeedbackQuestionCreateDTO dto = createDTO(feedbackQuestion);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackQuestionResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(feedbackQuestion, response.getBody().get());
    }

    @Test
    void testPostAuthorizedByAdmin() {
        MemberProfile memberOne = createADefaultMemberProfile();
        MemberProfile admin = createASecondMemberProfile();
        createDefaultAdminRole(admin);

        FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        FeedbackQuestion feedbackQuestion = createFeedbackQuestion(feedbackTemplate.getId());
        FeedbackQuestionCreateDTO dto = createDTO(feedbackQuestion);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackQuestionResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsResponse(feedbackQuestion, response.getBody().get());
    }

    @Test
    void testPostUnauthorized() {
        MemberProfile memberOne = createADefaultMemberProfile();
        MemberProfile memberTwo = createASecondMemberProfile();
        FeedbackTemplate feedbackTemplate = saveDefaultFeedbackTemplate(memberOne.getId());

        FeedbackQuestion feedbackQuestion = createFeedbackQuestion(feedbackTemplate.getId());
        FeedbackQuestionCreateDTO dto = createDTO(feedbackQuestion);

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);

        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Unauthorized", exception.getMessage());
    }

}

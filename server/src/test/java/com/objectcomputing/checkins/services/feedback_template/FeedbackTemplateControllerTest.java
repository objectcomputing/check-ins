package com.objectcomputing.checkins.services.feedback_template;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionCreateDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionResponseDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionUpdateDTO;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.TemplateQuestionFixture;
import com.objectcomputing.checkins.services.guild.GuildUpdateDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
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

import javax.inject.Inject;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FeedbackTemplateControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, TemplateQuestionFixture, FeedbackTemplateFixture {

    @Inject
    @Client("/services/feedback/templates")
    HttpClient client;

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    FeedbackTemplate saveDefaultFeedbackTemplate(UUID createdBy) {
       FeedbackTemplate feedbackTemplate = createFeedbackTemplate(createdBy);
       getFeedbackTemplateRepository().save(feedbackTemplate);
       return feedbackTemplate;
    }

   UUID saveDefaultFeedbackTemplateWithQuestions(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplateCreateDTO dto = createDTO(feedbackTemplate);
        MemberProfile admin = createADefaultSupervisor();
        createDefaultAdminRole(admin);
        final TemplateQuestion question1 = createDefaultFeedbackQuestion();
        final TemplateQuestion question2 = createSecondDefaultFeedbackQuestion();
        dto.setTemplateQuestions(List.of(createDefaultTemplateQuestionDto(feedbackTemplate, question1), createDefaultTemplateQuestionDto(feedbackTemplate, question2)));
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        return response.getBody().get().getId();


    }
    FeedbackTemplate saveInactiveFeedbackTemplate(UUID createdBy) {
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(createdBy);
        feedbackTemplate.setActive(false);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }

    FeedbackTemplate saveAnotherDefaultFeedbackTemplate(UUID createdBy) {
        FeedbackTemplate feedbackTemplate = createAnotherFeedbackTemplate(createdBy);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }

    FeedbackTemplate saveAThirdDefaultFeedbackTemplate(UUID createdBy) {
        FeedbackTemplate feedbackTemplate = createAThirdFeedbackTemplate(createdBy);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }

    FeedbackTemplateCreateDTO createDTO(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplateCreateDTO dto = new FeedbackTemplateCreateDTO();
        dto.setTitle(feedbackTemplate.getTitle());
        dto.setDescription(feedbackTemplate.getDescription());
        dto.setCreatedBy(feedbackTemplate.getCreatedBy());
        dto.setActive(feedbackTemplate.getActive());
        return dto;
    }
    TemplateQuestion saveDefaultQuestion() {
        TemplateQuestion feedbackQuestion = createDefaultFeedbackQuestion();
        getTemplateQuestionRepository().save(feedbackQuestion);
        return feedbackQuestion;
    }

    TemplateQuestion saveSecondDefaultQuestion() {
        TemplateQuestion feedbackQuestion = createSecondDefaultFeedbackQuestion();
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

    void assertContentEqualsEntity(FeedbackTemplate content, FeedbackTemplateResponseDTO dto) {
        assertEquals(content.getTitle(), dto.getTitle());
        assertEquals(content.getDescription(), dto.getDescription());
        assertEquals(content.getCreatedBy(), dto.getCreatedBy());
    }

    void assertContentEqualsEntity(FeedbackTemplateUpdateDTO content, FeedbackTemplateResponseDTO entity) {
         assertEquals(content.getId(), entity.getId());
         assertEquals(content.getTitle(), entity.getTitle());
         assertEquals(content.getDescription(), entity.getDescription());
    }

    @Test
    void testPostAllowedByMember() {
        MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(memberOne.getId());
        FeedbackTemplateCreateDTO dto = createDTO(feedbackTemplate);
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsEntity(feedbackTemplate, response.getBody().get());
    }

    @Test
    void testPostAllowedByMemberWithQuestions() {
        MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(memberOne.getId());
        FeedbackTemplateCreateDTO dto = createDTO(feedbackTemplate);
        final TemplateQuestion question1 = createDefaultFeedbackQuestion();
        final TemplateQuestion question2 = createSecondDefaultFeedbackQuestion();
        dto.setTemplateQuestions(List.of(createDefaultTemplateQuestionDto(feedbackTemplate, question1), createDefaultTemplateQuestionDto(feedbackTemplate, question2) ));
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsEntity(feedbackTemplate, response.getBody().get());
    }


    @Test
    void testPostAllowedByAdminWithQuestions() {
         MemberProfile admin = createADefaultMemberProfile();
         createDefaultAdminRole(admin);
         FeedbackTemplate feedbackTemplate = createFeedbackTemplate(admin.getId());
         FeedbackTemplateCreateDTO dto = createDTO(feedbackTemplate);
        final TemplateQuestion question1 = createDefaultFeedbackQuestion();
        final TemplateQuestion question2 = createSecondDefaultFeedbackQuestion();
        dto.setTemplateQuestions(List.of(createDefaultTemplateQuestionDto(feedbackTemplate, question1), createDefaultTemplateQuestionDto(feedbackTemplate, question2) ));
         final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsEntity(feedbackTemplate, response.getBody().get());
    }

    @Test
    void testPostDenied() {
        MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(memberOne.getId());
        FeedbackTemplateCreateDTO dto = createDTO(feedbackTemplate);
        final HttpRequest<?> request = HttpRequest.POST("", dto);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void testUpdateByAdmin() {
        final MemberProfile admin = createADefaultMemberProfile();
        createDefaultAdminRole(admin);

        // Template created by non-admin
        final MemberProfile memberOne = createASecondDefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();

        // Update by an admin
        updateDTO.setId(template.getId());
        updateDTO.setTitle("An Updated Title");
        updateDTO.setDescription("An updated description");
        updateDTO.setActive(true);

        HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody().get().getCreatedBy());
        assertEquals(response.getBody().get().getCreatedBy(), memberOne.getId());
        assertContentEqualsEntity(updateDTO, response.getBody().get());
    }

    @Test
    void testAddQuestionsByCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();
        TemplateQuestion questionOne = saveDefaultFeedbackQuestion(template);
        TemplateQuestion questionTwo = new TemplateQuestion("Question to push into list", template.getId(), 2);
        TemplateQuestionUpdateDTO questionDto = updateTemplateQuestionDto(questionOne);
        TemplateQuestionUpdateDTO questionDtoSecond = updateTemplateQuestionDto(questionTwo);
        updateDTO.setId(template.getId());
        updateDTO.setTitle("An Updated Title");
        updateDTO.setDescription("An updated description");
        updateDTO.setActive(true);
        updateDTO.setCreatedBy(template.getCreatedBy());
        updateDTO.setTemplateQuestions(List.of(questionDto, questionDtoSecond));
        HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody().get().getCreatedBy());
        assertEquals(response.getBody().get().getCreatedBy(), memberOne.getId());
        assertContentEqualsEntity(updateDTO, response.getBody().get());



    }

    @Test
    void testUpdateByCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();
        TemplateQuestion questionOne = saveDefaultFeedbackQuestion(template);
        TemplateQuestionUpdateDTO questionDto = updateTemplateQuestionDto(questionOne);

        // Update by the creator
        updateDTO.setId(template.getId());
        updateDTO.setTitle("An Updated Title");
        updateDTO.setDescription("An updated description");
        updateDTO.setActive(true);
        updateDTO.setCreatedBy(template.getCreatedBy());
        updateDTO.setTemplateQuestions(Collections.singletonList(questionDto));
        HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody().get().getCreatedBy());
        assertEquals(response.getBody().get().getCreatedBy(), memberOne.getId());
        assertContentEqualsEntity(updateDTO, response.getBody().get());
    }

    @Test
    void testUpdateByNonCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        TemplateQuestion questionOne = saveDefaultFeedbackQuestion(template);
        final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();
        TemplateQuestionUpdateDTO questionDto = updateTemplateQuestionDto(questionOne);
        // Update by unauthorized user
        updateDTO.setId(template.getId());
        updateDTO.setTitle(template.getTitle());
        updateDTO.setDescription(template.getDescription());
        updateDTO.setCreatedBy(template.getCreatedBy());
        updateDTO.setActive(true);
        updateDTO.setTemplateQuestions(Collections.singletonList(questionDto));
        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testUpdateOnNonexistentId() {
         final MemberProfile memberOne = createADefaultMemberProfile();
         final MemberProfile memberTwo = createASecondDefaultMemberProfile();
         final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

         final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();
         updateDTO.setId(memberTwo.getId());
         updateDTO.setTitle(template.getTitle());
         updateDTO.setDescription(template.getDescription());
        updateDTO.setCreatedBy(template.getCreatedBy());
         updateDTO.setActive(template.getActive());
        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("No feedback template with id " + memberTwo.getId(), error);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetPublicTemplateById() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", template.getId()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsEntity(template, response.getBody().get());
    }

    //TODO: How can we perform save without dependency on POST request?
    @Test
    void testGetPublicWithQuestionsById() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(memberOne.getId());
        UUID responseId = saveDefaultFeedbackTemplateWithQuestions(feedbackTemplate);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", responseId))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);
        FeedbackTemplateCreateDTO dto = createDTO(feedbackTemplate);
        final TemplateQuestion question1 = createDefaultFeedbackQuestion();
        final TemplateQuestion question2 = createSecondDefaultFeedbackQuestion();
        List<TemplateQuestionCreateDTO> baseTemplateQuestions = List.of(createDefaultTemplateQuestionDto(feedbackTemplate, question1), createDefaultTemplateQuestionDto(feedbackTemplate, question2));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(feedbackTemplate.getTitle(), response.getBody().get().getTitle());
        assertEquals(feedbackTemplate.getDescription(), response.getBody().get().getDescription());
        assertEquals(feedbackTemplate.getCreatedBy(), response.getBody().get().getCreatedBy());
        assertEquals(feedbackTemplate.getActive(), response.getBody().get().getActive());



    }

    @Test
    void testGetByIdNotFound() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final UUID random =  UUID.randomUUID();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", random))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals(error, "No feedback template with id " + random);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetByCreatedByAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveDefaultFeedbackTemplate(memberOne.getId());
        UUID createdBy = template.getCreatedBy();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s", createdBy))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(2, response.body().size());
        assertContentEqualsEntity(template, response.body().get(0));
        assertContentEqualsEntity(templateTwo, response.body().get(1));
    }

    @Test
    void testGetByCreatedByUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s", template.getCreatedBy()));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testGetOnlyActive() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveInactiveFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?onlyActive=%s", true))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(1, response.body().size());
        assertContentEqualsEntity(template, response.body().get(0));

    }

    @Test
    void testGetActiveAndInactive() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveInactiveFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?onlyActive=%s", false))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(2, response.body().size());
        assertContentEqualsEntity(template, response.body().get(0));
        assertContentEqualsEntity(template, response.body().get(1));
    }

    @Test
    void testGetByTitleAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveDefaultFeedbackTemplate(memberOne.getId());
        String title = templateTwo.getTitle();
        final String encoded = encodeValue(title);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?title=%s", encoded))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(response.body().size(), 2);
        assertContentEqualsEntity(template, response.body().get(0));
        assertContentEqualsEntity(templateTwo, response.body().get(1));
    }


    @Test
    void testGetByTitleAndCreatedByAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveDefaultFeedbackTemplate(memberTwo.getId());
        final FeedbackTemplate templateThree = saveAThirdDefaultFeedbackTemplate(memberOne.getId());
        String title = templateThree.getTitle();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s&title=%s&", memberOne.getId(), encodeValue(title)))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(1, response.body().size());
        assertContentEqualsEntity(templateThree, response.body().get(0));
    }

    @Test
    void testGetBySimilarTitleAndCreatedByAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveDefaultFeedbackTemplate(memberTwo.getId());
        String title = template.getTitle();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s&title=%s",memberOne.getId(), encodeValue(title)))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(response.body().size(), 1);
        assertContentEqualsEntity(template, response.body().get(0));
    }

    @Test
    void testGetByTitleAndCreatedByUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdBy=%s&title=%s", memberOne.getId(), encodeValue(template.getTitle())));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testDeleteValidAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", template.getId())).basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        final HttpRequest<?> getRequest = HttpRequest.GET(String.format("/%s", template.getId()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> getResponse = client.toBlocking().exchange(getRequest, FeedbackTemplateResponseDTO.class);
        template.setActive(false);
        assertEquals(HttpStatus.OK, getResponse.getStatus());
        assertContentEqualsEntity(template, getResponse.getBody().get());
    }

    @Test
    void testDeleteValidAuthorizedMultiple() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveDefaultFeedbackTemplate(memberTwo.getId());
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", template.getId())).basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        final HttpRequest<?> getRequest = HttpRequest.GET(String.format("/%s", template.getId()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> getResponse = client.toBlocking().exchange(getRequest, FeedbackTemplateResponseDTO.class);
        final HttpRequest<?> getRequestTemplateTwo = HttpRequest.GET(String.format("/%s", templateTwo.getId()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> getResponseTwo = client.toBlocking().exchange(getRequestTemplateTwo, FeedbackTemplateResponseDTO.class);
        template.setActive(false);
        assertEquals(HttpStatus.OK, getResponse.getStatus());
        assertContentEqualsEntity(template, getResponse.getBody().get());
        assertContentEqualsEntity(templateTwo, getResponseTwo.getBody().get());
    }

    @Test
    void testDeleteInvalidAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", UUID.randomUUID())).basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testDeleteValidUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", template.getId())).basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testCreateAdHocTemplateAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate adHocTemplate = new FeedbackTemplate("Ad Hoc", "Ask a quick, single question", memberOne.getId(), false);
        getFeedbackTemplateRepository().save(adHocTemplate);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?onlyActive=%s", true))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(0, response.body().size());
    }

    @Test
    void testCreateAdHocTemplateUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate adHocTemplate = new FeedbackTemplate("Ad Hoc", "Ask a quick, single question", memberOne.getId(), false);
        FeedbackTemplateCreateDTO dto = createDTO(adHocTemplate);

        final HttpRequest<?> request = HttpRequest.POST("", dto);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Unauthorized", exception.getMessage());
    }

}

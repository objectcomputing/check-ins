package com.objectcomputing.checkins.services.feedback_template;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.TemplateQuestionFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackTemplateControllerTest.class);

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
            return "";
        }
    }

    FeedbackTemplate saveDefaultFeedbackTemplate(UUID creatorId) {
       FeedbackTemplate feedbackTemplate = createFeedbackTemplate(creatorId);
       getFeedbackTemplateRepository().save(feedbackTemplate);
       return feedbackTemplate;
    }

    FeedbackTemplate saveAnotherDefaultFeedbackTemplate(UUID creatorId) {
        FeedbackTemplate feedbackTemplate = createAnotherFeedbackTemplate(creatorId);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }

    FeedbackTemplate saveAThirdDefaultFeedbackTemplate(UUID creatorId) {
        FeedbackTemplate feedbackTemplate = createAThirdFeedbackTemplate(creatorId);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }

    /**
     * Converts a {@link FeedbackTemplate} into a {@link FeedbackTemplateCreateDTO}
     * @param feedbackTemplate {@link FeedbackTemplate}
     * @return {@link FeedbackTemplateCreateDTO}
     */
    FeedbackTemplateCreateDTO createDTO(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplateCreateDTO dto = new FeedbackTemplateCreateDTO();
        dto.setTitle(feedbackTemplate.getTitle());
        dto.setDescription(feedbackTemplate.getDescription());
        dto.setCreatorId(feedbackTemplate.getCreatorId());
        return dto;
    }

    /**
     * Converts a {@link FeedbackTemplate} into a {@link FeedbackTemplateUpdateDTO}
     * @param feedbackTemplate {@link FeedbackTemplate}
     * @return {@link FeedbackTemplateUpdateDTO}
     */
    FeedbackTemplateUpdateDTO updateDTO(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplateUpdateDTO dto = new FeedbackTemplateUpdateDTO();
        dto.setId(feedbackTemplate.getId());
        dto.setTitle(feedbackTemplate.getTitle());
        dto.setDescription(feedbackTemplate.getDescription());
        dto.setUpdaterId(feedbackTemplate.getUpdaterId());
        return dto;
    }

    void assertContentEqualsEntity(FeedbackTemplate content, FeedbackTemplateResponseDTO dto) {
        assertEquals(content.getTitle(), dto.getTitle());
        assertEquals(content.getDescription(), dto.getDescription());
        assertEquals(content.getCreatorId(), dto.getCreatorId());
        assertEquals(content.getUpdaterId(), dto.getUpdaterId());
    }

    void assertUnauthorized(HttpClientResponseException exception) {
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void testPostAllowedByMember() {
        MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(memberOne.getId());
        FeedbackTemplateCreateDTO createDTO = createDTO(feedbackTemplate);

        final HttpRequest<?> request = HttpRequest.POST("", createDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsEntity(feedbackTemplate, response.getBody().get());
    }

    @Test
    void testPostAllowedByAdmin() {
         MemberProfile admin = createADefaultMemberProfile();
         createDefaultAdminRole(admin);
         FeedbackTemplate feedbackTemplate = createFeedbackTemplate(admin.getId());
         FeedbackTemplateCreateDTO createDTO = createDTO(feedbackTemplate);

        final HttpRequest<?> request = HttpRequest.POST("", createDTO)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsEntity(feedbackTemplate, response.getBody().get());
    }

    @Test
    void testPostDeniedByUnauthorized() {
        MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate feedbackTemplate = createFeedbackTemplate(memberOne.getId());
        FeedbackTemplateCreateDTO createDTO = createDTO(feedbackTemplate);

        final HttpRequest<?> request = HttpRequest.POST("", createDTO);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    void testUpdateByAdmin() {
        final MemberProfile admin = createADefaultMemberProfile();
        createDefaultAdminRole(admin);

        // Template created by non-admin
        final MemberProfile memberOne = createASecondDefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        // Updated by an admin
        template.setTitle("An Updated Title");
        template.setDescription("An updated description");
        template.setUpdaterId(admin.getId());
        final FeedbackTemplateUpdateDTO updateDTO = updateDTO(template);

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsEntity(template, response.getBody().get());
    }

    @Test
    void testUpdateByCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        // Update by the creator
        template.setTitle("An Updated Title");
        template.setDescription("An updated description");
        template.setUpdaterId(memberOne.getId());
        final FeedbackTemplateUpdateDTO updateDTO = updateDTO(template);

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsEntity(template, response.getBody().get());
    }

    @Test
    void testUpdateByNonCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile nonCreator = createASecondDefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        // Update by unauthorized user
        template.setTitle("An Updated Title");
        template.setDescription("An updated description");
        template.setUpdaterId(nonCreator.getId());
        final FeedbackTemplateUpdateDTO updateDTO = updateDTO(template);

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(nonCreator.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("You are not authorized to do this operation", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testUpdateOnNonexistentId() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        // Update by creator
        final UUID nonexistentId = UUID.randomUUID();
        template.setId(nonexistentId);
        template.setTitle("An Updated Title");
        template.setDescription("An updated description");
        template.setUpdaterId(memberOne.getId());
        final FeedbackTemplateUpdateDTO updateDTO = updateDTO(template);

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("Could not update template with nonexistent ID " + nonexistentId, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetTemplateById() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", template.getId()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertContentEqualsEntity(template, response.getBody().get());
    }

    @Test
    void testGetByIdNotFound() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final UUID random =  UUID.randomUUID();
        saveDefaultFeedbackTemplate(memberOne.getId());

       final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", random))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("No feedback template with ID " + random, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetByCreatorId() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s", memberOne.getId()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertContentEqualsEntity(template, response.getBody().get().get(0));
        assertContentEqualsEntity(templateTwo, response.getBody().get().get(1));
    }

    @Test
    void testGetByCreatorIdUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        createASecondDefaultMemberProfile();
        saveAnotherDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s", memberOne.getId()));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    void testGetByCreatorIdNotFound() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        saveDefaultFeedbackTemplate(memberOne.getId());
        saveAnotherDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s", UUID.randomUUID()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(0, response.getBody().get().size());
    }

    @Test
    void testGetByTitle() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveAnotherDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?title=%s", encodeValue(template.getTitle())))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertContentEqualsEntity(template, response.getBody().get().get(0));
        assertContentEqualsEntity(templateTwo, response.getBody().get().get(1));
    }

    @Test
    void testGetByTitleNotFound() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        saveDefaultFeedbackTemplate(memberOne.getId());
        saveAnotherDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?title=%s", encodeValue("Not Matching")))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(0, response.getBody().get().size());
    }

    @Test
    void testGetByTitleAndCreatorId() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        saveDefaultFeedbackTemplate(memberOne.getId());
        saveAnotherDefaultFeedbackTemplate(memberTwo.getId());
        final FeedbackTemplate templateThree = saveAThirdDefaultFeedbackTemplate(memberOne.getId());
        String title = templateThree.getTitle();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&title=%s&", memberOne.getId(), encodeValue(title)))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().size());
        assertContentEqualsEntity(templateThree, response.getBody().get().get(0));
    }

    @Test
    void testGetByExistentTitleAndNonexistentCreatorId() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&title=%s&", UUID.randomUUID(), encodeValue(template.getTitle())))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(0, response.getBody().get().size());
    }

    @Test
    void testGetByNonexistentTitleAndExistentCreatorId() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        saveDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&title=%s&", memberOne.getId(), encodeValue("Not Matching")))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(0, response.getBody().get().size());
    }

    @Test
    void testGetByNonexistentTitleAndNonexistentCreatorId() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        saveDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&title=%s&", UUID.randomUUID(), encodeValue("Not Matching")))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<List<FeedbackTemplateResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackTemplateResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(0, response.getBody().get().size());
    }

    @Test
    void testGetByTitleAndCreatedByUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&title=%s", memberOne.getId(), encodeValue(template.getTitle())));
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(exception);
    }

    @Test
    void testDeleteValidAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());

        // Delete the template
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", template.getId()))
                .basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());

        // Ensure that the user cannot get the deleted template
        final HttpRequest<?> getRequest = HttpRequest.GET(String.format("/%s", template.getId()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(getRequest, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No feedback template with ID " + template.getId(), exception.getMessage());
    }

    @Test
    void testDeleteWithQuestions() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveFeedbackTemplate(memberOne.getId());

        saveTemplateQuestion(template, 1);
        saveAnotherTemplateQuestion(template, 2);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", template.getId()))
                .basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        // Ensure deleting the template also deletes all connected questions
        List<TemplateQuestion> questions = getTemplateQuestionRepository().findByTemplateId(Util.nullSafeUUIDToString(template.getId()));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(0, questions.size());
    }

    @Test
    void testDeleteInvalidAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        createASecondDefaultMemberProfile();
        saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final UUID nonexistentId = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", nonexistentId))
                .basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No feedback template with ID " + nonexistentId, exception.getMessage());
    }

    @Test
    void testDeleteUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondDefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", template.getId()))
                .basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not authorized to do this operation", exception.getMessage());
    }
}

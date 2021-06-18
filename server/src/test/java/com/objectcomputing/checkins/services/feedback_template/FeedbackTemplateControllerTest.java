package com.objectcomputing.checkins.services.feedback_template;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
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

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FeedbackTemplateControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, FeedbackTemplateFixture {

    @Inject
    @Client("/services/feedback/templates")
    HttpClient client;

     FeedbackTemplate saveDefaultFeedbackTemplate(UUID createdBy) {
       FeedbackTemplate feedbackTemplate = createFeedbackTemplate(createdBy);
       getFeedbackTemplateRepository().save(feedbackTemplate);
       return feedbackTemplate;
    }

    FeedbackTemplate saveAnotherDefaultFeedbackTemplate(UUID createdBy) {
        FeedbackTemplate feedbackTemplate = createAnotherFeedbackTemplate(createdBy);
        getFeedbackTemplateRepository().save(feedbackTemplate);
        return feedbackTemplate;
    }


    FeedbackTemplateCreateDTO createDTO(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplateCreateDTO dto = new FeedbackTemplateCreateDTO();
        dto.setTitle(feedbackTemplate.getTitle());
        dto.setDescription(feedbackTemplate.getDescription());
        dto.setCreatedBy(feedbackTemplate.getCreatedBy());
        return dto;
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
    void testPostAllowedByAdmin() {
         MemberProfile admin = createADefaultMemberProfile();
         createDefaultAdminRole(admin);
         FeedbackTemplate feedbackTemplate = createFeedbackTemplate(admin.getId());
         FeedbackTemplateCreateDTO dto = createDTO(feedbackTemplate);

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
    void testUpdateByCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();

        // Update by the creator
        updateDTO.setId(template.getId());
        updateDTO.setTitle("An Updated Title");
        updateDTO.setDescription("An updated description");

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
        final MemberProfile memberTwo = createASecondMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();

        // Update by unauthorized user
        updateDTO.setId(template.getId());
        updateDTO.setTitle(template.getTitle());
        updateDTO.setDescription(template.getDescription());

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals(error, "You are not authorized to do this operation");
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testUpdateOnNonexistentId() {
         final MemberProfile memberOne = createADefaultMemberProfile();
         final MemberProfile memberTwo = createASecondMemberProfile();
         final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

         final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();
         updateDTO.setId(memberTwo.getId());
         updateDTO.setTitle(template.getTitle());
         updateDTO.setDescription(template.getDescription());

        final HttpRequest<?> request = HttpRequest.PUT("", updateDTO)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals(error, "No feedback template with id " + memberTwo.getId());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }


    @Test
    void testGetPublicTemplateById() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", template.getId()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsEntity(template, response.getBody().get());
    }

    @Test
    void testGetTemplateUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", template.getId()));
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
    void testGetCreatedByAuthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final FeedbackTemplate templateTwo = saveDefaultFeedbackTemplate(memberOne.getId());
        UUID createdBy = template.getCreatedBy();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?=%s", createdBy))
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
    void testGetCreatedByUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?=%s", template.getCreatedBy()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertNotNull(body);
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You are not authorized to do this operation", exception.getMessage());

    }

}

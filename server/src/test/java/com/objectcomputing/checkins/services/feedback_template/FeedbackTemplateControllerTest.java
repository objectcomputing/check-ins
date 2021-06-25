package com.objectcomputing.checkins.services.feedback_template;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
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

import javax.inject.Inject;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FeedbackTemplateControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, FeedbackTemplateFixture {

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
    void testUpdateByAdmin() {
        final MemberProfile admin = createADefaultMemberProfile();
        createDefaultAdminRole(admin);

        // Template created by non-admin
        final MemberProfile memberOne = createASecondMemberProfile();
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
    void testUpdateByCreator() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

        final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();

        // Update by the creator
        updateDTO.setId(template.getId());
        updateDTO.setTitle("An Updated Title");
        updateDTO.setDescription("An updated description");
        updateDTO.setActive(true);

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
        updateDTO.setActive(true);

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
         final MemberProfile memberTwo = createASecondMemberProfile();
         final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());

         final FeedbackTemplateUpdateDTO updateDTO = new FeedbackTemplateUpdateDTO();
         updateDTO.setId(memberTwo.getId());
         updateDTO.setTitle(template.getTitle());
         updateDTO.setDescription(template.getDescription());
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
        final MemberProfile memberTwo = createASecondMemberProfile();
        final FeedbackTemplate template = saveDefaultFeedbackTemplate(memberOne.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", template.getId()))
                .basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackTemplateResponseDTO> response = client.toBlocking().exchange(request, FeedbackTemplateResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertContentEqualsEntity(template, response.getBody().get());
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
        final MemberProfile memberTwo = createASecondMemberProfile();
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
        final MemberProfile memberTwo = createASecondMemberProfile();
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
        final MemberProfile memberTwo = createASecondMemberProfile();
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
        final MemberProfile memberTwo = createASecondMemberProfile();
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
        final MemberProfile memberTwo = createASecondMemberProfile();
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
        final MemberProfile memberTwo = createASecondMemberProfile();
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
        final MemberProfile memberTwo = createASecondMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", UUID.randomUUID())).basicAuth(memberOne.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testDeleteValidUnauthorized() {
        final MemberProfile memberOne = createADefaultMemberProfile();
        final MemberProfile memberTwo = createASecondMemberProfile();
        final FeedbackTemplate template = saveAnotherDefaultFeedbackTemplate(memberOne.getId());
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", template.getId())).basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException exception = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        final JsonNode body = exception.getResponse().getBody(JsonNode.class).orElse(null);
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

}

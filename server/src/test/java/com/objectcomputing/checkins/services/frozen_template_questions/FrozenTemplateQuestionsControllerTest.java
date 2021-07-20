package com.objectcomputing.checkins.services.frozen_template_questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.frozen_template.FrozenTemplate;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class FrozenTemplateQuestionsControllerTest extends TestContainersSuite implements RepositoryFixture, FeedbackRequestFixture, FrozenTemplateFixture, FrozenTemplateQuestionFixture, MemberProfileFixture, RoleFixture {
    @Inject
    @Client("/services/feedback/frozen_template_questions")
    HttpClient client;

    private FrozenTemplate createSampleFrozenTemplate(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        FeedbackRequest req = createFeedbackRequest(creator, requestee, recipient);
        FrozenTemplate ft = saveDefaultFrozenTemplate(creator.getId(), req.getId());
        return ft;
    }

    private void assertUnauthorized(HttpClientResponseException responseException) {
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    private void assertResponseEqualsEntity(FrozenTemplateQuestion feedbackRequestQ, FrozenTemplateQuestionResponseDTO dto) {
        if (feedbackRequestQ == null || dto == null) {
            assertNull(feedbackRequestQ);
            assertNull(dto);
        } else {
            assertEquals(feedbackRequestQ.getFrozenTemplateId(), dto.getFrozenTemplateId());
            assertEquals(feedbackRequestQ.getQuestionContent(), dto.getQuestionContent());
            assertEquals(feedbackRequestQ.getQuestionNumber(), dto.getQuestionNumber());
        }
    }

    @Test
    void testSaveByCreator() {
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        FrozenTemplate template = createSampleFrozenTemplate(pdlMemberProfile, employeeMemberProfile, recipient);
        FrozenTemplateQuestion questionOne = new FrozenTemplateQuestion(
                template.getId(),
                "How are you?",
                1);
        final HttpRequest<?> request = HttpRequest.POST("", questionOne)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FrozenTemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateQuestionResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(questionOne, response.getBody().get());
    }

    @Test
    void testSaveUnauthorized() {
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        final MemberProfile randomMember = createASecondDefaultMemberProfile();
        FrozenTemplate template = createSampleFrozenTemplate(pdlMemberProfile, employeeMemberProfile, recipient);
        FrozenTemplateQuestion questionOne = createDefaultFrozenTemplateQuestion(template.getId());
        final HttpRequest<?> request = HttpRequest.POST("", questionOne)
                .basicAuth(randomMember.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));
        assertUnauthorized(responseException);
    }

    @Test
    void testSaveAdmin() {
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        final MemberProfile admin = createADefaultSupervisor();
        createDefaultAdminRole(admin);
        FrozenTemplate template= createSampleFrozenTemplate(pdlMemberProfile, employeeMemberProfile, recipient);
        FrozenTemplateQuestion questionOne = new FrozenTemplateQuestion(
                template.getId(),
                "How are you?",
                1);
        final HttpRequest<?> request = HttpRequest.POST("", questionOne)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FrozenTemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateQuestionResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(questionOne, response.getBody().get());
    }

    @Test
    void testGetByIdByCreator() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FrozenTemplate template = createSampleFrozenTemplate(pdlMemberProfile, requestee, recipient);
        FrozenTemplateQuestion questionOne = createDefaultFrozenTemplateQuestion(template.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", questionOne.getId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FrozenTemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateQuestionResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertResponseEqualsEntity(questionOne, response.body());
    }

    @Test
    void testGetByIdByRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FrozenTemplate template = createSampleFrozenTemplate(pdlMemberProfile, requestee, recipient);
        FrozenTemplateQuestion questionOne = createDefaultFrozenTemplateQuestion(template.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", questionOne.getId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FrozenTemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateQuestionResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertResponseEqualsEntity(questionOne, response.body());
    }

    @Test
    void testGetByIdByAdmin() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile admin = createADefaultSupervisor();
        createDefaultAdminRole(admin);
        FrozenTemplate template = createSampleFrozenTemplate(pdlMemberProfile, requestee, recipient);
        FrozenTemplateQuestion questionOne = createDefaultFrozenTemplateQuestion(template.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", questionOne.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FrozenTemplateQuestionResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateQuestionResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertResponseEqualsEntity(questionOne, response.body());
    }

    @Test
    void testGetByIdUnauthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile random = createASecondDefaultMemberProfile();
        FrozenTemplate frozenTemplate = createSampleFrozenTemplate(pdlMemberProfile, requestee, recipient);
        FrozenTemplateQuestion questionOne = createDefaultFrozenTemplateQuestion(frozenTemplate.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", questionOne.getId()))
                .basicAuth(random.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));
        assertUnauthorized(responseException);
    }

    @Test
    void testGetByFrozenTemplateIdAuthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FrozenTemplate frozenTemplate = createSampleFrozenTemplate(pdlMemberProfile, requestee, recipient);
        FrozenTemplateQuestion questionOne = createDefaultFrozenTemplateQuestion(frozenTemplate.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", questionOne.getFrozenTemplateId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FrozenTemplateQuestionResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FrozenTemplateQuestionResponseDTO.class));

        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().size());
        assertResponseEqualsEntity(questionOne, response.getBody().get().get(0));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGetMultipleByFrozenTemplateIdAuthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FrozenTemplate template = createSampleFrozenTemplate(pdlMemberProfile, requestee, recipient);
        FrozenTemplateQuestion questionOne = createDefaultFrozenTemplateQuestion(template.getId());
        FrozenTemplateQuestion questionTwo = createAnotherDefaultFrozenTemplateQuestion(template.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", questionOne.getFrozenTemplateId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FrozenTemplateQuestionResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FrozenTemplateQuestionResponseDTO.class));

        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(questionOne, response.getBody().get().get(0));
        assertResponseEqualsEntity(questionTwo, response.getBody().get().get(1));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGetByFrozenTemplateIdUnauthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile randomPerson = createAnUnrelatedUser();
        FrozenTemplate frozenTemplate= createSampleFrozenTemplate(pdlMemberProfile, requestee, recipient);
        FrozenTemplateQuestion questionOne = createDefaultFrozenTemplateQuestion(frozenTemplate.getId());
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?templateId=%s", questionOne.getFrozenTemplateId()))
                .basicAuth(randomPerson.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));
        assertUnauthorized(responseException);
    }


}
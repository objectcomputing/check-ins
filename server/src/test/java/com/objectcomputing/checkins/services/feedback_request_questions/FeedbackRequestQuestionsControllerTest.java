package com.objectcomputing.checkins.services.feedback_request_questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestCreateDTO;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestResponseDTO;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestUpdateDTO;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FeedbackRequestQuestionsControllerTest extends TestContainersSuite implements RepositoryFixture, FeedbackRequestFixture, FeedbackRequestQuestionFixture, MemberProfileFixture, RoleFixture {
    @Inject
    @Client("/services/feedback/request_questions")
    HttpClient client;

    private FeedbackRequest createSampleFeedbackRequest(MemberProfile pdlMember, MemberProfile requestee, MemberProfile recipient) {
        createDefaultRole(RoleType.PDL, pdlMember);
        return createFeedbackRequest(pdlMember, requestee, recipient);
    }
    private void assertUnauthorized(HttpClientResponseException responseException) {
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
    private void assertResponseEqualsEntity(FeedbackRequestQuestion feedbackRequestQ, FeedbackRequestQuestionResponseDTO dto) {
        if (feedbackRequestQ == null || dto == null) {
            assertEquals(feedbackRequestQ, dto);
        } else {
            assertEquals(feedbackRequestQ.getRequestId(), dto.getRequestId());
            assertEquals(feedbackRequestQ.getQuestionContent(), dto.getQuestionContent());
            assertEquals(feedbackRequestQ.getAnswerContent(), dto.getAnswerContent());
            assertEquals(feedbackRequestQ.getOrderNum(), dto.getOrderNum());

        }
    }

    @Test
    void testSaveByCreator() {
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest req = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(req.getId());
        final HttpRequest<?> request = HttpRequest.POST("", questionOne)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestQuestionResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
       assertResponseEqualsEntity(questionOne, response.getBody().get());

    }

    @Test
    void testSaveUnauthorized() {
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        final MemberProfile randomMember = createASecondDefaultMemberProfile();
        FeedbackRequest req = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(req.getId());
        final HttpRequest<?> request = HttpRequest.POST("", questionOne)
                .basicAuth(randomMember.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testSaveAdmin() {
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        final MemberProfile admin = createADefaultSupervisor();
        createDefaultAdminRole(admin);
        FeedbackRequest req = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(req.getId());
        final HttpRequest<?> request = HttpRequest.POST("", questionOne)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestQuestionResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertResponseEqualsEntity(questionOne, response.getBody().get());

    }

   @Test
    void testUpdateByCreator() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackReq.getId());
       final FeedbackRequestQuestionUpdateDTO dto = new FeedbackRequestQuestionUpdateDTO();
       dto.setId(questionOne.getId());
       dto.setAnswerContent("Something random");
       questionOne.setAnswerContent("Something random");
       HttpRequest<?> request = HttpRequest.PUT("", dto)
               .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(request, Map.class));
       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("You are not authorized to do this operation", error);
       assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

   }

   @Test
    void testUpdateByAdmin() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       final MemberProfile admin = createADefaultSupervisor();
       createDefaultAdminRole(admin);
       FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackReq.getId());
       final FeedbackRequestQuestionUpdateDTO dto = new FeedbackRequestQuestionUpdateDTO();
       dto.setId(questionOne.getId());
       dto.setAnswerContent("Something random");
       questionOne.setAnswerContent("Something random");
       HttpRequest<?> request = HttpRequest.PUT("", dto)
               .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
       final HttpResponse<FeedbackRequestQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestQuestionResponseDTO.class);
       assertEquals(HttpStatus.OK, response.getStatus());
       assertResponseEqualsEntity(questionOne, response.getBody().get());

   }

   @Test
   void updateByRecipient() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackReq.getId());
       final FeedbackRequestQuestionUpdateDTO dto = new FeedbackRequestQuestionUpdateDTO();
       dto.setId(questionOne.getId());
       dto.setAnswerContent("Something random");
       questionOne.setAnswerContent("Something random");
       HttpRequest<?> request = HttpRequest.PUT("", dto)
               .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
       final HttpResponse<FeedbackRequestQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestQuestionResponseDTO.class);
       assertEquals(HttpStatus.OK, response.getStatus());
       assertResponseEqualsEntity(questionOne, response.getBody().get());

   }

   @Test
    void testUpdateRequesteeUnauthorized() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackReq.getId());
       final FeedbackRequestQuestionUpdateDTO dto = new FeedbackRequestQuestionUpdateDTO();
       dto.setId(questionOne.getId());
       dto.setAnswerContent("Something random");
       questionOne.setAnswerContent("Something random");
       HttpRequest<?> request = HttpRequest.PUT("", dto)
               .basicAuth(employeeMemberProfile.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(request, Map.class));
       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("You are not authorized to do this operation", error);
       assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

   }

   @Test
   void testUpdateRandomUserUnauthorized() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile rando = createASecondDefaultMemberProfile();
       FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackReq.getId());
       final FeedbackRequestQuestionUpdateDTO dto = new FeedbackRequestQuestionUpdateDTO();
       dto.setId(questionOne.getId());
       dto.setAnswerContent("Something random");
       questionOne.setAnswerContent("Something random");
       HttpRequest<?> request = HttpRequest.PUT("", dto)
               .basicAuth(rando.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(request, Map.class));
       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("You are not authorized to do this operation", error);
       assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

   }

   @Test
   void testGetByIdByCreator() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("%s", questionOne.getId()))
               .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
       final HttpResponse<FeedbackRequestQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestQuestionResponseDTO.class);
       assertEquals(HttpStatus.OK, response.getStatus());
       assertResponseEqualsEntity(questionOne, response.body());

   }

   @Test
   void testGetByIdByRecipient() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("%s", questionOne.getId()))
               .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
       final HttpResponse<FeedbackRequestQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestQuestionResponseDTO.class);
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
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("%s", questionOne.getId()))
               .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
       final HttpResponse<FeedbackRequestQuestionResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestQuestionResponseDTO.class);
       assertEquals(HttpStatus.OK, response.getStatus());
       assertResponseEqualsEntity(questionOne, response.body());

   }

   @Test
   void testGetByIdUnauthorized() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile random = createASecondDefaultMemberProfile();
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("%s", questionOne.getId()))
               .basicAuth(random.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(request, Map.class));
       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("You are not authorized to do this operation", error);
       assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

   }

}

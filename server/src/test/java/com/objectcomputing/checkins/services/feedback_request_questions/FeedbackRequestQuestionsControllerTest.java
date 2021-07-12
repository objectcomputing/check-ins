package com.objectcomputing.checkins.services.feedback_request_questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.fixture.*;
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

   @Test
    void testGetByRequestIdAuthorized() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("/?requestId=%s", questionOne.getRequestId()))
               .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
       final HttpResponse<List<FeedbackRequestQuestionResponseDTO>> response = client.toBlocking()
               .exchange(request, Argument.listOf(FeedbackRequestQuestionResponseDTO.class));

       assertEquals(1, response.getBody().get().size());
       assertResponseEqualsEntity(questionOne, response.getBody().get().get(0));
      assertEquals(HttpStatus.OK, response.getStatus());

   }

   @Test
    void testGetMultipleByRequestIdAuthorized() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       FeedbackRequestQuestion questionTwo = createAnotherDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("/?requestId=%s", questionOne.getRequestId()))
               .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
       final HttpResponse<List<FeedbackRequestQuestionResponseDTO>> response = client.toBlocking()
               .exchange(request, Argument.listOf(FeedbackRequestQuestionResponseDTO.class));

       assertEquals(2, response.getBody().get().size());
       assertResponseEqualsEntity(questionOne, response.getBody().get().get(0));
       assertResponseEqualsEntity(questionTwo, response.getBody().get().get(1));
       assertEquals(HttpStatus.OK, response.getStatus());

   }

   @Test
    void testGetByRequestIdUnauthorized() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile randomPerson = createAnUnrelatedUser();
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("/?requestId=%s", questionOne.getRequestId()))
               .basicAuth(randomPerson.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(request, Map.class));
       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("You are not authorized to do this operation", error);
       assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

   }

   @Test
    void testDeleteAdmin() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile admin = createADefaultSupervisor();
       createDefaultAdminRole(admin);
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", questionOne.getId()))
               .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
       final HttpResponse<?> response = client.toBlocking()
               .exchange(request, Argument.listOf(FeedbackRequestQuestionResponseDTO.class));
       assertEquals(HttpStatus.OK, response.getStatus());
       final HttpRequest<?> getRequest = HttpRequest.GET(String.format("/%s", questionOne.getId()))
               .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(getRequest, Map.class));
       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("No feedback request question with id " + questionOne.getId(), error);
       assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());


   }

   @Test
    void testDeleteUnauthorized() {
       MemberProfile pdlMemberProfile = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
       MemberProfile recipient = createADefaultRecipient();
       FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
       FeedbackRequestQuestion questionOne = createDefaultFeedbackRequestQuestion(feedbackRequest.getId());
       final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", questionOne.getId()))
               .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(request, Map.class));
       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("You are not authorized to do this operation", error);
       assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

   }


}

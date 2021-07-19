package com.objectcomputing.checkins.services.frozen_template;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.fixture.*;

import static org.junit.jupiter.api.Assertions.*;

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
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FrozenTemplateControllerTest extends TestContainersSuite implements RepositoryFixture, RoleFixture, FrozenTemplateFixture, MemberProfileFixture, FeedbackRequestFixture {

    @Inject
    @Client("/services/feedback/frozen_templates")
    HttpClient client;

    void assertContentEqualsEntity(FrozenTemplate template, FrozenTemplateResponseDTO res) {
        assertEquals(template.getCreatorId(), res.getCreatorId());
        assertEquals(template.getDescription(), res.getDescription());
        assertEquals(template.getTitle(), res.getTitle());
        assertEquals(template.getRequestId(), res.getRequestId());
    }

    FeedbackRequest createFeedbackRequest(UUID creatorId, UUID requesteeId, UUID recipientId) {
        FeedbackRequest req = new FeedbackRequest();
        req.setCreatorId(creatorId);
        req.setRequesteeId(requesteeId);
        req.setRecipientId(recipientId);
        req.setStatus("Pending");
        req.setSendDate(LocalDate.now());
        getFeedbackRequestRepository().save(req);
        return req;
    }

    @Test
    void testPostByRequestCreator( ) {
        MemberProfile memberOne = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(memberOne);
        createDefaultRole(RoleType.PDL, memberOne);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest req = createFeedbackRequest(memberOne, requestee, recipient);
        FrozenTemplate temp = new FrozenTemplate("Random Title", "Random description", memberOne.getId(),req.getId() );
//        //create frozen template
        FrozenTemplateCreateDTO dto = new FrozenTemplateCreateDTO();
        dto.setTitle("Random Title");
        dto.setDescription("Random description");
        dto.setCreatorId(memberOne.getId());
        dto.setRequestId(req.getId());

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FrozenTemplateResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsEntity(temp, response.getBody().get());


    }


    @Test
    void testPostAdmin() {
        MemberProfile memberOne = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(memberOne);
        createDefaultRole(RoleType.PDL, memberOne);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile admin = createADefaultSupervisor();
        createDefaultAdminRole(admin);
        FeedbackRequest req = createFeedbackRequest(memberOne, requestee, recipient);
        FrozenTemplate temp = new FrozenTemplate("Random Title", "Random description", memberOne.getId(),req.getId() );       //create frozen template
        FrozenTemplateCreateDTO dto = new FrozenTemplateCreateDTO();
        dto.setTitle("Random Title");
        dto.setDescription("Random description");
        dto.setCreatorId(memberOne.getId());
        dto.setRequestId(req.getId());

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

    }

   @Test
    void testPostUnrelatedUser() {
       MemberProfile memberOne = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(memberOne);
       createDefaultRole(RoleType.PDL, memberOne);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile rando = createASecondDefaultMemberProfile();
       FeedbackRequest req = createFeedbackRequest(memberOne, requestee, recipient);
       FrozenTemplate temp = new FrozenTemplate("Random Title", "Random description", memberOne.getId(),req.getId() );       //create frozen template
       FrozenTemplateCreateDTO dto = new FrozenTemplateCreateDTO();
       dto.setTitle("Random Title");
       dto.setDescription("Random description");
       dto.setCreatorId(memberOne.getId());
       dto.setRequestId(req.getId());

       final HttpRequest<?> request = HttpRequest.POST("", dto)
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
       MemberProfile memberOne = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(memberOne);
       createDefaultRole(RoleType.PDL, memberOne);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile rando = createASecondDefaultMemberProfile();
       FeedbackRequest req = createFeedbackRequest(memberOne, requestee, recipient);
       FrozenTemplate temp = saveDefaultFrozenTemplate(rando.getId(), req.getId());

       final HttpRequest<?> request = HttpRequest.GET(String.format("%s", temp.getId()))
               .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.PDL_ROLE);
       final HttpResponse<FrozenTemplateResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateResponseDTO.class);
       assertEquals(HttpStatus.OK, response.getStatus());
       assertContentEqualsEntity(temp, response.getBody().get());

   }



   @Test
    void testGetByIdUnauthorized() {
       MemberProfile memberOne = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(memberOne);
       createDefaultRole(RoleType.PDL, memberOne);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile rando = createASecondDefaultMemberProfile();
       FeedbackRequest req = createFeedbackRequest(memberOne, requestee, recipient);
       FrozenTemplate temp = saveDefaultFrozenTemplate(rando.getId(), req.getId());

       final HttpRequest<?> request = HttpRequest.GET(String.format("%s", temp.getId()))
               .basicAuth(rando.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(request, Map.class));

       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("You are not authorized to do this operation", error);
       assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

   }

   @Test
    void testGetByRequestIdAuthorized() {
       MemberProfile memberOne = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(memberOne);
       createDefaultRole(RoleType.PDL, memberOne);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile rando = createASecondDefaultMemberProfile();
       FeedbackRequest req = createFeedbackRequest(memberOne, requestee, recipient);
       FrozenTemplate temp = saveDefaultFrozenTemplate(rando.getId(), req.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("/?requestId=%s", temp.getRequestId()))
               .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.PDL_ROLE);
       final HttpResponse<FrozenTemplateResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateResponseDTO.class);
       assertEquals(HttpStatus.OK, response.getStatus());
       assertContentEqualsEntity(temp, response.getBody().get());


   }

   @Test
    void testGetByRequestIdUnauthorized() {
       MemberProfile memberOne = createADefaultMemberProfile();
       MemberProfile requestee = createADefaultMemberProfileForPdl(memberOne);
       createDefaultRole(RoleType.PDL, memberOne);
       MemberProfile recipient = createADefaultRecipient();
       MemberProfile rando = createASecondDefaultMemberProfile();
       FeedbackRequest req = createFeedbackRequest(memberOne, requestee, recipient);
       FrozenTemplate temp = saveDefaultFrozenTemplate(rando.getId(), req.getId());
       final HttpRequest<?> request = HttpRequest.GET(String.format("/?requestId=%s", temp.getRequestId()))
               .basicAuth(rando.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
       final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
               client.toBlocking().exchange(request, Map.class));

       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
       String error = Objects.requireNonNull(body).get("message").asText();
       assertEquals("You are not authorized to do this operation", error);
       assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());


   }



}

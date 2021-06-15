package com.objectcomputing.checkins.services.feedback_request;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FeedbackRequestControllerTest extends TestContainersSuite implements RepositoryFixture, MemberProfileFixture, FeedbackRequestFixture, RoleFixture {
    @Inject
    @Client("/services/feedback/requests")
    HttpClient client;

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestControllerTest.class);

    private FeedbackRequest createSampleFeedbackRequest(MemberProfile pdlMember, MemberProfile employeeMember) {
        createDefaultRole(RoleType.PDL, pdlMember);
        return createFeedbackRequest(pdlMember, employeeMember);
    }

    private void assertResponseEqualsEntity(FeedbackRequest feedbackRequest, FeedbackRequestResponseDTO dto) {
        if (feedbackRequest == null || dto == null) {
            assertEquals(feedbackRequest, dto);
        } else {
            assertEquals(feedbackRequest.getId(), dto.getId());
            assertEquals(feedbackRequest.getCreatorId(), dto.getCreatorId());
            assertEquals(feedbackRequest.getRequesteeId(), dto.getRequesteeId());
            assertEquals(feedbackRequest.getSendDate(), dto.getSendDate());
            assertEquals(feedbackRequest.getTemplateId(), dto.getTemplateId());
            assertEquals(feedbackRequest.getDueDate(), dto.getDueDate());
        }
    }

    private void assertResponseEqualsCreate(FeedbackRequestResponseDTO entity, FeedbackRequestCreateDTO dto) {
        if (entity == null || dto == null) {
            assertEquals(entity, dto);
        } else {
            assertEquals(entity.getCreatorId(), dto.getCreatorId());
            assertEquals(entity.getRequesteeId(), dto.getRequesteeId());
            assertEquals(entity.getSendDate(), dto.getSendDate());
            assertEquals(entity.getTemplateId(), dto.getTemplateId());
            assertEquals(entity.getStatus(), dto.getStatus());
            assertEquals(entity.getDueDate(), dto.getDueDate());
        }
    }

    @Test
    void testCreateFeedbackRequestByAdmin() {
        //create two member profiles: one for normal employee, one for admin
        final MemberProfile memberProfile = createADefaultMemberProfile();
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("admin"));
        createDefaultAdminRole(admin);

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        dto.setCreatorId(admin.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setTemplateId(UUID.randomUUID());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertResponseEqualsCreate(response.body(), dto);
    }

    @Test
    void testCreateFeedbackRequestByAssignedPDL() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        createDefaultRole(RoleType.PDL, pdlMemberProfile);

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        dto.setCreatorId(pdlMemberProfile.getId());
        dto.setRequesteeId(employeeMemberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setTemplateId(UUID.randomUUID());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertResponseEqualsCreate(response.body(), dto);
    }

    @Test
    void testCreateFeedbackRequestByUnassignedPdl() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createAnotherDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, memberProfileForPDL);

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        dto.setCreatorId(memberProfileForPDL.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setTemplateId(UUID.randomUUID());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileForPDL.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testCreateFeedbackRequestByMember() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile requesteeProfile = createAnotherDefaultMemberProfile();

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        dto.setCreatorId(requesteeProfile.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setTemplateId(UUID.randomUUID());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(requesteeProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testGetFeedbackRequestByPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertResponseEqualsEntity(feedbackRequest, response.body());
    }

    @Test
    void testGetFeedbackRequestByEmployee() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberProfile2 = createAnotherDefaultMemberProfile();
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(memberProfile2.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());


    }

    @Test
    void testDeleteFeedbackRequestByMember() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberTwo = createAnUnrelatedUser();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

    }

    @Test
    void testDeleteByAdmin() {
        MemberProfile admin = createAnotherDefaultMemberProfile();
        createDefaultAdminRole(admin);
        MemberProfile employeeMemberProfile = createADefaultMemberProfile();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(admin, employeeMemberProfile);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE );
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackRequestByPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE );
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testDeleteFeedbackReqByUnassignedPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createAnotherDefaultMemberProfile();
        MemberProfile creator = createAnUnrelatedUser();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(creator, memberOne);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

    }

}

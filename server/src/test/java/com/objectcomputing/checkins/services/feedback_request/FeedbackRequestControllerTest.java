package com.objectcomputing.checkins.services.feedback_request;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.util.*;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

import java.time.LocalDate;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FeedbackRequestControllerTest extends TestContainersSuite implements RepositoryFixture, MemberProfileFixture, FeedbackTemplateFixture, FeedbackRequestFixture, RoleFixture {
    @Inject
    @Client("/services/feedback/requests")
    HttpClient client;

    private EmailSender emailSender = mock(EmailSender.class);

    @Inject
    private FeedbackRequestServicesImpl feedbackRequestServicesImpl;

    @Property(name = FeedbackRequestServicesImpl.FEEDBACK_REQUEST_NOTIFICATION_SUBJECT)
    String notificationSubject;

    @Property(name = FeedbackRequestServicesImpl.FEEDBACK_REQUEST_NOTIFICATION_CONTENT)
    String notificationContent;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(emailSender);
        feedbackRequestServicesImpl.setEmailSender(emailSender);
    }

    private FeedbackRequest createSampleFeedbackRequest(MemberProfile pdlMember, MemberProfile requestee, MemberProfile recipient) {
        createDefaultRole(RoleType.PDL, pdlMember);
        FeedbackTemplate template = createFeedbackTemplate(pdlMember.getId());
        getFeedbackTemplateRepository().save(template);
        return createFeedbackRequest(pdlMember, requestee, recipient, template.getId());
    }

    private void assertUnauthorized(HttpClientResponseException responseException) {
        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals("You are not authorized to do this operation", error);
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    private void assertResponseEqualsEntity(FeedbackRequest feedbackRequest, FeedbackRequestResponseDTO dto) {
        assertEquals(feedbackRequest.getId(), dto.getId());
        assertEquals(feedbackRequest.getCreatorId(), dto.getCreatorId());
        assertEquals(feedbackRequest.getRequesteeId(), dto.getRequesteeId());
        assertEquals(feedbackRequest.getSendDate(), dto.getSendDate());
        assertEquals(feedbackRequest.getDueDate(), dto.getDueDate());
        assertEquals(feedbackRequest.getTemplateId(), dto.getTemplateId());
        assertEquals(feedbackRequest.getRecipientId(), dto.getRecipientId());
    }

    private void assertResponseEqualsCreate(FeedbackRequestResponseDTO entity, FeedbackRequestCreateDTO dto) {
        assertEquals(entity.getCreatorId(), dto.getCreatorId());
        assertEquals(entity.getRequesteeId(), dto.getRequesteeId());
        assertEquals(entity.getSendDate(), dto.getSendDate());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getDueDate(), dto.getDueDate());
        assertEquals(entity.getRecipientId(), dto.getRecipientId());
    }

    @Test
    void testCreateFeedbackRequestByAdmin() {
        //create two member profiles: one for normal employee, one for admin
        final MemberProfile memberProfile = createADefaultMemberProfile();
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("admin"));
        final MemberProfile recipient = createADefaultRecipient();
        createDefaultAdminRole(admin);

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(admin.getId()));
        dto.setCreatorId(admin.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setStatus("pending");
        dto.setTemplateId(template.getId());
        dto.setRecipientId(recipient.getId());
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsCreate(response.getBody().get(), dto);
    }

    @Test
    void testCreateFeedbackRequestByAssignedPDL() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        dto.setCreatorId(pdlMemberProfile.getId());
        dto.setRequesteeId(employeeMemberProfile.getId());
        dto.setRecipientId(recipient.getId());
        dto.setSendDate(LocalDate.now());
        dto.setStatus("pending");
        dto.setTemplateId(template.getId());
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsCreate(response.getBody().get(), dto);
    }

    @Test
    void testCreateFeedbackRequestSendsEmail() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();
        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        dto.setCreatorId(pdlMemberProfile.getId());
        dto.setRequesteeId(employeeMemberProfile.getId());
        dto.setRecipientId(recipient.getId());
        dto.setSendDate(LocalDate.now());
        dto.setStatus("pending");
        dto.setDueDate(null);
        dto.setTemplateId(template.getId());

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //verify appropriate email was sent
        verify(emailSender).sendEmail(notificationSubject, "You have received a feedback request. Please go to the <a href=\"https://checkins.objectcomputing.com/feedback/submit?requestId=" + response.getBody().get().getId() + "\">Check-Ins application</a>.", recipient.getWorkEmail());
    }

    @Test
    void testCreateFeedbackRequestByUnassignedPdl() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createASecondDefaultMemberProfile();
        MemberProfile recipient = createAnUnrelatedUser();
        createDefaultRole(RoleType.PDL, memberProfileForPDL);

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(memberProfileForPDL.getId()));
        dto.setCreatorId(memberProfileForPDL.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setRecipientId(recipient.getId());
        dto.setSendDate(LocalDate.now());
        dto.setStatus("pending");
        dto.setDueDate(null);
        dto.setTemplateId(template.getId());

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileForPDL.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testCreateFeedbackRequestByMember() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile requesteeProfile = createASecondDefaultMemberProfile();
        MemberProfile recipient = createAnUnrelatedUser();

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(memberProfile.getId()));
        dto.setTemplateId(template.getId());
        dto.setCreatorId(requesteeProfile.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setRecipientId(recipient.getId());
        dto.setSendDate(LocalDate.now());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(requesteeProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testGetFeedbackRequestByAdmin() {
        MemberProfile admin = createADefaultMemberProfile();
        createDefaultAdminRole(admin);
        MemberProfile pdlMemberProfile = createASecondDefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetFeedbackRequestByAssignedPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetFeedbackRequestByUnassignedPdl() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile unrelatedPdl = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, unrelatedPdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(unrelatedPdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testGetFeedbackRequestByRequestee() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberProfile2 = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(memberProfile2.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        // requestee should not be able to get the feedback request about them
        assertUnauthorized(responseException);
    }

    @Test
    void testGetFeedbackRequestByRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        // recipient must be able to get the feedback request
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetFeedbackRequestByUnrelatedUser() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile unrelatedUser = createAnUnrelatedUser();
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(unrelatedUser.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testGetByCreatorIdPermitted() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        //create two sample feedback requests by the same PDL
        FeedbackRequest feedbackReq = createFeedbackRequest(pdlMemberProfile, memberOne, recipientOne, template.getId());
        FeedbackRequest feedbackReq2 = createFeedbackRequest(pdlMemberProfile, memberTwo, recipientTwo, template.getId());

        //search for feedback requests by a specific creator
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s", feedbackReq.getCreatorId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReq2, response.getBody().get().get(1));
    }

    @Test
    void testGetByCreatorIdPermittedMultipleReqs() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberThree = createAThirdDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipient = createADefaultRecipient();

        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        //create two sample feedback requests by the same PDL
        FeedbackRequest feedbackReq = createFeedbackRequest(pdlMemberProfile, memberOne, recipient, template.getId());
        FeedbackRequest feedbackReqTwo = createFeedbackRequest(pdlMemberProfile, memberTwo, recipient, template.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s", feedbackReq.getCreatorId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(response.getBody().get().size(), 2);
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqTwo, response.getBody().get().get(1));

    }

    @Test
    void testGetByCreatorIdNotPermitted() {
        MemberProfile pdl = createADefaultMemberProfile();
        MemberProfile employeeWithPdl = createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = createSampleFeedbackRequest(pdl, employeeWithPdl, recipient);
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s", feedbackRequest.getCreatorId()))
                .basicAuth(employeeWithPdl.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testGetByCreatorRequesteeIdPermitted() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        //create two sample feedback requests by the same PDL
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        FeedbackRequest feedbackReq = createFeedbackRequest(pdlMemberProfile, memberOne, recipientOne, template.getId());
        createFeedbackRequest(pdlMemberProfileTwo, memberTwo, recipientTwo, template.getId());

        //search for feedback requests by a specific creator, requestee, and template
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&requesteeId=%s", feedbackReq.getCreatorId(), feedbackReq.getRequesteeId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGetByCreatorRequesteeIdNotPermitted() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        //create two sample feedback requests by the same PDL
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        FeedbackRequest feedbackReq = createFeedbackRequest(pdlMemberProfile, memberOne, recipientOne, template.getId());
        createFeedbackRequest(pdlMemberProfileTwo, memberTwo, recipientTwo, template.getId());

        //search for feedback requests by a specific creator, requestee, and template
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&requesteeId=%s", feedbackReq.getCreatorId(), feedbackReq.getRequesteeId()))
                .basicAuth(recipientOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);

    }

    @Test
    void testGetByCreatorRequesteeIdMultiplePermitted() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();
        LocalDate now = LocalDate.now();
        //create two sample feedback requests by the same PDL and same requestee
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        FeedbackRequest feedbackReq = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientOne.getId(), template.getId(), now, null, "pending", null);

        FeedbackRequest feedbackReqTwo = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientTwo.getId(), template.getId(), now, null, "pending", null);
        getFeedbackRequestRepository().save(feedbackReq);
        getFeedbackRequestRepository().save(feedbackReqTwo);

        //search for feedback requests by a specific creator, requestee
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&requesteeId=%s", feedbackReq.getCreatorId(), feedbackReq.getRequesteeId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqTwo, response.getBody().get().get(1));
    }

    @Test
    void testGetLastThreeMonthsByCreator() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        UUID identicalTemplateId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);

        // create sample feedback requests with different send dates
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        FeedbackRequest feedbackReq = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientOne.getId(), template.getId(), now, null, "pending", null);
        FeedbackRequest feedbackReqTwo = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientTwo.getId(), template.getId(), withinLastFewMonths, null, "pending", null);
        FeedbackRequest feedbackReqThree = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientTwo.getId(), template.getId(), outOfRange, null, "pending", null);
        getFeedbackRequestRepository().save(feedbackReq);
        getFeedbackRequestRepository().save(feedbackReqTwo);
        getFeedbackRequestRepository().save(feedbackReqThree);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&oldestDate=%s", pdlMemberProfile.getId(), oldestDate))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqTwo, response.getBody().get().get(1));
    }

    @Test
    void testGetLastThreeMonthsByCreatorRequesteeId() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        // create sample feedback requests with different send dates
        FeedbackRequest feedbackReq = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientOne.getId(), template.getId(), now, null, "pending", null);
        FeedbackRequest feedbackReqTwo = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientTwo.getId(), template.getId(), withinLastFewMonths, null, "pending", null);
        FeedbackRequest feedbackReqThree = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientTwo.getId(), template.getId(), outOfRange, null, "pending", null);
        getFeedbackRequestRepository().save(feedbackReq);
        getFeedbackRequestRepository().save(feedbackReqTwo);
        getFeedbackRequestRepository().save(feedbackReqThree);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&requesteeId=%s&oldestDate=%s", pdlMemberProfile.getId(), memberOne.getId(), oldestDate))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqTwo, response.getBody().get().get(1));
    }

    @Test
    void testGetLastThreeMonthsByRequesteeId() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfile);

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);

        // create sample feedback requests with different send dates
        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        FeedbackRequest feedbackReq = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientOne.getId(), template.getId(), now, null, "pending", null);
        FeedbackRequest feedbackReqTwo = new FeedbackRequest(pdlMemberProfile.getId(), memberTwo.getId(), recipientTwo.getId(), template.getId(), withinLastFewMonths, null, "pending", null);
        FeedbackRequest feedbackReqThree = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientTwo.getId(), template.getId(), withinLastFewMonths, null, "pending", null);
        getFeedbackRequestRepository().save(feedbackReq);
        getFeedbackRequestRepository().save(feedbackReqTwo);
        getFeedbackRequestRepository().save(feedbackReqThree);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&requesteeId=%s&oldestDate=%s", feedbackReq.getCreatorId(), feedbackReq.getRequesteeId(), oldestDate))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqThree, response.getBody().get().get(1));
    }

    @Test
    void testGetEveryAllTimeAdmin() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultAdminRole(pdlMemberProfile);
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = LocalDate.of(2010, 10, 10);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);

        FeedbackTemplate template = getFeedbackTemplateRepository().save(createFeedbackTemplate(pdlMemberProfile.getId()));
        // create sample feedback requests with different send dates
        FeedbackRequest feedbackReq = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientOne.getId(), template.getId(), now, null, "pending", null);
        FeedbackRequest feedbackReqTwo = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientTwo.getId(), template.getId(), withinLastFewMonths, null, "pending", null);
        FeedbackRequest feedbackReqThree = new FeedbackRequest(pdlMemberProfile.getId(), memberOne.getId(), recipientTwo.getId(), template.getId(), outOfRange, null, "pending", null);
        getFeedbackRequestRepository().save(feedbackReq);
        getFeedbackRequestRepository().save(feedbackReqTwo);
        getFeedbackRequestRepository().save(feedbackReqThree);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?oldestDate=%s", oldestDate))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(3, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqTwo, response.getBody().get().get(1));
        assertResponseEqualsEntity(feedbackReqThree, response.getBody().get().get(2));
    }

    @Test
    void testUpdateDueDateAuthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        final FeedbackRequestUpdateDTO dto = new FeedbackRequestUpdateDTO();
        LocalDate newDueDate = LocalDate.now();

        dto.setId(feedbackReq.getId());
        dto.setDueDate(newDueDate);
        dto.setSubmitDate(null);
        dto.setStatus("Pending");

        feedbackReq.setDueDate(newDueDate);

        HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testUpdateDueDateUnauthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        final FeedbackRequestUpdateDTO dto = new FeedbackRequestUpdateDTO();
        LocalDate newDueDate = LocalDate.now();

        dto.setId(feedbackReq.getId());
        dto.setDueDate(newDueDate);
        dto.setSubmitDate(feedbackReq.getDueDate());
        dto.setStatus(feedbackReq.getStatus());

        feedbackReq.setDueDate(newDueDate);
        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUpdateStatusAndSubmitDateAuthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        final FeedbackRequestUpdateDTO dto = new FeedbackRequestUpdateDTO();
        LocalDate newSubmitDate = LocalDate.now();

        dto.setId(feedbackReq.getId());
        dto.setSubmitDate(newSubmitDate);
        dto.setStatus("Complete");
        dto.setDueDate(null);

        feedbackReq.setSubmitDate(newSubmitDate);
        feedbackReq.setStatus("Complete");

        HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testUpdateAllFieldsUnauthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, requestee, recipient);
        final FeedbackRequestUpdateDTO dto = new FeedbackRequestUpdateDTO();

        feedbackReq.setStatus("Complete");
        feedbackReq.setSubmitDate(LocalDate.now());
        feedbackReq.setDueDate(LocalDate.now());
        dto.setId(feedbackReq.getId());
        dto.setDueDate(feedbackReq.getDueDate());
        dto.setStatus(feedbackReq.getStatus());
        dto.setSubmitDate(feedbackReq.getSubmitDate());

        HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(requestee.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testAdminUpdatesAllFields() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile admin = createASecondDefaultMemberProfile();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        final FeedbackRequestUpdateDTO dto = new FeedbackRequestUpdateDTO();


        feedbackReq.setStatus("Complete");
        feedbackReq.setDueDate(LocalDate.now());
        feedbackReq.setSubmitDate(LocalDate.now());
        dto.setId(feedbackReq.getId());
        dto.setStatus(feedbackReq.getStatus());
        dto.setDueDate(feedbackReq.getDueDate());
        dto.setSubmitDate(feedbackReq.getSubmitDate());

        HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testDeleteFeedbackRequestByMember() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberTwo = createAnUnrelatedUser();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testDeleteByAdmin() {
        MemberProfile admin = createASecondDefaultMemberProfile();
        createDefaultAdminRole(admin);
        MemberProfile employeeMemberProfile = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(admin, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackRequestByPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackReqByUnassignedPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = createASecondDefaultMemberProfile();
        MemberProfile creator = createAnUnrelatedUser();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = createSampleFeedbackRequest(creator, memberOne, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }
}
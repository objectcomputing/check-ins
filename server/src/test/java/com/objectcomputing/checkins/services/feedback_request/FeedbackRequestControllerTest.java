package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.util.Util;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FeedbackRequestControllerTest extends TestContainersSuite implements RepositoryFixture, MemberProfileFixture, FeedbackTemplateFixture, FeedbackRequestFixture, RoleFixture {

    @Inject
    @Client("/services/feedback/requests")
    HttpClient client;

    private final EmailSender emailSender = mock(EmailSender.class);

    @Inject
    private FeedbackRequestServicesImpl feedbackRequestServicesImpl;

    @Property(name = FeedbackRequestServicesImpl.FEEDBACK_REQUEST_NOTIFICATION_SUBJECT) String notificationSubject;

    @Property(name = "check-ins.web-address") String submitURL;

    @Property(name = FeedbackRequestServicesImpl.WEB_UI_URL) String emailUrl;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(emailSender);
        feedbackRequestServicesImpl.setEmailSender(emailSender);
        createAndAssignRoles();
    }


    private String createEmailContent(FeedbackRequest storedRequest, UUID requestId, MemberProfile creator, MemberProfile requestee){
        String newContent = "<h1>You have received a feedback request.</h1>" +
                "<p><b>" + creator.getFirstName() + " " + creator.getLastName() + "</b> is requesting feedback on <b>" + requestee.getFirstName() + " " + requestee.getLastName() + "</b> from you.</p>";
        if (storedRequest.getDueDate() != null) {
            newContent += "<p>This request is due on " + storedRequest.getDueDate().getMonth() + " " + storedRequest.getDueDate().getDayOfMonth()+ ", " +storedRequest.getDueDate().getYear() + ".";
        }
        newContent+="<p>Please go to your unique link at " + emailUrl + "/feedback/submit?request=" +requestId + " to complete this request.</p>";
        return newContent;
    }
    private FeedbackRequest createFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        FeedbackTemplate template = createFeedbackTemplate(creator.getId());
        getFeedbackTemplateRepository().save(template);
        return createSampleFeedbackRequest(creator, requestee, recipient, template.getId());
    }

    private FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        final FeedbackRequest feedbackRequest = createFeedbackRequest(creator, requestee, recipient);
        return saveSampleFeedbackRequest(creator, requestee, recipient, feedbackRequest.getTemplateId());
    }

    private FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, LocalDate sendDate) {
        final FeedbackRequest feedbackRequest = createFeedbackRequest(creator, requestee, recipient);
        feedbackRequest.setSendDate(sendDate);
        return getFeedbackRequestRepository().save(feedbackRequest);
    }

    /**
     * Converts a {@link FeedbackRequest} to a {@link FeedbackRequestCreateDTO}
     * @param feedbackRequest {@link FeedbackRequest}
     * @return {@link FeedbackRequestCreateDTO}
     */
    private FeedbackRequestCreateDTO createDTO(FeedbackRequest feedbackRequest) {
        FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        dto.setCreatorId(feedbackRequest.getCreatorId());
        dto.setRequesteeId(feedbackRequest.getRequesteeId());
        dto.setRecipientId(feedbackRequest.getRecipientId());
        dto.setTemplateId(feedbackRequest.getTemplateId());
        dto.setSendDate(feedbackRequest.getSendDate());
        dto.setDueDate(feedbackRequest.getDueDate());
        dto.setStatus(feedbackRequest.getStatus());
        dto.setSubmitDate(feedbackRequest.getSubmitDate());
        return dto;
    }

    /**
     * Converts a {@link FeedbackRequest} to a {@link FeedbackRequestUpdateDTO}
     * @param feedbackRequest {@link FeedbackRequest}
     * @return {@link FeedbackRequestUpdateDTO}
     */
    private FeedbackRequestUpdateDTO updateDTO(FeedbackRequest feedbackRequest) {
        FeedbackRequestUpdateDTO dto = new FeedbackRequestUpdateDTO();
        dto.setId(feedbackRequest.getId());
        dto.setDueDate(feedbackRequest.getDueDate());
        dto.setStatus(feedbackRequest.getStatus());
        dto.setSubmitDate(feedbackRequest.getSubmitDate());
        return dto;
    }

    private void assertUnauthorized(HttpClientResponseException responseException) {
        assertEquals("You are not authorized to do this operation", responseException.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    private void assertResponseEqualsEntity(FeedbackRequest feedbackRequest, FeedbackRequestResponseDTO dto) {
        // If the feedback request is newly created, then only the response should have an ID
        if (feedbackRequest.getId() == null) {
            assertNotNull(dto.getId());
        } else {
            assertEquals(feedbackRequest.getId(), dto.getId());
        }
        assertEquals(feedbackRequest.getCreatorId(), dto.getCreatorId());
        assertEquals(feedbackRequest.getRequesteeId(), dto.getRequesteeId());
        assertEquals(feedbackRequest.getTemplateId(), dto.getTemplateId());
        assertEquals(feedbackRequest.getSendDate(), dto.getSendDate());
        assertEquals(feedbackRequest.getDueDate(), dto.getDueDate());
        assertEquals(feedbackRequest.getRecipientId(), dto.getRecipientId());
    }

    @Test
    void testCreateFeedbackRequestByAdmin() {
        //create two member profiles: one for normal employee, one for admin
        final MemberProfile memberProfile = createADefaultMemberProfile();
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("admin"));
        final MemberProfile recipient = createADefaultRecipient();
        assignAdminRole(admin);

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, memberProfile, recipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testCreateFeedbackRequestFailsWhenRecipientAndRequesteeAreSame() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, employeeMemberProfile);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateFeedbackRequestByAssignedPDL() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testCreateFeedbackRequestSendsEmail() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //verify appropriate email was sent
        assertTrue(response.getBody().isPresent());
        String correctContent = createEmailContent(feedbackRequest, response.getBody().get().getId(), pdlMemberProfile, employeeMemberProfile);
        verify(emailSender).sendEmail(notificationSubject, correctContent, recipient.getWorkEmail());
    }

    @Test
    void testCreateFeedbackRequestByUnassignedPdl() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createASecondDefaultMemberProfile();
        assignPdlRole(memberProfileForPDL);
        MemberProfile recipient = createAnUnrelatedUser();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(memberProfileForPDL, memberProfile, recipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

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
        final FeedbackRequest feedbackRequest = createFeedbackRequest(requesteeProfile, memberProfile, recipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(requesteeProfile.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testCreateFeedbackRequestWithInvalidCreatorId() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignAdminRole(admin);

        // Create feedback request with invalid creator ID
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, recipient);
        feedbackRequest.setCreatorId(UUID.randomUUID());
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Cannot save feedback request with invalid creator ID", responseException.getMessage());
    }

    @Test
    void testCreateFeedbackRequestWithInvalidRecipientId() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignAdminRole(admin);

        // Create feedback request with invalid recipient ID
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, recipient);
        feedbackRequest.setRecipientId(UUID.randomUUID());
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Cannot save feedback request with invalid recipient ID", responseException.getMessage());
    }

    @Test
    void testCreateFeedbackRequestWithInvalidRequesteeId() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignAdminRole(admin);

        // Create feedback request with invalid requestee ID
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, recipient);
        feedbackRequest.setRequesteeId(UUID.randomUUID());
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Cannot save feedback request with invalid requestee ID", responseException.getMessage());
    }

    @Test
    void testCreateFeedbackRequestWithNoRequestee() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignAdminRole(admin);

        // Create feedback request with no requestee
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, recipient);
        feedbackRequest.setRequesteeId(null);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("requestBody.requesteeId: must not be null", responseException.getMessage());
    }

    @Test
    void testCreateFeedbackRequestWithSameRecipientAndRequestee() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        assignAdminRole(admin);

        // Create feedback request with with same requestee and recipient ids
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, requestee);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("The requestee must not be the same person as the recipient", responseException.getMessage());
    }

    @Test
    void testCreateFeedbackRequestWithNoRecipients() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignAdminRole(admin);

        // Create feedback request with no recipient(s)
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, recipient);
        feedbackRequest.setRecipientId(null);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("requestBody.recipientId: must not be null", responseException.getMessage());
    }

    @Test
    void testCreateFeedbackRequestWithNoSelectedTemplate() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignAdminRole(admin);

        // Create feedback request with no template
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, recipient);
        feedbackRequest.setTemplateId(null);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("requestBody.templateId: must not be null", responseException.getMessage());
    }

    @Test
    void testCreateFeedbackRequestWithSendDateAfterDueDate() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        assignAdminRole(admin);

        // Create feedback request with invalid requestee ID
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, recipient);
        feedbackRequest.setSendDate(LocalDate.of(1111, 11, 2));
        feedbackRequest.setDueDate(LocalDate.of(1111, 11, 1));
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Send date of feedback request must be before the due date.", responseException.getMessage());
    }

    @Test
    void testGetFeedbackRequestByAdmin() {
        //Create users
        MemberProfile adminMemberProfile = createADefaultMemberProfile();
        MemberProfile pdlMemberProfile = createASecondDefaultMemberProfile();
        MemberProfile requesteeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipientMemberProfile = createADefaultRecipient();

        //Setup Roles and permissions for users
        assignAdminRole(adminMemberProfile);
        assignPdlRole(pdlMemberProfile);
        assignMemberRole(requesteeMemberProfile);
        assignMemberRole(recipientMemberProfile);

        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requesteeMemberProfile, recipientMemberProfile);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(adminMemberProfile.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetFeedbackRequestByAssignedPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, recipient);

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
        assignPdlRole(unrelatedPdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, recipient);

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
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberProfile2 = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);

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
        assignPdlRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, recipient);

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
        assignPdlRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile unrelatedUser = createAnUnrelatedUser();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, recipient);

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
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        // Create a feedback request from a PDL
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientOne);
        // Create a feedback request by a different PDL
        saveFeedbackRequest(pdlMemberProfileTwo, memberTwo, recipientTwo);

        //search for feedback requests by a specific creator
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s", feedbackReq.getCreatorId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
    }

    @Test
    void testGetByCreatorIdPermittedMultipleReqs() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);

        MemberProfile memberThree = createAThirdDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipient = createADefaultRecipient();

        // Create two sample feedback requests by the same PDL
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipient);
        FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberTwo, recipient);

        // Create a feedback request by a different PDL
        saveFeedbackRequest(pdlMemberProfileTwo, memberThree, recipient);

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
        assignPdlRole(pdl);
        MemberProfile employeeWithPdl = createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdl, employeeWithPdl, recipient);
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
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        //create two sample feedback requests by the same PDL
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientOne);
        saveFeedbackRequest(pdlMemberProfileTwo, memberTwo, recipientTwo);

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
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        //create two sample feedback requests by the same PDL
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientOne);
        saveFeedbackRequest(pdlMemberProfileTwo, memberTwo, recipientTwo);

        //search for feedback requests by a specific creator, requestee, and template
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&requesteeId=%s", feedbackReq.getCreatorId(), feedbackReq.getRequesteeId()))
                .basicAuth(recipientOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testGetByCreatorRecipientIdPermitted() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, recipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?recipientId=%s", feedbackRequest.getRecipientId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        // recipient must be able to get the feedback request
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetByCreatorRequesteeIdMultiplePermitted() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        //create two sample feedback requests by the same PDL and same requestee
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientOne);
        final FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientTwo);

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
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);

        // create sample feedback requests with different send dates
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientOne, now);
        final FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientTwo, withinLastFewMonths);
        saveFeedbackRequest(pdlMemberProfile, memberOne, recipientTwo, outOfRange);

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
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);

        // create sample feedback requests with different send dates
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientOne, now);
        final FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientTwo, withinLastFewMonths);
        saveFeedbackRequest(pdlMemberProfile, memberOne, recipientTwo, outOfRange);

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
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);

        // create sample feedback requests with different send dates and different requestees
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientOne, now);
        saveFeedbackRequest(pdlMemberProfile, memberTwo, recipientTwo, withinLastFewMonths);
        final FeedbackRequest feedbackReqThree = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientTwo, withinLastFewMonths);

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
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);
        MemberProfile pdlMemberProfile = createASecondDefaultMemberProfile();
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipientOne = createADefaultRecipient();
        MemberProfile recipientTwo = createASecondDefaultRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = LocalDate.of(2010, 10, 10);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);

        // create sample feedback requests with different send dates
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientOne, now);
        final FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientTwo, withinLastFewMonths);
        final FeedbackRequest feedbackReqThree = saveFeedbackRequest(pdlMemberProfile, memberOne, recipientTwo, outOfRange);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?oldestDate=%s", oldestDate))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
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
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setDueDate(LocalDate.now());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testUpdateDueDateToBeforeSendDate() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setSendDate(LocalDate.of(1111, 11, 2));
        feedbackReq.setDueDate(LocalDate.of(1111, 11, 1));
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Send date of feedback request must be before the due date.", responseException.getMessage());
    }

    @Test
    void testUpdateDueDateUnauthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setDueDate(Util.MAX.toLocalDate());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUpdateStatusAndSubmitDateAuthorizedByRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setStatus("complete");
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testUpdateStatusAuthorizedByCreator() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setStatus("canceled");
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testUpdateStatusNotAuthorized() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdl, requestee, recipient);
        feedbackReq.setStatus("complete");
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(requestee.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUpdateSubmitDateNotAuthorized() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdl, requestee, recipient);
        feedbackReq.setSubmitDate(LocalDate.now());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUpdateAllFieldsUnauthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, requestee, recipient);
        feedbackReq.setStatus("complete");
        feedbackReq.setDueDate(LocalDate.now());
        feedbackReq.setSubmitDate(LocalDate.now());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

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
        assignAdminRole(admin);

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setStatus("complete");
        feedbackReq.setDueDate(LocalDate.now());
        feedbackReq.setSubmitDate(LocalDate.now());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

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
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testDeleteByAdmin() {
        MemberProfile admin = createASecondDefaultMemberProfile();
        assignAdminRole(admin);
        MemberProfile employeeMemberProfile = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(admin, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE );
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackRequestByPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE );
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackReqByUnassignedPDL() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createASecondDefaultMemberProfile();
        MemberProfile creator = createAnUnrelatedUser();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(creator, memberOne, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testRecipientGetBeforeSendDateNotAuthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, requestee, recipient);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        getFeedbackRequestRepository().save(feedbackRequest);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        // the sendDate must be before the sent date
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("You are not permitted to access this request before the send date.", responseException.getMessage());
    }

    @Test
    void testRecipientGetBeforeSendDateAsAdminAuthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile adminUser = createAThirdDefaultMemberProfile();
        assignAdminRole(adminUser);

        // Save feedback request with send date in the future
        FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, requestee, recipient);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        getFeedbackRequestRepository().save(feedbackRequest);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(adminUser.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        // the sendDate must be before the sent date unless its an admin
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testRecipientGetBeforeSendDateAsPdlAuthorized() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        // Save feedback request with send date in the future
        FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, requestee, recipient);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        getFeedbackRequestRepository().save(feedbackRequest);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        // the sendDate must be before the sent date unless its an admin or the pdl who created it.
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

}

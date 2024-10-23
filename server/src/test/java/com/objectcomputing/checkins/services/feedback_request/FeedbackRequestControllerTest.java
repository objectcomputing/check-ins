package com.objectcomputing.checkins.services.feedback_request;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_external_recipient.FeedbackExternalRecipient;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackExternalRecipientFixture;
import com.objectcomputing.checkins.services.fixture.ReviewPeriodFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.EmailHelper;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
class FeedbackRequestControllerTest extends TestContainersSuite implements MemberProfileFixture, FeedbackTemplateFixture, FeedbackRequestFixture, RoleFixture, ReviewPeriodFixture, FeedbackExternalRecipientFixture {

    @Inject
    @Client("/services/feedback/requests")
    HttpClient client;

    @Inject
    @Client("/services/feedback/external/recipients")
    HttpClient clientExternalRecipient;

    @Inject
    CheckInsConfiguration checkInsConfiguration;

    @Inject
    @Named(MailJetFactory.MJML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @BeforeEach
    void resetMocks() {
        createAndAssignRoles();
        emailSender.reset();
    }

    /**
     * Converts a {@link FeedbackRequest} to a {@link FeedbackRequestCreateDTO}
     *
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
        dto.setExternalRecipientId(feedbackRequest.getExternalRecipientId());
        return dto;
    }

    /**
     * Converts a {@link FeedbackRequest} to a {@link FeedbackRequestUpdateDTO}
     *
     * @param feedbackRequest {@link FeedbackRequest}
     * @return {@link FeedbackRequestUpdateDTO}
     */
    private FeedbackRequestUpdateDTO updateDTO(FeedbackRequest feedbackRequest) {
        FeedbackRequestUpdateDTO dto = new FeedbackRequestUpdateDTO();
        dto.setId(feedbackRequest.getId());
        dto.setDueDate(feedbackRequest.getDueDate());
        dto.setStatus(feedbackRequest.getStatus());
        dto.setSubmitDate(feedbackRequest.getSubmitDate());
        dto.setRecipientId(feedbackRequest.getRecipientId());
        dto.setExternalRecipientId(feedbackRequest.getExternalRecipientId());
        return dto;
    }

    private void assertUnauthorized(HttpClientResponseException responseException) {
        assertEquals(NOT_AUTHORIZED_MSG, responseException.getMessage());
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
        assertEquals(feedbackRequest.getExternalRecipientId(), dto.getExternalRecipientId());
    }

    @Test
    void testCreateFeedbackRequestByAdminToRecipient() {
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
    void testCreateFeedbackRequestByAdminToExternalRecipient() {
        //create two member profiles: one for normal employee, one for admin
        final MemberProfile memberProfile = createADefaultMemberProfile();
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("admin"));
        final FeedbackExternalRecipient feedbackExternalRecipient = createADefaultFeedbackExternalRecipient();
        assignAdminRole(admin);

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, memberProfile, feedbackExternalRecipient);
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
    void testCreateFeedbackRequestByAssignedPdlToRecipient() {
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
    void testCreateFeedbackRequestByAssignedPdlToExternalRecipient() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient feedbackExternalRecipient = createADefaultFeedbackExternalRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, feedbackExternalRecipient);
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
    void testCreateFeedbackRequestSendsEmailNowToRecipient() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackRequest.setSendDate(LocalDate.now());
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        String fromName = pdlMemberProfile.getFirstName() + " " + pdlMemberProfile.getLastName();
        //verify appropriate email was sent
        assertTrue(response.getBody().isPresent());
        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL",
                                  fromName,
                                  pdlMemberProfile.getWorkEmail(),
                                  checkInsConfiguration.getApplication().getFeedback().getRequestSubject(),
                                  "You have received a feedback request",
                                  recipient.getWorkEmail(),
                                  emailSender.events.getFirst()
        );
    }

    @Test
    void testCreateFeedbackRequestSendsEmailNowToExternalRecipient() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient);
        feedbackRequest.setSendDate(LocalDate.now());
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        String fromName = pdlMemberProfile.getFirstName() + " " + pdlMemberProfile.getLastName();
        //verify appropriate email was sent
        assertTrue(response.getBody().isPresent());
        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL",
                fromName,
                pdlMemberProfile.getWorkEmail(),
                checkInsConfiguration.getApplication().getFeedback().getRequestSubject(),
                "You have received a feedback request",
                externalRecipient.getEmail(),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testCreateFeedbackRequestSendsEmailFutureToRecipient() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = createADefaultRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //verify appropriate email was not sent
        assertTrue(response.getBody().isPresent());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testCreateFeedbackRequestSendsEmailFutureToExternalRecipient() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        final MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //verify appropriate email was not sent
        assertTrue(response.getBody().isPresent());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testCreateFeedbackRequestByUnassignedPdlToRecipient() {
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
    void testCreateFeedbackRequestByUnassignedPdlToExternalRecipient() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createASecondDefaultMemberProfile();
        assignPdlRole(memberProfileForPDL);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(memberProfileForPDL, memberProfile, externalRecipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileForPDL.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testCreateFeedbackRequestByMemberToRecipient() {
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
    void testCreateFeedbackRequestByMemberToExternalRecipient() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile requesteeProfile = createASecondDefaultMemberProfile();
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(requesteeProfile, memberProfile, externalRecipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(requesteeProfile.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void testCreateFeedbackRequestWithInvalidCreatorIdToRecipient() {
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
    void testCreateFeedbackRequestWithInvalidCreatorIdToExternalRecipient() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        assignAdminRole(admin);

        // Create feedback request with invalid creator ID
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, externalRecipient);
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
    void testCreateFeedbackRequestWithInvalidExternalRecipientId() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        assignAdminRole(admin);

        // Create feedback request with invalid recipient ID
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, externalRecipient);
        feedbackRequest.setExternalRecipientId(UUID.randomUUID());
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Cannot save feedback request with invalid external-recipient ID", responseException.getMessage());
    }

    @Test
    void testCreateFeedbackRequestWithInvalidRequesteeIdToRecipient() {
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
    void testCreateFeedbackRequestWithInvalidRequesteeIdToExternalRecipient() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        assignAdminRole(admin);

        // Create feedback request with invalid requestee ID
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, externalRecipient);
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
    void testCreateFeedbackRequestWithNoRequesteeToRecipient() {
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

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message").asText();
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("requestBody.requesteeId: must not be null", error);
    }

    @Test
    void testCreateFeedbackRequestWithNoRequesteeToExternalRecipient() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        assignAdminRole(admin);

        // Create feedback request with no requestee
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, externalRecipient);
        feedbackRequest.setRequesteeId(null);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message").asText();
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("requestBody.requesteeId: must not be null", error);
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

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Cannot save feedback request without both recipient and external-recipient ID", error);
    }

    @Test
    void testCreateFeedbackRequestWithBothRecipientsAndExternalRecipients() {
        MemberProfile admin = createADefaultMemberProfile();
        MemberProfile requestee = createASecondDefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        assignAdminRole(admin);

        // Create feedback request with both a recipient and an external recipient
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, requestee, recipient);
        feedbackRequest.setExternalRecipientId(externalRecipient.getId());
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        // Post feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Cannot save feedback request with both recipient and external-recipient ID", error);
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

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message").asText();
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("requestBody.templateId: must not be null", error);
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
    void testGetFeedbackRequestByAdminToRecipient() {
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
    void testGetFeedbackRequestByAdminToExternalRecipient() {
        //Create users
        MemberProfile adminMemberProfile = createADefaultMemberProfile();
        MemberProfile pdlMemberProfile = createASecondDefaultMemberProfile();
        MemberProfile requesteeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();

        //Setup Roles and permissions for users
        assignAdminRole(adminMemberProfile);
        assignPdlRole(pdlMemberProfile);
        assignMemberRole(requesteeMemberProfile);

        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requesteeMemberProfile, externalRecipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(adminMemberProfile.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetFeedbackRequestByAssignedPDLToRecipient() {
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
    void testGetFeedbackRequestByAssignedPDLToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, externalRecipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetFeedbackRequestByUnassignedPdlToRecipient() {
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
    void testGetFeedbackRequestByUnassignedPdlToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile unrelatedPdl = createASecondDefaultMemberProfile();
        assignPdlRole(unrelatedPdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, externalRecipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(unrelatedPdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetFeedbackRequestByRequesteeToRecipient() {
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
    void testGetFeedbackRequestByRequesteeToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberProfile2 = createASecondDefaultMemberProfile();
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(memberProfile2.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
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
    void testGetFeedbackRequestByExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, externalRecipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()))
                .basicAuth(externalRecipient.getEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        // recipient must be able to get the feedback request
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetFeedbackRequestByReviewPeriodIdToRecipient() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignAdminRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest unrelatedRequest = saveFeedbackRequest(pdlMemberProfile, requestee, recipient);
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, recipient, reviewPeriod);

        //search for feedback requests by a specific creator
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?reviewPeriodId=%s", reviewPeriod.getId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get().get(0));
    }

    @Test
    void testGetFeedbackRequestByReviewPeriodIdToExternalRecipient() {
        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignAdminRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, externalRecipient, reviewPeriod);

        //search for feedback requests by a specific creator
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?reviewPeriodId=%s", reviewPeriod.getId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get().get(0));
    }

    @Test
    void testGetFeedbackRequestByUnrelatedUserToRecipient() {
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
    void testGetFeedbackRequestByUnrelatedUserToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        MemberProfile unrelatedUser = createAnUnrelatedUser();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, externalRecipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()));
        final HttpResponse<FeedbackRequestResponseDTO> response = clientExternalRecipient.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetByCreatorIdPermittedToRecipients() {
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
    void testGetByCreatorIdPermittedToExternalRecipients() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        final FeedbackExternalRecipient externalRecipient02 = createASecondDefaultFeedbackExternalRecipient();

        // Create a feedback request from a PDL
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01);
        // Create a feedback request by a different PDL
        saveFeedbackRequest(pdlMemberProfileTwo, memberTwo, externalRecipient02);

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
    void testGetByCreatorIdPermittedMultipleReqsToRecipients() {
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
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqTwo, response.getBody().get().get(1));
    }

    @Test
    void testGetByCreatorIdPermittedMultipleReqsToExternalRecipients() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile memberThree = createAThirdDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        // Create two sample feedback requests by the same PDL
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01);
        FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberTwo, externalRecipient01);
        // Create a feedback request by a different PDL
        saveFeedbackRequest(pdlMemberProfileTwo, memberThree, externalRecipient01);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s", feedbackReq.getCreatorId()))
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
    void testGetByCreatorRequesteeIdPermittedToRecipients() {
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
    void testGetByCreatorRequesteeIdPermittedToExternalRecipients() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        final FeedbackExternalRecipient externalRecipient02 = createASecondDefaultFeedbackExternalRecipient();

        //create two sample feedback requests by the same PDL
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01);
        saveFeedbackRequest(pdlMemberProfileTwo, memberTwo, externalRecipient02);

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
    void testGetByCreatorRequesteeIdNotPermittedToRecipients() {
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
                .basicAuth(recipientTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        ;

        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertTrue(response.getBody().isPresent());
        assertEquals(0, response.getBody().get().size());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGetByCreatorRequesteeIdNotPermittedToExternalRecipients() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        final FeedbackExternalRecipient externalRecipient02 = createASecondDefaultFeedbackExternalRecipient();

        //create two sample feedback requests by the same PDL
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01);
        saveFeedbackRequest(pdlMemberProfileTwo, memberTwo, externalRecipient02);

        //search for feedback requests by a specific creator, requestee, and template
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&requesteeId=%s", feedbackReq.getCreatorId(), feedbackReq.getRequesteeId()))
                .basicAuth(externalRecipient02.getEmail(), RoleType.Constants.MEMBER_ROLE);
        ;

        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertTrue(response.getBody().isPresent());
        assertEquals(0, response.getBody().get().size());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGetByCreatorRecipientIdPermittedToRecipients() {
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
    void testGetByCreatorRecipientIdPermittedToExternalRecipients() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, externalRecipient);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("/?externalRecipientId=%s", feedbackRequest.getExternalRecipientId()));
        final HttpResponse<FeedbackRequestResponseDTO> response = clientExternalRecipient.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        // recipient must be able to get the feedback request
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testGetByCreatorRequesteeIdMultiplePermittedToRecipients() {
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
    void testGetByCreatorRequesteeIdMultiplePermittedToExternalRecipients() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        final FeedbackExternalRecipient externalRecipient02 = createASecondDefaultFeedbackExternalRecipient();

        //create two sample feedback requests by the same PDL and same requestee
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01);
        final FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient02);

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
    void testGetLastThreeMonthsByCreatorToRecipients() {
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
    void testGetLastThreeMonthsByCreatorToExternalRecipients() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        final FeedbackExternalRecipient externalRecipient02 = createASecondDefaultFeedbackExternalRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);

        // create sample feedback requests with different send dates
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01, now);
        final FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient02, withinLastFewMonths);
        saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient02, outOfRange);

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
    void testGetLastThreeMonthsByCreatorRequesteeIdToRecipients() {
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
    void testGetLastThreeMonthsByCreatorRequesteeIdToExternalRecipients() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        final FeedbackExternalRecipient externalRecipient02 = createASecondDefaultFeedbackExternalRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);

        // create sample feedback requests with different send dates
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01, now);
        final FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient02, withinLastFewMonths);
        saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient02, outOfRange);

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
    void testGetLastThreeMonthsByRequesteeIdToRecipients() {
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
    void testGetLastThreeMonthsByRequesteeIdToExternalRecipients() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        final FeedbackExternalRecipient externalRecipient02 = createASecondDefaultFeedbackExternalRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = now.minusMonths(3);
        LocalDate withinLastFewMonths = now.minusMonths(2);

        // create sample feedback requests with different send dates and different requestees
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01, now);
        saveFeedbackRequest(pdlMemberProfile, memberTwo, externalRecipient02, withinLastFewMonths);
        final FeedbackRequest feedbackReqThree = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient02, withinLastFewMonths);

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
    void testGetEveryAllTimeAdminToRecipients() {
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
    void testGetEveryAllTimeAdminToExternalRecipients() {
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);
        MemberProfile pdlMemberProfile = createASecondDefaultMemberProfile();
        MemberProfile memberOne = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        final FeedbackExternalRecipient externalRecipient02 = createASecondDefaultFeedbackExternalRecipient();

        LocalDate now = LocalDate.now();
        LocalDate oldestDate = LocalDate.of(2010, 10, 10);
        LocalDate withinLastFewMonths = now.minusMonths(2);
        LocalDate outOfRange = now.minusMonths(10);

        // create sample feedback requests with different send dates
        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient01, now);
        final FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient02, withinLastFewMonths);
        final FeedbackRequest feedbackReqThree = saveFeedbackRequest(pdlMemberProfile, memberOne, externalRecipient02, outOfRange);

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
    void testUpdateDueDateAuthorizedToRecipient() {
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
    void testUpdateDueDateAuthorizedToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
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
    void testUpdateDueDateToBeforeSendDateToRecipient() {
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
    void testUpdateDueDateToBeforeSendDateToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
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
    void testUpdateDueDateUnauthorizedToRecipient() {
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
    void testUpdateDueDateUnauthorizedToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
        feedbackReq.setDueDate(Util.MAX.toLocalDate());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                clientExternalRecipient.toBlocking().exchange(request, Map.class));

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
    void testUpdateStatusAndSubmitDateAuthorizedByExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
        feedbackReq.setStatus("complete");
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto);
        final HttpResponse<FeedbackRequestResponseDTO> response = clientExternalRecipient.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testUpdateStatusAuthorizedByCreatorToRecipient() {
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
    void testUpdateStatusAuthorizedByCreatorToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
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
    void testUpdateStatusNotAuthorizedToRecipient() {
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
    void testUpdateStatusNotAuthorizedToExternalRecipient() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdl, requestee, externalRecipient01);
        feedbackReq.setStatus("complete");
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(requestee.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUpdateSubmitDateNotAuthorizedToRecipient() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = createADefaultRecipient();

        // Save feedback request that has not been submitted yet
        final FeedbackRequest feedbackReq = createFeedbackRequest(pdl, requestee, recipient);
        feedbackReq.setSubmitDate(null);
        getFeedbackRequestRepository().save(feedbackReq);

        // The PDL attempts to submit the feedback request
        feedbackReq.setSubmitDate(LocalDate.now());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUpdateSubmitDateNotAuthorizedToExternalRecipient() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        // Save feedback request that has not been submitted yet
        final FeedbackRequest feedbackReq = createFeedbackRequest(pdl, requestee, externalRecipient01);
        feedbackReq.setSubmitDate(null);
        getFeedbackRequestRepository().save(feedbackReq);

        // The PDL attempts to submit the feedback request
        feedbackReq.setSubmitDate(LocalDate.now());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUpdateSubmitDateWhenPdlEnablesEditsToRecipient() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = createADefaultRecipient();

        // Save feedback request that has already been submitted
        final FeedbackRequest feedbackReq = createFeedbackRequest(pdl, requestee, recipient);
        feedbackReq.setSubmitDate(LocalDate.now());
        getFeedbackRequestRepository().save(feedbackReq);

        // The PDL attempts to enable edits on the request
        feedbackReq.setSubmitDate(null);
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testUpdateSubmitDateWhenPdlEnablesEditsToExternalRecipient() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        // Save feedback request that has already been submitted
        final FeedbackRequest feedbackReq = createFeedbackRequest(pdl, requestee, externalRecipient01);
        feedbackReq.setSubmitDate(LocalDate.now());
        getFeedbackRequestRepository().save(feedbackReq);

        // The PDL attempts to enable edits on the request
        feedbackReq.setSubmitDate(null);
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testFeedbackRequestEnableEditsSendsEmailToRecipient() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = createADefaultRecipient();

        // Save feedback request that has already been submitted
        final FeedbackRequest feedbackReq = createFeedbackRequest(pdl, requestee, recipient);
        feedbackReq.setSubmitDate(LocalDate.now());
        feedbackReq.setStatus("submitted");
        getFeedbackRequestRepository().save(feedbackReq);

        // The PDL attempts to enable edits on the request
        feedbackReq.setSubmitDate(null);
        feedbackReq.setStatus("sent");
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        String fromName = pdl.getFirstName() + " " + pdl.getLastName();
        // Verify appropriate email was sent
        assertTrue(response.getBody().isPresent());
        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL",
                                  fromName,
                                  pdl.getWorkEmail(),
                                  checkInsConfiguration.getApplication().getFeedback().getRequestSubject(),
                                  "You have received edit access to a feedback request",
                                  recipient.getWorkEmail(),
                                  emailSender.events.getFirst()
        );
    }

    @Test
    void testFeedbackRequestEnableEditsSendsEmailToExternalRecipient() {
        MemberProfile pdl = createADefaultMemberProfile();
        assignPdlRole(pdl);
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdl);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        // Save feedback request that has already been submitted
        final FeedbackRequest feedbackReq = createFeedbackRequest(pdl, requestee, externalRecipient01);
        feedbackReq.setSubmitDate(LocalDate.now());
        feedbackReq.setStatus("submitted");
        getFeedbackRequestRepository().save(feedbackReq);

        // The PDL attempts to enable edits on the request
        feedbackReq.setSubmitDate(null);
        feedbackReq.setStatus("sent");
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdl.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        String fromName = pdl.getFirstName() + " " + pdl.getLastName();
        // Verify appropriate email was sent
        assertTrue(response.getBody().isPresent());
        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL",
                fromName,
                pdl.getWorkEmail(),
                checkInsConfiguration.getApplication().getFeedback().getRequestSubject(),
                "You have received edit access to a feedback request",
                externalRecipient01.getEmail(),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testUpdateAllFieldsUnauthorizedToRecipient() {
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
    void testUpdateAllFieldsUnauthorizedToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, requestee, externalRecipient01);
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
    void testAdminUpdatesAllFieldsToRecipient() {
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
    void testAdminUpdatesAllFieldsToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        MemberProfile admin = createASecondDefaultMemberProfile();
        assignAdminRole(admin);

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
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
    void testAdminCancelsFeedbackRequestToRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        MemberProfile admin = createASecondDefaultMemberProfile();
        assignAdminRole(admin);

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setStatus("canceled");

        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testAdminCancelsFeedbackRequestToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        MemberProfile admin = createASecondDefaultMemberProfile();
        assignAdminRole(admin);

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
        feedbackReq.setStatus("canceled");

        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get());
    }

    @Test
    void testCreatorCancelsFeedbackRequestToRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
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
    void testCreatorCancelsFeedbackRequestToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
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
    void testCreatorCancelsSubmittedFeedbackRequestToRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        FeedbackTemplate template = createFeedbackTemplate(pdlMemberProfile.getId());
        getFeedbackTemplateRepository().save(template);
        final FeedbackRequest submittedFeedbackRequest = saveSampleFeedbackRequestWithStatus(pdlMemberProfile, employeeMemberProfile, recipient, template.getId(), "submitted");

        submittedFeedbackRequest.setStatus("canceled");

        final FeedbackRequestUpdateDTO dto = updateDTO(submittedFeedbackRequest);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Attempted to cancel a feedback request that was already submitted", responseException.getMessage());
    }

    @Test
    void testCreatorCancelsSubmittedFeedbackRequestToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        FeedbackTemplate template = createFeedbackTemplate(pdlMemberProfile.getId());
        getFeedbackTemplateRepository().save(template);
        final FeedbackRequest submittedFeedbackRequest = saveSampleFeedbackRequestWithStatus(pdlMemberProfile, employeeMemberProfile, externalRecipient01, template.getId(), "submitted");

        submittedFeedbackRequest.setStatus("canceled");

        final FeedbackRequestUpdateDTO dto = updateDTO(submittedFeedbackRequest);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Attempted to cancel a feedback request that was already submitted", responseException.getMessage());
    }

    @Test
    void testUnauthorizedCancelsFeedbackRequestToRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setStatus("canceled");

        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(employeeMemberProfile.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUnauthorizedCancelsFeedbackRequestToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
        feedbackReq.setStatus("canceled");

        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(employeeMemberProfile.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testDeleteFeedbackRequestByMemberToRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberTwo = createAnUnrelatedUser();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testDeleteFeedbackRequestByMemberToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberTwo = createAnUnrelatedUser();
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);

        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(memberTwo.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testDeleteByAdminToRecipient() {
        MemberProfile admin = createASecondDefaultMemberProfile();
        assignAdminRole(admin);
        MemberProfile employeeMemberProfile = createADefaultMemberProfile();
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(admin, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteByAdminToExternalRecipient() {
        MemberProfile admin = createASecondDefaultMemberProfile();
        assignAdminRole(admin);
        MemberProfile employeeMemberProfile = createADefaultMemberProfile();
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(admin, employeeMemberProfile, externalRecipient01);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackRequestByPDLToRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackRequestByPDLToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, externalRecipient01);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackReqByUnassignedPDLToRecipient() {
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
    void testDeleteFeedbackReqByUnassignedPDLToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createASecondDefaultMemberProfile();
        MemberProfile creator = createAnUnrelatedUser();
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(creator, memberOne, externalRecipient01);
        getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testRecipientGetBeforeSendDateNotAuthorizedToRecipient() {
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
    void testRecipientGetBeforeSendDateNotAuthorizedToExternalRecipient() {
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(pdlMemberProfile);
        final FeedbackExternalRecipient externalRecipient01 = createADefaultFeedbackExternalRecipient();
        FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, requestee, externalRecipient01);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        getFeedbackRequestRepository().save(feedbackRequest);

        //get feedback request
        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", feedbackRequest.getId()));
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                clientExternalRecipient.toBlocking().exchange(request, Map.class));

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

    @Test
    void testGetMultipleRequesteesByAdmin() {
        MemberProfile adminMemberProfile = createAnUnrelatedUser();
        assignAdminRole(adminMemberProfile);

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
        saveFeedbackRequest(pdlMemberProfile, memberThree, recipient);

        // Create a feedback request by a different PDL
        saveFeedbackRequest(pdlMemberProfileTwo, memberThree, recipient);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?requesteeIds=%s&requesteeIds=%s", memberOne.getId(), memberTwo.getId()))
                .basicAuth(adminMemberProfile.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqTwo, response.getBody().get().get(1));
    }

    @Test
    void testGetMultipleRequesteesByPDL() {
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
        saveFeedbackRequest(pdlMemberProfile, memberThree, recipient);

        // Create a feedback request by a different PDL
        saveFeedbackRequest(pdlMemberProfileTwo, memberThree, recipient);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?creatorId=%s&requesteeIds=%s&requesteeIds=%s", pdlMemberProfile.getId(), memberOne.getId(), memberTwo.getId()))
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
    void testGetMultipleRequesteesBySupervisor() {
        MemberProfile supervisor = createADefaultSupervisor();
        MemberProfile anotherSupervisor = createAnotherSupervisor();

        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        assignPdlRole(pdlMemberProfile);
        MemberProfile memberOne = createAProfileWithSupervisorAndPDL(supervisor, pdlMemberProfile);

        MemberProfile pdlMemberProfileTwo = createASecondDefaultMemberProfile();
        assignPdlRole(pdlMemberProfileTwo);
        MemberProfile memberTwo = createAnotherProfileWithSupervisorAndPDL(supervisor, pdlMemberProfileTwo);

        MemberProfile memberThree = createYetAnotherProfileWithSupervisorAndPDL(anotherSupervisor, pdlMemberProfileTwo);
        MemberProfile recipient = createADefaultRecipient();

        // Create two sample feedback requests by the same PDL
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, memberOne, recipient);
        FeedbackRequest feedbackReqTwo = saveFeedbackRequest(pdlMemberProfile, memberTwo, recipient);
        saveFeedbackRequest(pdlMemberProfileTwo, memberThree, recipient);

        // Create a feedback request by a different PDL
        saveFeedbackRequest(pdlMemberProfileTwo, memberThree, recipient);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?requesteeIds=%s&requesteeIds=%s", memberOne.getId(), memberTwo.getId()))
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<FeedbackRequestResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(FeedbackRequestResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());
        assertResponseEqualsEntity(feedbackReq, response.getBody().get().get(0));
        assertResponseEqualsEntity(feedbackReqTwo, response.getBody().get().get(1));
    }
}

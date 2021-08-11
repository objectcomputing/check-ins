package com.objectcomputing.checkins.services.feedback_request;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.testing.json.MockJsonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import com.objectcomputing.checkins.gcp.chat.GoogleChatBot;
import com.objectcomputing.checkins.notifications.all_types.NotificationSender;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.fixture.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.gcp.GoogleCloudConfiguration;
import io.micronaut.gcp.pubsub.annotation.PubSubListener;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;

import java.time.LocalDate;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FeedbackRequestControllerTest {

    @Inject
    @Client("/services/feedback/requests")
    HttpClient client;


    @Inject
    private FeedbackRequestServicesImpl feedbackRequestServicesImpl;
    private RepositoryFixture repositoryFixture;
    private MemberProfileFixture memberProfileFixture;
    private FeedbackTemplateFixture feedbackTemplateFixture;
    private FeedbackRequestFixture feedbackRequestFixture;
    private RoleFixture roleFixture;

    final static JsonFactory jsonFactory = new MockJsonFactory();

    @Mock
    private Authentication authentication;

    @Mock
    private Map mockAttributes;

    @Mock
    private PubsubMessage pubsubMessage;

    @Mock
    private MessageReceiver messageReceiver;

    @Mock
    private Publisher publisher;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private GoogleApiAccess mockGoogleApiAccess;

    @Mock
    private CurrentUserServices currentUserServices;

    @Mock
    private MemberProfileServices memberProfileServices;

    @Mock
    private CompletedFileUpload completedFileUpload;

    @Mock
    private MemberProfile testMemberProfile;

    @Mock
    private GoogleServiceConfiguration googleServiceConfiguration;

    @Mock
    private GoogleCloudConfiguration googleCloudConfiguration;

    @Mock
    private OAuth2Credentials oAuth2Credentials;

    @Mock
    private CredentialsProvider credentialsProvider;

    @Mock
    private GoogleCredentials credentials;

    @Mock
    private NotificationSender notificationSender;

    @Mock
    private EmailSender emailSender;

    @Mock
    private GoogleChatBot googleChatBot;

    @InjectMocks
    private FeedbackRequestServicesImpl feedbackRequestServices;

    @BeforeAll
    void initMocks() throws IOException {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(authentication);
        Mockito.reset(mockAttributes);
        Mockito.reset(mockInputStream);
        Mockito.reset(mockGoogleApiAccess);
        Mockito.reset(currentUserServices);
        Mockito.reset(memberProfileServices);
        Mockito.reset(completedFileUpload);
        Mockito.reset(googleServiceConfiguration);
        Mockito.reset(oAuth2Credentials);
        Mockito.reset(credentialsProvider);
        Mockito.reset(credentials);
        Mockito.reset(testMemberProfile);
        Mockito.reset(googleCloudConfiguration);
        Mockito.reset(messageReceiver);
        Mockito.reset(publisher);
        Mockito.reset(pubsubMessage);
        Mockito.reset(emailSender);
        Mockito.reset(googleChatBot);
        Mockito.reset(notificationSender);
        googleChatBot.setCredentials(credentials);
        notificationSender.setEmailSender(emailSender);
        notificationSender.setGoogleChatBot(googleChatBot);
        feedbackRequestServices.setNotificationSender(notificationSender);
        when(authentication.getAttributes()).thenReturn(mockAttributes);
        when(mockAttributes.get("email")).thenReturn(mockAttributes);
        when(mockAttributes.toString()).thenReturn("test.email");
        when(currentUserServices.findOrSaveUser(any(), any(), any())).thenReturn(testMemberProfile);
    }

    @Property(name = FeedbackRequestServicesImpl.FEEDBACK_REQUEST_NOTIFICATION_SUBJECT) String notificationSubject;
    @Property(name = FeedbackRequestServicesImpl.FEEDBACK_REQUEST_NOTIFICATION_CONTENT) String notificationContent;


    private FeedbackRequest createFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        FeedbackTemplate template = feedbackTemplateFixture.createFeedbackTemplate(creator.getId());
        repositoryFixture.getFeedbackTemplateRepository().save(template);
        return feedbackRequestFixture.createSampleFeedbackRequest(creator, requestee, recipient, template.getId());
    }

    private FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        final FeedbackRequest feedbackRequest = createFeedbackRequest(creator, requestee, recipient);
        return feedbackRequestFixture.saveSampleFeedbackRequest(creator, requestee, recipient, feedbackRequest.getTemplateId());
    }

    private FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, LocalDate sendDate) {
        final FeedbackRequest feedbackRequest = createFeedbackRequest(creator, requestee, recipient);
        feedbackRequest.setSendDate(sendDate);
        return repositoryFixture.getFeedbackRequestRepository().save(feedbackRequest);
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
        final MemberProfile memberProfile = memberProfileFixture.createADefaultMemberProfile();
        final MemberProfile admin = repositoryFixture.getMemberProfileRepository().save(mkMemberProfile("admin"));
        final MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        roleFixture.createDefaultAdminRole(admin);

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(admin, memberProfile, recipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        verify(notificationSender).sendNotification(notificationSubject, "You have received a feedback request. Please go to the <a href=\"https://checkins.objectcomputing.com/feedback/submit?requestId="+response.getBody().get().getId()+"\">Check-Ins application</a>.", recipient.getWorkEmail());

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertResponseEqualsEntity(feedbackRequest, response.getBody().get());
    }

    @Test
    void testCreateFeedbackRequestByAssignedPDL() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        final MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        final MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

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
        final MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        final MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        final MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //verify appropriate email was sent
        assertTrue(response.getBody().isPresent());
        verify(emailSender).sendEmail(notificationSubject, "You have received a feedback request. Please go to the <a href=\"https://checkins.objectcomputing.com/feedback/submit?requestId="+response.getBody().get().getId()+"\">Check-Ins application</a>.", recipient.getWorkEmail());
    }

    @Test
    void testCreateFeedbackRequestByUnassignedPdl() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, memberProfileForPDL);
        MemberProfile recipient = memberProfileFixture.createAnUnrelatedUser();

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
        MemberProfile memberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile requesteeProfile = memberProfileFixture.createASecondDefaultMemberProfile();
        MemberProfile recipient = memberProfileFixture.createAnUnrelatedUser();

        //create feedback request
        final FeedbackRequest feedbackRequest = createFeedbackRequest(requesteeProfile, memberProfile, recipient);
        final FeedbackRequestCreateDTO dto = createDTO(feedbackRequest);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(requesteeProfile.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testCreateFeedbackRequestWithInvalidCreatorId() {
        MemberProfile admin = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile requestee = memberProfileFixture.createASecondDefaultMemberProfile();
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        roleFixture.createDefaultAdminRole(admin);

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
        MemberProfile admin =   memberProfileFixture.createADefaultMemberProfile();
        MemberProfile requestee = memberProfileFixture.createASecondDefaultMemberProfile();
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        roleFixture.createDefaultAdminRole(admin);

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
        MemberProfile admin = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile requestee = memberProfileFixture.createASecondDefaultMemberProfile();
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        roleFixture.createDefaultAdminRole(admin);

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
    void testGetFeedbackRequestByAdmin() {
        MemberProfile admin = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultAdminRole(admin);
        MemberProfile pdlMemberProfile = memberProfileFixture.createASecondDefaultMemberProfile();

        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        FeedbackRequest feedbackRequest = saveFeedbackRequest(pdlMemberProfile, requestee, recipient);

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile unrelatedPdl = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, unrelatedPdl);
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberProfile2 = memberProfileFixture.createASecondDefaultMemberProfile();
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile unrelatedUser = memberProfileFixture.createAnUnrelatedUser();
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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile memberTwo = memberProfileFixture.createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile recipientTwo = feedbackRequestFixture.createASecondDefaultRecipient();

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberTwo = memberProfileFixture.createASecondDefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberThree = memberProfileFixture.createAThirdDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

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
        MemberProfile pdl = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdl);
        MemberProfile employeeWithPdl = memberProfileFixture.createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile memberTwo = memberProfileFixture.createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile recipientTwo = feedbackRequestFixture.createASecondDefaultRecipient();

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile memberTwo = memberProfileFixture.createASecondDefaultMemberProfileForPdl(pdlMemberProfileTwo);
        MemberProfile recipientOne = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile recipientTwo = feedbackRequestFixture.createASecondDefaultRecipient();

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
    void testGetByCreatorRequesteeIdMultiplePermitted() {
        //create two employee-PDL relationships
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile recipientOne = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile recipientTwo = feedbackRequestFixture.createASecondDefaultRecipient();

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile recipientOne = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile recipientTwo = feedbackRequestFixture.createASecondDefaultRecipient();

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipientOne = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile recipientTwo = feedbackRequestFixture.createASecondDefaultRecipient();

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile pdlMemberProfileTwo = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfileTwo);
        MemberProfile recipientOne = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile recipientTwo = feedbackRequestFixture.createASecondDefaultRecipient();
        MemberProfile memberTwo = memberProfileFixture.createASecondDefaultMemberProfileForPdl(pdlMemberProfile);

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
        MemberProfile admin = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultAdminRole(admin);
        MemberProfile pdlMemberProfile = memberProfileFixture.createASecondDefaultMemberProfile();
        MemberProfile memberOne = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipientOne = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile recipientTwo = feedbackRequestFixture.createASecondDefaultRecipient();

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

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
    void testUpdateDueDateUnauthorized() {
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setDueDate(LocalDate.now());
        final FeedbackRequestUpdateDTO dto = updateDTO(feedbackReq);

        final HttpRequest<?> request = HttpRequest.PUT("", dto)
                .basicAuth(recipient.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testUpdateStatusAndSubmitDateAuthorizedByRecipient() {
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

        final FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        feedbackReq.setStatus("complete");
        feedbackReq.setDueDate(null);
        feedbackReq.setSubmitDate(LocalDate.now());
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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

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
        MemberProfile pdl = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdl);
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

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
        MemberProfile pdl = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdl);
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdl);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile admin = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultAdminRole(admin);

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile memberTwo = memberProfileFixture.createAnUnrelatedUser();
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        repositoryFixture.getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(memberTwo.getWorkEmail(), MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testDeleteByAdmin() {
        MemberProfile admin = memberProfileFixture.createASecondDefaultMemberProfile();
        roleFixture.createDefaultAdminRole(admin);
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(admin, employeeMemberProfile, recipient);
        repositoryFixture.getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE );
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackRequestByPDL() {
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(pdlMemberProfile, employeeMemberProfile, recipient);
        repositoryFixture.getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE );
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteFeedbackReqByUnassignedPDL() {
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        roleFixture.createDefaultRole(RoleType.PDL, pdlMemberProfile);
        MemberProfile memberOne = memberProfileFixture.createASecondDefaultMemberProfile();
        MemberProfile creator = memberProfileFixture.createAnUnrelatedUser();
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        FeedbackRequest feedbackReq = saveFeedbackRequest(creator, memberOne, recipient);
        repositoryFixture.getFeedbackRequestRepository().save(feedbackReq);
        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", feedbackReq.getId())).basicAuth(pdlMemberProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertUnauthorized(responseException);
    }

    @Test
    void testRecipientGetBeforeSendDateNotAuthorized() {
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, requestee, recipient);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        repositoryFixture.getFeedbackRequestRepository().save(feedbackRequest);

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();
        MemberProfile adminUser = memberProfileFixture.createAThirdDefaultMemberProfile();
        roleFixture.createDefaultAdminRole(adminUser);

        // Save feedback request with send date in the future
        FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, requestee, recipient);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        repositoryFixture.getFeedbackRequestRepository().save(feedbackRequest);

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
        MemberProfile pdlMemberProfile = memberProfileFixture.createADefaultMemberProfile();
        MemberProfile requestee = memberProfileFixture.createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = feedbackRequestFixture.createADefaultRecipient();

        // Save feedback request with send date in the future
        FeedbackRequest feedbackRequest = createFeedbackRequest(pdlMemberProfile, requestee, recipient);
        feedbackRequest.setSendDate(LocalDate.now().plusDays(1));
        repositoryFixture.getFeedbackRequestRepository().save(feedbackRequest);

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

package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.reviews.ReviewAssignment;
import com.objectcomputing.checkins.services.reviews.ReviewAssignmentRepository;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodRepository;
import com.objectcomputing.checkins.services.EmailHelper;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
// Disabled in nativeTest, as we get an exception from Mockito
//     => java.lang.NoClassDefFoundError: Could not initialize class org.mockito.Mockito
@DisabledInNativeImage
class FeedbackRequestTest extends TestContainersSuite {

    @Mock
    private FeedbackRequestRepository feedbackReqRepository;

    @Mock
    private CurrentUserServices currentUserServices;

    @Mock
    private MemberProfileServices memberProfileServices;

    @Mock
    private ReviewPeriodRepository reviewPeriodRepository;

    @Mock
    private ReviewAssignmentRepository reviewAssignmentRepository;

    private FeedbackRequestServicesImpl feedbackRequestServices;

    @Inject
    @Named(MailJetFactory.MJML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    private EmbeddedServer server;

    private AutoCloseable mockFinalizer;

    @Inject
    CheckInsConfiguration checkInsConfiguration;

    @BeforeAll
    void initMocks() {
        mockFinalizer = openMocks(this);
        feedbackRequestServices = new FeedbackRequestServicesImpl(feedbackReqRepository, currentUserServices, memberProfileServices, reviewPeriodRepository, reviewAssignmentRepository, emailSender, checkInsConfiguration);
        server.getApplicationContext().inject(feedbackRequestServices);
    }

    @BeforeEach
    @Tag("mocked")
    void setUp() {
        Mockito.reset(feedbackReqRepository);
        Mockito.reset(currentUserServices);
        Mockito.reset(memberProfileServices);
        Mockito.reset(reviewPeriodRepository);
        Mockito.reset(reviewAssignmentRepository);
        emailSender.reset();
    }

    @AfterAll
    void cleanupMocks() throws Exception {
        mockFinalizer.close();
    }

    @Test
    @Tag("mocked")
    void testUpdateFeedbackRequest() {
        UUID feedbackRequestId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID requesteeId = UUID.randomUUID();

        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setId(feedbackRequestId);
        feedbackRequest.setCreatorId(creatorId);
        feedbackRequest.setRecipientId(recipientId);
        feedbackRequest.setRequesteeId(requesteeId);
        feedbackRequest.setSendDate(LocalDate.now());
        feedbackRequest.setStatus("sent");

        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(creatorId);
        FeedbackRequestUpdateDTO updateDTO = new FeedbackRequestUpdateDTO();
        updateDTO.setId(feedbackRequest.getId());
        updateDTO.setDueDate(LocalDate.now().plusDays(7));
        updateDTO.setStatus("submitted");
        updateDTO.setRecipientId(feedbackRequest.getRecipientId());

        when(feedbackReqRepository.findById(feedbackRequest.getId())).thenReturn(Optional.of(feedbackRequest));
        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(currentUserServices.isAdmin()).thenReturn(true);
        when(feedbackReqRepository.update(any(FeedbackRequest.class))).thenReturn(feedbackRequest);
        when(memberProfileServices.getById(any(UUID.class))).thenReturn(new MemberProfile());

        FeedbackRequest updatedFeedbackRequest = feedbackRequestServices.update(updateDTO);

        assertNotNull(updatedFeedbackRequest);
        assertEquals("submitted", updatedFeedbackRequest.getStatus());
        verify(feedbackReqRepository, times(1)).update(any(FeedbackRequest.class));
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testUpdateFeedbackRequest_NotFound() {
        UUID feedbackRequestId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID requesteeId = UUID.randomUUID();

        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setId(feedbackRequestId);
        feedbackRequest.setCreatorId(creatorId);
        feedbackRequest.setRecipientId(recipientId);
        feedbackRequest.setRequesteeId(requesteeId);
        feedbackRequest.setSendDate(LocalDate.now());
        feedbackRequest.setStatus("sent");

        FeedbackRequestUpdateDTO updateDTO = new FeedbackRequestUpdateDTO();
        updateDTO.setId(feedbackRequest.getId());

        when(feedbackReqRepository.findById(feedbackRequest.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> feedbackRequestServices.update(updateDTO));
        verify(feedbackReqRepository, never()).update(any(FeedbackRequest.class));
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testUpdateFeedbackRequest_Unauthorized() {
        UUID feedbackRequestId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID requesteeId = UUID.randomUUID();

        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setId(feedbackRequestId);
        feedbackRequest.setCreatorId(creatorId);
        feedbackRequest.setRecipientId(recipientId);
        feedbackRequest.setRequesteeId(requesteeId);
        feedbackRequest.setSendDate(LocalDate.now());
        feedbackRequest.setStatus("sent");

        FeedbackRequestUpdateDTO updateDTO = new FeedbackRequestUpdateDTO();
        updateDTO.setId(feedbackRequest.getId());
        updateDTO.setDueDate(LocalDate.now().plusDays(7));
        updateDTO.setStatus("submitted");
        updateDTO.setRecipientId(feedbackRequest.getRecipientId());

        MemberProfile requestee = new MemberProfile();
        requestee.setId(requesteeId);

        MemberProfile currentMemberProfile = new MemberProfile();
        currentMemberProfile.setId(UUID.randomUUID());
        when(feedbackReqRepository.findById(feedbackRequest.getId())).thenReturn(Optional.of(feedbackRequest));
        when(currentUserServices.getCurrentUser()).thenReturn(currentMemberProfile);
        when(currentUserServices.isAdmin()).thenReturn(false);
        when(memberProfileServices.getById(requesteeId)).thenReturn(requestee);

        assertThrows(PermissionException.class, () -> feedbackRequestServices.update(updateDTO));
        verify(feedbackReqRepository, never()).update(any(FeedbackRequest.class));
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmailToReviewers() {
        UUID reviewAssignmentId;
        ReviewAssignment reviewAssignment;
        UUID creatorId = UUID.randomUUID();
        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(creatorId);

        MemberProfile pdlProfile = new MemberProfile();
        pdlProfile.setId(UUID.randomUUID());
        pdlProfile.setFirstName("PDL");
        pdlProfile.setLastName("Profile");
        pdlProfile.setWorkEmail("pdl@example.com");

        MemberProfile supervisorProfile = new MemberProfile();
        supervisorProfile.setId(UUID.randomUUID());
        supervisorProfile.setFirstName("Supervisor");
        supervisorProfile.setLastName("Profile");
        supervisorProfile.setWorkEmail("supervisor@example.com");

        currentUser.setPdlId(pdlProfile.getId());
        currentUser.setSupervisorid(supervisorProfile.getId());

        MemberProfile reviewer01 = new MemberProfile();
        reviewer01.setId(UUID.randomUUID());
        reviewer01.setFirstName("Reviewer01");
        reviewer01.setLastName("Profile");
        reviewer01.setWorkEmail("reviewer01@example.com");

        MemberProfile reviewer02 = new MemberProfile();
        reviewer02.setId(UUID.randomUUID());
        reviewer02.setFirstName("Reviewer02");
        reviewer02.setLastName("Profile");
        reviewer02.setWorkEmail("reviewer02@example.com");

        ReviewPeriod reviewPeriod = new ReviewPeriod();
        reviewPeriod.setName("Self-Review Test");

        String firstName = "firstName";
        String lastName = "lastName";

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);

        UUID reviewPeriodId = UUID.randomUUID();
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setReviewPeriodId(reviewPeriodId);

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(memberProfileServices.getById(pdlProfile.getId())).thenReturn(pdlProfile);
        when(memberProfileServices.getById(supervisorProfile.getId())).thenReturn(supervisorProfile);
        when(memberProfileServices.getById(reviewer01.getId())).thenReturn(reviewer01);
        when(memberProfileServices.getById(reviewer02.getId())).thenReturn(reviewer02);
        when(reviewPeriodRepository.findById(reviewPeriodId)).thenReturn(Optional.of(reviewPeriod));

        Set<ReviewAssignment> reviewAssignmentsSet = new HashSet<ReviewAssignment>();
        reviewAssignmentId = UUID.randomUUID();
        reviewAssignment= new ReviewAssignment();
        reviewAssignment.setId(reviewAssignmentId);
        reviewAssignment.setReviewPeriodId(reviewPeriodId);
        reviewAssignment.setReviewerId(reviewer01.getId());
        reviewAssignment.setRevieweeId(currentUser.getId());
        reviewAssignmentsSet.add(reviewAssignment);
        reviewAssignmentId = UUID.randomUUID();
        reviewAssignment= new ReviewAssignment();
        reviewAssignment.setId(reviewAssignmentId);
        reviewAssignment.setReviewPeriodId(reviewPeriodId);
        reviewAssignment.setReviewerId(reviewer02.getId());
        reviewAssignment.setRevieweeId(currentUser.getId());
        reviewAssignmentsSet.add(reviewAssignment);

        feedbackRequestServices.sendSelfReviewCompletionEmailToReviewers(feedbackRequest, reviewAssignmentsSet);

        // This should equal the number of review assignments.
        // The order in which emails are sent is random.  We will not be
        // checking the recipient.
        assertEquals(2, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL", "null", "null", "firstName lastName has finished their self-review for Self-Review Test.", "firstName lastName has completed their self-review", null, emailSender.events.getFirst());
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmailToSupervisor() {
        UUID creatorId = UUID.randomUUID();
        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(creatorId);

        MemberProfile pdlProfile = new MemberProfile();
        pdlProfile.setId(UUID.randomUUID());
        pdlProfile.setFirstName("PDL");
        pdlProfile.setLastName("Profile");
        pdlProfile.setWorkEmail("pdl@example.com");

        MemberProfile supervisorProfile = new MemberProfile();
        supervisorProfile.setId(UUID.randomUUID());
        supervisorProfile.setFirstName("Supervisor");
        supervisorProfile.setLastName("Profile");
        supervisorProfile.setWorkEmail("supervisor@example.com");

        currentUser.setPdlId(pdlProfile.getId());
        currentUser.setSupervisorid(supervisorProfile.getId());

        ReviewPeriod reviewPeriod = new ReviewPeriod();
        reviewPeriod.setName("Self-Review Test");

        String firstName = "firstName";
        String lastName = "lastName";

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);

        UUID reviewPeriodId = UUID.randomUUID();
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setReviewPeriodId(reviewPeriodId);

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(memberProfileServices.getById(pdlProfile.getId())).thenReturn(pdlProfile);
        when(memberProfileServices.getById(supervisorProfile.getId())).thenReturn(supervisorProfile);
        when(reviewPeriodRepository.findById(reviewPeriodId)).thenReturn(Optional.of(reviewPeriod));

        feedbackRequestServices.sendSelfReviewCompletionEmailToSupervisor(feedbackRequest);

        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL", "null", "null", "firstName lastName has finished their self-review for Self-Review Test.", "firstName lastName has completed their self-review", supervisorProfile.getWorkEmail(), emailSender.events.getFirst());
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmailToSupervisor_MissingSupervisor() {
        UUID creatorId = UUID.randomUUID();
        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(creatorId);

        MemberProfile pdlProfile = new MemberProfile();
        pdlProfile.setId(UUID.randomUUID());
        pdlProfile.setFirstName("PDL");
        pdlProfile.setLastName("Profile");
        pdlProfile.setWorkEmail("pdl@example.com");

        currentUser.setPdlId(pdlProfile.getId());

        String firstName = "firstName";
        String lastName = "lastName";

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(memberProfileServices.getById(pdlProfile.getId())).thenReturn(pdlProfile);

        feedbackRequestServices.sendSelfReviewCompletionEmailToSupervisor(new FeedbackRequest());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmailToSupervisor_EmailSenderException() {
        UUID creatorId = UUID.randomUUID();
        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(creatorId);

        MemberProfile pdlProfile = new MemberProfile();
        pdlProfile.setId(UUID.randomUUID());
        pdlProfile.setFirstName("PDL");
        pdlProfile.setLastName("Profile");
        pdlProfile.setWorkEmail("pdl@example.com");

        MemberProfile supervisorProfile = new MemberProfile();
        supervisorProfile.setId(UUID.randomUUID());
        supervisorProfile.setFirstName("Supervisor");
        supervisorProfile.setLastName("Profile");
        supervisorProfile.setWorkEmail("supervisor@example.com");

        currentUser.setPdlId(pdlProfile.getId());
        currentUser.setSupervisorid(supervisorProfile.getId());

        String firstName = "firstName";
        String lastName = "lastName";

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(memberProfileServices.getById(pdlProfile.getId())).thenReturn(pdlProfile);
        when(memberProfileServices.getById(supervisorProfile.getId())).thenReturn(supervisorProfile);

        emailSender.setException(new RuntimeException("Email sending failed"));

        assertDoesNotThrow(() -> feedbackRequestServices.sendSelfReviewCompletionEmailToSupervisor(new FeedbackRequest()));
        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL", "null", "null", "firstName lastName has finished their self-review.", "firstName lastName has completed their self-review", supervisorProfile.getWorkEmail(), emailSender.events.getFirst());
    }
}

package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeedbackRequestTest extends TestContainersSuite {

    private FeedbackRequestRepository feedbackReqRepository;

    private CurrentUserServices currentUserServices;

    private MemberProfileServices memberProfileServices;

    private EmailSender emailSender;

    private FeedbackRequestServicesImpl feedbackRequestServices;

    @BeforeEach
    @Tag("mocked")
    void setUp() {

        feedbackReqRepository = Mockito.mock(FeedbackRequestRepository.class);
        currentUserServices = Mockito.mock(CurrentUserServices.class);
        memberProfileServices = Mockito.mock(MemberProfileServices.class);
        emailSender = Mockito.mock(EmailSender.class);

        feedbackRequestServices = new FeedbackRequestServicesImpl(feedbackReqRepository, currentUserServices, memberProfileServices, emailSender, "DNC", "http://localhost:8080");
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
        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
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
        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
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
        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmail() {
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

        feedbackRequestServices.sendSelfReviewCompletionEmail();

        verify(emailSender, times(1)).sendEmail(any(), any(),
                eq("firstName lastName has finished their self-review."),
                eq("Self-review has been completed by firstName lastName<br>PDL: PDL Profile<br>Supervisor:" +
                        " Supervisor Profile<br><br>It is now your turn in their review process, please complete your portion in a timely manner."),
                eq("pdl@example.com"), eq("supervisor@example.com"));
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmail_MissingPdl() {
        UUID creatorId = UUID.randomUUID();
        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(creatorId);

        MemberProfile supervisorProfile = new MemberProfile();
        supervisorProfile.setId(UUID.randomUUID());
        supervisorProfile.setFirstName("Supervisor");
        supervisorProfile.setLastName("Profile");
        supervisorProfile.setWorkEmail("supervisor@example.com");

        currentUser.setSupervisorid(supervisorProfile.getId());

        String firstName = "firstName";
        String lastName = "lastName";

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(memberProfileServices.getById(supervisorProfile.getId())).thenReturn(supervisorProfile);

        feedbackRequestServices.sendSelfReviewCompletionEmail();

        verify(emailSender, times(1)).sendEmail(any(), any(),
                eq("firstName lastName has finished their self-review."),
                eq("Self-review has been completed by firstName lastName<br>Supervisor: Supervisor Profile<br><br>It is now your turn in their review process, please complete your portion in a timely manner."),
                eq("supervisor@example.com"));
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmail_MissingSupervisor() {
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

        feedbackRequestServices.sendSelfReviewCompletionEmail();

        verify(emailSender, times(1)).sendEmail(any(), any(),
                eq("firstName lastName has finished their self-review."),
                eq("Self-review has been completed by firstName lastName<br>PDL: PDL Profile<br><br>It is now your turn in their review process, please complete your portion in a timely manner."),
                eq("pdl@example.com"));
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmail_MissingPdlAndSupervisor() {
        UUID creatorId = UUID.randomUUID();
        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(creatorId);

        String firstName = "firstName";
        String lastName = "lastName";

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);

        feedbackRequestServices.sendSelfReviewCompletionEmail();

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testSendSelfReviewCompletionEmail_EmailSenderException() {
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
        doThrow(new RuntimeException("Email sending failed")).when(emailSender).sendEmail(any(), any(), any(), any(), any(), any());

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }
}

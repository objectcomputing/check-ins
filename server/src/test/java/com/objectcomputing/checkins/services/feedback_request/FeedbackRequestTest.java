package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.ReviewPeriodFixture;
import com.objectcomputing.checkins.services.fixture.ReviewAssignmentFixture;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
class FeedbackRequestTest extends TestContainersSuite
                          implements FeedbackRequestFixture, MemberProfileFixture, ReviewPeriodFixture, ReviewAssignmentFixture, RoleFixture {
    @Inject
    private CurrentUserServicesReplacement currentUserServices;

    @Inject
    private FeedbackRequestServicesImpl feedbackRequestServices;

    @Inject
    @Named(MailJetFactory.MJML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    CheckInsConfiguration checkInsConfiguration;

    @BeforeEach
    void setUp() {
        createAndAssignRoles();
        emailSender.reset();
    }

    @Test
    void testUpdateFeedbackRequest() {
        MemberProfile creator = createADefaultMemberProfile();
        MemberProfile recipient = createASecondDefaultMemberProfile();
        MemberProfile requestee = createAThirdDefaultMemberProfile();
        FeedbackRequest feedbackRequest =
            saveFeedbackRequest(creator, requestee, recipient);

        UUID feedbackRequestId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID requesteeId = UUID.randomUUID();

        // We need the current user to be logged in and admin.
        currentUserServices.currentUser = creator;
        assignAdminRole(creator);

        FeedbackRequestUpdateDTO updateDTO = new FeedbackRequestUpdateDTO();
        updateDTO.setId(feedbackRequest.getId());
        updateDTO.setDueDate(LocalDate.now().plusDays(7));
        updateDTO.setStatus("submitted");
        updateDTO.setRecipientId(feedbackRequest.getRecipientId());

        FeedbackRequest updatedFeedbackRequest = feedbackRequestServices.update(updateDTO);

        assertNotNull(updatedFeedbackRequest);
        assertEquals("submitted", updatedFeedbackRequest.getStatus());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testUpdateFeedbackRequest_NotFound() {
        MemberProfile creator = createADefaultMemberProfile();
        UUID feedbackRequestId = UUID.randomUUID();
        UUID creatorId = creator.getId();
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

        // We need the creator to be logged in.
        currentUserServices.currentUser = creator;
        assignMemberRole(creator);

        assertThrows(NotFoundException.class, () -> feedbackRequestServices.update(updateDTO));
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testUpdateFeedbackRequest_Unauthorized() {
        MemberProfile creator = createADefaultMemberProfile();
        MemberProfile recipient = createASecondDefaultMemberProfile();
        MemberProfile requestee = createAThirdDefaultMemberProfile();
        FeedbackRequest feedbackRequest =
            saveFeedbackRequest(creator, requestee, recipient);

        UUID feedbackRequestId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID requesteeId = UUID.randomUUID();

        // We need the current user to be logged in and *not* admin.
        currentUserServices.currentUser = creator;

        FeedbackRequestUpdateDTO updateDTO = new FeedbackRequestUpdateDTO();
        updateDTO.setId(feedbackRequest.getId());
        updateDTO.setDueDate(LocalDate.now().plusDays(7));
        updateDTO.setStatus("submitted");
        updateDTO.setRecipientId(feedbackRequest.getRecipientId());

        assertThrows(PermissionException.class, () -> feedbackRequestServices.update(updateDTO));
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSendSelfReviewCompletionEmailToReviewers() {
        MemberProfile pdlProfile = createASecondDefaultMemberProfile();
        MemberProfile supervisorProfile = createAThirdDefaultMemberProfile();
        MemberProfile currentUser =
            createAProfileWithSupervisorAndPDL(supervisorProfile, pdlProfile);

        MemberProfile reviewer01 = createADefaultMemberProfile();
        MemberProfile reviewer02 = createAnUnrelatedUser();

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        FeedbackRequest feedbackRequest =
            saveFeedbackRequest(supervisorProfile, currentUser,
                                pdlProfile, reviewPeriod);

        currentUserServices.currentUser = currentUser;

        Set<ReviewAssignment> reviewAssignmentsSet = new HashSet<ReviewAssignment>();
        reviewAssignmentsSet.add(
            createAReviewAssignmentBetweenMembers(currentUser, reviewer01, reviewPeriod, true));
        reviewAssignmentsSet.add(
            createAReviewAssignmentBetweenMembers(currentUser, reviewer02, reviewPeriod, true));

        feedbackRequestServices.sendSelfReviewCompletionEmailToReviewers(feedbackRequest, reviewAssignmentsSet);

        // This should equal the number of review assignments.
        // The order in which emails are sent is random.  We will not be
        // checking the recipient.
        assertEquals(2, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL", "null", "null",
                                  String.format("%s %s has finished their self-review for %s.", currentUser.getFirstName(), currentUser.getLastName(), reviewPeriod.getName()),
                                  String.format("%s %s has completed their self-review", currentUser.getFirstName(), currentUser.getLastName()),
                                  null, emailSender.events.getFirst());
    }

    @Test
    void testSendSelfReviewCompletionEmailToSupervisor() {
        MemberProfile pdlProfile = createASecondDefaultMemberProfile();
        MemberProfile supervisorProfile = createAThirdDefaultMemberProfile();
        MemberProfile currentUser =
            createAProfileWithSupervisorAndPDL(supervisorProfile, pdlProfile);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        FeedbackRequest feedbackRequest =
            saveFeedbackRequest(supervisorProfile, currentUser,
                                pdlProfile, reviewPeriod);

        currentUserServices.currentUser = currentUser;

        feedbackRequestServices.sendSelfReviewCompletionEmailToSupervisor(feedbackRequest);

        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL", "null", "null",
                                  String.format("%s %s has finished their self-review for %s.", currentUser.getFirstName(), currentUser.getLastName(), reviewPeriod.getName()),
                                  String.format("%s %s has completed their self-review", currentUser.getFirstName(), currentUser.getLastName()),
                                  supervisorProfile.getWorkEmail(),
                                  emailSender.events.getFirst());
    }

    @Test
    void testSendSelfReviewCompletionEmailToSupervisor_MissingSupervisor() {
        MemberProfile pdlProfile = createASecondDefaultMemberProfile();
        MemberProfile otherProfile = createAThirdDefaultMemberProfile();
        MemberProfile currentUser = createADefaultMemberProfile();

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        FeedbackRequest feedbackRequest =
            saveFeedbackRequest(otherProfile, currentUser,
                                pdlProfile, reviewPeriod);

        currentUserServices.currentUser = currentUser;

        feedbackRequestServices.sendSelfReviewCompletionEmailToSupervisor(feedbackRequest);
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSendSelfReviewCompletionEmailToSupervisor_EmailSenderException() {
        MemberProfile pdlProfile = createASecondDefaultMemberProfile();
        MemberProfile supervisorProfile = createAThirdDefaultMemberProfile();
        MemberProfile currentUser =
            createAProfileWithSupervisorAndPDL(supervisorProfile, pdlProfile);

        ReviewPeriod reviewPeriod = createADefaultReviewPeriod();

        FeedbackRequest feedbackRequest =
            saveFeedbackRequest(supervisorProfile, currentUser,
                                pdlProfile, reviewPeriod);

        currentUserServices.currentUser = currentUser;

        emailSender.setException(new RuntimeException("Email sending failed"));

        assertDoesNotThrow(() -> feedbackRequestServices.sendSelfReviewCompletionEmailToSupervisor(feedbackRequest));
        assertEquals(1, emailSender.events.size());
        EmailHelper.validateEmail("SEND_EMAIL", "null", "null",
                                  String.format("%s %s has finished their self-review for %s.", currentUser.getFirstName(), currentUser.getLastName(), reviewPeriod.getName()),
                                  String.format("%s %s has completed their self-review", currentUser.getFirstName(), currentUser.getLastName()),
                                  supervisorProfile.getWorkEmail(),
                                  emailSender.events.getFirst());
    }
}

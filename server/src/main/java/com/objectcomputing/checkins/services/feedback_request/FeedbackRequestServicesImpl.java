package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.email.Email;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.reviews.ReviewAssignment;
import com.objectcomputing.checkins.services.reviews.ReviewAssignmentRepository;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodRepository;
import com.objectcomputing.checkins.util.Util;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class FeedbackRequestServicesImpl implements FeedbackRequestServices {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestServicesImpl.class);

    private final FeedbackRequestRepository feedbackReqRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final ReviewPeriodRepository reviewPeriodRepository;
    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final EmailSender emailSender;
    private final String notificationSubject;
    private final String webURL;

    private enum CompletionEmailType { REVIEWERS, SUPERVISOR }
    private record ReviewPeriodInfo(String subject, LocalDate closeDate) {}

    public FeedbackRequestServicesImpl(FeedbackRequestRepository feedbackReqRepository,
                                       CurrentUserServices currentUserServices,
                                       MemberProfileServices memberProfileServices,
                                       ReviewPeriodRepository reviewPeriodRepository, ReviewAssignmentRepository reviewAssignmentRepository,
                                       @Named(MailJetFactory.MJML_FORMAT) EmailSender emailSender,
                                       CheckInsConfiguration checkInsConfiguration
    ) {
        this.feedbackReqRepository = feedbackReqRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.reviewPeriodRepository = reviewPeriodRepository;
        this.reviewAssignmentRepository = reviewAssignmentRepository;
        this.emailSender = emailSender;
        this.notificationSubject = checkInsConfiguration.getApplication().getFeedback().getRequestSubject();
        this.webURL = checkInsConfiguration.getWebAddress();
    }

    private void validateMembers(FeedbackRequest feedbackRequest) {
        try {
            memberProfileServices.getById(feedbackRequest.getCreatorId());
        } catch (NotFoundException e) {
            throw new BadArgException("Cannot save feedback request with invalid creator ID");
        }

        try {
            memberProfileServices.getById(feedbackRequest.getRecipientId());
        } catch (NotFoundException e) {
            throw new BadArgException("Cannot save feedback request with invalid recipient ID");
        }

        try {
            memberProfileServices.getById(feedbackRequest.getRequesteeId());
        } catch (NotFoundException e) {
            throw new BadArgException("Cannot save feedback request with invalid requestee ID");
        }
    }

    @Override
    public FeedbackRequest save(FeedbackRequest feedbackRequest) {
        validateMembers(feedbackRequest);
        if (!createIsPermitted(feedbackRequest.getRequesteeId())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (feedbackRequest.getId() != null) {
            throw new BadArgException("Attempted to save feedback request with non-auto-populated ID");
        }


        if (feedbackRequest.getDueDate() != null && feedbackRequest.getSendDate().isAfter(feedbackRequest.getDueDate())) {
            throw new BadArgException("Send date of feedback request must be before the due date.");
        }
        String status = feedbackRequest.getSendDate().isAfter(LocalDate.now()) ? "pending" : "sent";
        feedbackRequest.setStatus(status);
        FeedbackRequest storedRequest = feedbackReqRepository.save(feedbackRequest);
        if (feedbackRequest.getSendDate().equals(LocalDate.now())) {
            sendNewRequestEmail(storedRequest);
        }
        return storedRequest;
    }

    public void sendNewRequestEmail(FeedbackRequest storedRequest) {
        MemberProfile creator = memberProfileServices.getById(storedRequest.getCreatorId());
        MemberProfile reviewer = memberProfileServices.getById(storedRequest.getRecipientId());
        MemberProfile requestee = memberProfileServices.getById(storedRequest.getRequesteeId());
        String senderName = MemberProfileUtils.getFullName(creator);

        String newContent = String.format("""
<mjml>
  <mj-head>
    <mj-title>Feedback Request</mj-title>
    <mj-preview>Feedback Request</mj-preview>
    <mj-attributes>
      <mj-class name="preheader" color="#000000" font-size="11px" font-family="Ubuntu, Helvetica, Arial, sans-serif" padding="0px"></mj-class>
    </mj-attributes>
  </mj-head>
  <mj-body background-color="#e0f2ff">
    <mj-section background-color="#2559a7">
      <mj-column>
        <mj-image src="https://objectcomputing.com/files/6416/4277/8012/ObjectComputingLogo_version2_white.png" alt="logo" width="150px"></mj-image>
      </mj-column>
    </mj-section>
    <mj-hero mode="fluid-height" background-url="https://lh3.googleusercontent.com/pw/AL9nZEXvzBSrNroLHtqfW8W5_oM296XY7FPJqz15RNP3RBcf_XEkyZ0gn5JVkDCSTWA-loYTeVL5c-ycoAEOh_3dFBpPju1UmfGt7tLPCMFQdf5IVeHipmhyOV4fZnCWSl0n-b3tsHB4THfub4Mtknvz8R4t=w900-h600-no" background-color="#FFF" padding="100px 0px">
      <mj-text padding="20px" font-family="Helvetica" align="center" font-size="45px" line-height="45px" font-weight="900"> Give Your Feedback! </mj-text>
   
    </mj-hero>
    <mj-section background-color="#ffffff">
      <mj-column>
        <mj-text>
          <h2>You have received a feedback request.</h2>
        </mj-text>
          <mj-text font-size="16px">Hello, %s!</mj-text>
        <mj-text font-size="16px"><strong>%s</strong> is requesting feedback on <strong>%s</strong> from you.</mj-text>
        <mj-text font-size="16px">%s</mj-text>
        <mj-text font-size="16px">Please go to <a href="%s">your unique link</a> to complete this request.</mj-text>
      </mj-column>
    </mj-section>
    <mj-section background-color="#feb672" padding="10px">
      <mj-column vertical-align="top" width="100%%">
        <mj-text align="center" color="#FFF" font-size="16px">Thank you for everything you do!</mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>
""", reviewer.getFirstName(), senderName,
     MemberProfileUtils.getFullName(requestee),
     storedRequest.getDueDate() == null ?
         "This request does not have a due date." :
         String.format("This request is due on %s %d, %d.",
                       storedRequest.getDueDate().getMonth(),
                       storedRequest.getDueDate().getDayOfMonth(),
                       storedRequest.getDueDate().getYear()),
     String.format("%s/feedback/submit?request=%s",
                   webURL, storedRequest.getId().toString()));

        if (!storedRequest.getRecipientId().equals(storedRequest.getRequesteeId())) {
            emailSender.sendEmail(senderName, creator.getWorkEmail(),
                                  notificationSubject, newContent,
                                  reviewer.getWorkEmail());
        }
    }

    @Override
    public FeedbackRequest update(FeedbackRequestUpdateDTO feedbackRequestUpdateDTO) {
        /*
         * only creator can update due date--only field they can update without making new request
         * status has to be updated with any permissions--fired on submission from any recipient
         * submit date can be updated only when the recipient is logged in--fired on submission from any recipient
         */

        final FeedbackRequest feedbackRequest = this.getFromDTO(feedbackRequestUpdateDTO);
        FeedbackRequest originalFeedback = null;

        if (feedbackRequest.getId() != null) {
            originalFeedback = getById(feedbackRequest.getId());
        }

        if (originalFeedback == null) {
            throw new BadArgException("Cannot update feedback request that does not exist");
        }

        validateMembers(originalFeedback);

        Set<ReviewAssignment> reviewAssignmentsSet = Set.of();
        if (feedbackRequest != null && feedbackRequest.getReviewPeriodId() != null && feedbackRequest.getRequesteeId() != null) {
            reviewAssignmentsSet = reviewAssignmentRepository.findByReviewPeriodIdAndRevieweeId(feedbackRequest.getReviewPeriodId(), feedbackRequest.getRequesteeId());
        }        

        boolean reassignAttempted = !Objects.equals(originalFeedback.getRecipientId(), feedbackRequest.getRecipientId());
        boolean dueDateUpdateAttempted = !Objects.equals(originalFeedback.getDueDate(), feedbackRequest.getDueDate());
        boolean submitDateUpdateAttempted = !Objects.equals(originalFeedback.getSubmitDate(), feedbackRequest.getSubmitDate());

        // If a status update is made to anything other than submitted by the requestee, throw an error.
        if (!"submitted".equals(feedbackRequest.getStatus())
                && !Objects.equals(originalFeedback.getStatus(), feedbackRequest.getStatus())
                && currentUserServices.getCurrentUser().getId().equals(originalFeedback.getRequesteeId())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (reassignAttempted) {
            if (!reassignIsPermitted(originalFeedback)) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }
            feedbackRequest.setStatus("sent");
        }

        if (feedbackRequest.getStatus().equals("canceled") && originalFeedback.getStatus().equals("submitted")) {
            throw new BadArgException("Attempted to cancel a feedback request that was already submitted");
        }

        if (dueDateUpdateAttempted && !updateDueDateIsPermitted(originalFeedback)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (dueDateUpdateAttempted && !updateDueDateIsPermitted(originalFeedback)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (submitDateUpdateAttempted && !updateSubmitDateIsPermitted(originalFeedback)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (feedbackRequest.getDueDate() != null && originalFeedback.getSendDate().isAfter(feedbackRequest.getDueDate())) {
            throw new BadArgException("Send date of feedback request must be before the due date.");
        }

        FeedbackRequest storedRequest = feedbackReqRepository.update(feedbackRequest);
        MemberProfile reviewer = memberProfileServices.getById(storedRequest.getRecipientId());
        MemberProfile requestee = memberProfileServices.getById(storedRequest.getRequesteeId());
        // Send email if the feedback request has been reopened for edits
        if (originalFeedback.getStatus().equals("submitted") && feedbackRequest.getStatus().equals("sent")) {
            MemberProfile creator = memberProfileServices.getById(storedRequest.getCreatorId());
            String senderName = MemberProfileUtils.getFullName(creator);
            String newContent = String.format("""
<mjml>
  <mj-head>
    <mj-title>Feedback Request Reopened</mj-title>
    <mj-preview>Feedback Request Reopened</mj-preview>
    <mj-attributes>
      <mj-class name="preheader" color="#000000" font-size="11px" font-family="Ubuntu, Helvetica, Arial, sans-serif" padding="0px"></mj-class>
    </mj-attributes>
  </mj-head>
  <mj-body background-color="#e0f2ff">
    <mj-section background-color="#2559a7">
      <mj-column>
        <mj-image src="https://objectcomputing.com/files/6416/4277/8012/ObjectComputingLogo_version2_white.png" alt="logo" width="150px"></mj-image>
      </mj-column>
    </mj-section>
    <mj-hero mode="fluid-height" background-url="https://lh3.googleusercontent.com/pw/AL9nZEXvzBSrNroLHtqfW8W5_oM296XY7FPJqz15RNP3RBcf_XEkyZ0gn5JVkDCSTWA-loYTeVL5c-ycoAEOh_3dFBpPju1UmfGt7tLPCMFQdf5IVeHipmhyOV4fZnCWSl0n-b3tsHB4THfub4Mtknvz8R4t=w900-h600-no" background-color="#FFF" padding="100px 0px">
      <mj-text padding="20px" font-family="Helvetica" align="center" font-size="45px" line-height="45px" font-weight="900"> Edit Your Feedback! </mj-text>
   
    </mj-hero>
    <mj-section background-color="#ffffff">
      <mj-column>
        <mj-text>
          <h2>You have received edit access to a feedback request.</h2>
        </mj-text>
          <mj-text font-size="16px">Hello, %s!</mj-text>
        <mj-text font-size="16px"><strong>%s has reopened the feedback request on %s from you.</strong></mj-text>
        <mj-text font-size="16px">You may make changes to your answers, but you will need to submit the form again when finished.</mj-text>
        <mj-text font-size="16px">Please go to <a href="%s">your unique link</a> to complete this request.</mj-text>
      </mj-column>
    </mj-section>
    <mj-section background-color="#feb672" padding="10px">
      <mj-column vertical-align="top" width="100%%">
        <mj-text align="center" color="#FFF" font-size="16px">Thank you for everything you do!</mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>
""", reviewer.getFirstName(), senderName,
     MemberProfileUtils.getFullName(requestee),
     String.format("%s/feedback/submit?request=%s",
                   webURL, storedRequest.getId().toString()));

            emailSender.sendEmail(senderName, creator.getWorkEmail(), notificationSubject, newContent, reviewer.getWorkEmail());
        }

        // Send email if the feedback request has been reassigned
        if (reassignAttempted) {
            sendNewRequestEmail(storedRequest);
        }

        // Send self-review completion email to supervisor and pdl if appropriate
        if (currentUserServices.getCurrentUser().getId().equals(requestee.getId())) {
            sendSelfReviewCompletionEmailToSupervisor(feedbackRequest);
        }

        // Send email to reviewers.  But, only when the requestee is the
        // recipient (i.e., a self-review).
        if (reviewAssignmentsSet != null && reviewAssignmentsSet.size() > 0 &&
            feedbackRequest.getRequesteeId().equals(feedbackRequest.getRecipientId())) {
            sendSelfReviewCompletionEmailToReviewers(feedbackRequest, reviewAssignmentsSet);    
        }        

        return storedRequest;
    }

    @Override
    public void delete(UUID id) {
        final Optional<FeedbackRequest> feedbackReq = feedbackReqRepository.findById(id);
        if (feedbackReq.isEmpty()) {
            throw new NotFoundException("No feedback request with id " + id);
        }

        if (!createIsPermitted(feedbackReq.get().getRequesteeId())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        feedbackReqRepository.deleteById(id);
    }

    @Override
    public FeedbackRequest getById(UUID id) {
        final Optional<FeedbackRequest> feedbackReq = feedbackReqRepository.findById(id);
        if (feedbackReq.isEmpty()) {
            throw new NotFoundException("No feedback req with id " + id);
        }
        final LocalDate sendDate = feedbackReq.get().getSendDate();
        final UUID requesteeId = feedbackReq.get().getRequesteeId();
        final UUID recipientId = feedbackReq.get().getRecipientId();
        if (!getIsPermitted(requesteeId, recipientId, sendDate)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return feedbackReq.get();
    }

    @Override
    public List<FeedbackRequest> findByValues(UUID creatorId, UUID requesteeId, UUID recipientId, LocalDate oldestDate, UUID reviewPeriodId, UUID templateId, List<UUID> requesteeIds) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (currentUserId == null) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        List<FeedbackRequest> feedbackReqList = new ArrayList<>();
        if (requesteeIds != null && !requesteeIds.isEmpty()) {
            LOG.debug("Finding feedback requests for {} requesteeIds.", requesteeIds.size());
            feedbackReqList.addAll(feedbackReqRepository.findByValues(Util.nullSafeUUIDToString(creatorId), Util.nullSafeUUIDToString(recipientId), oldestDate, Util.nullSafeUUIDToString(reviewPeriodId), Util.nullSafeUUIDToString(templateId), Util.nullSafeUUIDListToStringList(requesteeIds)));
        } else {
            LOG.debug("Finding feedback requests one or fewer requesteeIds: {}", requesteeId);
            feedbackReqList.addAll(feedbackReqRepository.findByValues(Util.nullSafeUUIDToString(creatorId), Util.nullSafeUUIDToString(requesteeId), Util.nullSafeUUIDToString(recipientId), oldestDate, Util.nullSafeUUIDToString(reviewPeriodId), Util.nullSafeUUIDToString(templateId)));
        }

        feedbackReqList = feedbackReqList.stream().filter((FeedbackRequest request) -> {
            boolean visible = false;
            if (currentUserServices.isAdmin()) {
                visible = true;
            } else if (request != null) {
                if (currentUserId.equals(request.getCreatorId())) visible = true;
                if (isSupervisor(request.getRequesteeId(), currentUserId)) visible = true;
                if (currentUserId.equals(request.getRecipientId())) visible = true;
            }
            return visible;
        }).toList();

        return feedbackReqList;
    }

    private boolean isSupervisor(UUID requesteeId, UUID currentUserId) {
        return requesteeId != null
                && memberProfileServices.getSupervisorsForId(requesteeId).stream().anyMatch(profile -> currentUserId.equals(profile.getId()));
    }

    private boolean createIsPermitted(UUID requesteeId) {
        final boolean isAdmin = currentUserServices.isAdmin();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        MemberProfile requestee = memberProfileServices.getById(requesteeId);
        boolean isRequesteesSupervisor = isSupervisor(requesteeId, currentUserId);
        final UUID requesteePDL = requestee.getPdlId();

        //a PDL may create a request for a user who is assigned to them
        return isAdmin || currentUserId.equals(requesteePDL) || isRequesteesSupervisor || currentUserId.equals(requesteeId);
    }

    private boolean getIsPermitted(UUID requesteeId, UUID recipientId, LocalDate sendDate) {
        LocalDate today = LocalDate.now();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();

        // The recipient can only access the feedback request after it has been sent
        if (sendDate.isAfter(today) && currentUserId.equals(recipientId)) {
            throw new PermissionException("You are not permitted to access this request before the send date.");
        }

        return createIsPermitted(requesteeId) || currentUserId.equals(recipientId);
    }

    private boolean updateDueDateIsPermitted(FeedbackRequest feedbackRequest) {
        return isCurrentUserAdminOrOwner(feedbackRequest);
    }

    private boolean reassignIsPermitted(FeedbackRequest feedbackRequest) {
        return isCurrentUserAdminOrOwner(feedbackRequest) && !feedbackRequest.getStatus().equals("submitted");
    }

    private boolean isCurrentUserAdminOrOwner(FeedbackRequest feedbackRequest) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return isAdmin || currentUserId.equals(feedbackRequest.getCreatorId());
    }

    private boolean updateSubmitDateIsPermitted(FeedbackRequest feedbackRequest) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (isAdmin || (currentUserId.equals(feedbackRequest.getCreatorId()) && feedbackRequest.getSubmitDate() != null)) {
            return true;
        }

        return currentUserId.equals(feedbackRequest.getRecipientId());
    }

    private FeedbackRequest getFromDTO(FeedbackRequestUpdateDTO dto) {
        FeedbackRequest feedbackRequest = this.getById(dto.getId());
        feedbackRequest.setDueDate(dto.getDueDate());
        feedbackRequest.setStatus(dto.getStatus());
        feedbackRequest.setSubmitDate(dto.getSubmitDate());
        feedbackRequest.setRecipientId(dto.getRecipientId());

        return feedbackRequest;
    }

    private void sendSelfReviewCompletionEmailToReviewers(FeedbackRequest feedbackRequest, Set<ReviewAssignment> reviewAssignmentSet) {
        // Send an email to each reviewer.
        reviewAssignmentSet.forEach(reviewAssignment -> {
            MemberProfile memberProfileReviewer = memberProfileServices.getById(reviewAssignment.getReviewerId());
            if (memberProfileReviewer != null &&
                memberProfileReviewer.getWorkEmail() != null) {
                sendSelfReviewCompletionEmail(feedbackRequest,
                                              memberProfileReviewer,
                                              CompletionEmailType.REVIEWERS);
            }
        });
    }

    private void sendSelfReviewCompletionEmailToSupervisor(FeedbackRequest feedbackRequest) {
        MemberProfile currentUserProfile = currentUserServices.getCurrentUser();
        try {
            if (currentUserProfile.getSupervisorid() != null) {
                MemberProfile supervisorProfile =
                    memberProfileServices.getById(
                        currentUserProfile.getSupervisorid());
                sendSelfReviewCompletionEmail(feedbackRequest,
                                              supervisorProfile,
                                              CompletionEmailType.SUPERVISOR);
            }
        } catch (NotFoundException e) {
            LOG.error("Supervisor could not be found for completion email");
        }
    }

    private void sendSelfReviewCompletionEmail(FeedbackRequest feedbackRequest,
                                               MemberProfile reviewer,
                                               CompletionEmailType emailType) {
        // Build the email contents.
        Email email;
        MemberProfile currentUserProfile = currentUserServices.getCurrentUser();
        switch(emailType) {
            case CompletionEmailType.REVIEWERS:
                email = buildReviewerEmail(feedbackRequest, reviewer,
                                           currentUserProfile);
                break;
            default:
            case CompletionEmailType.SUPERVISOR:
                email = buildSupervisorEmail(feedbackRequest, reviewer,
                                             currentUserProfile);
                break;
        }

        // Send the email.
        try {
            emailSender.sendEmail(null, null, email.getSubject(),
                                  email.getContents(),
                                  reviewer.getWorkEmail());
        } catch (Exception e) {
           LOG.error("Unable to send the self-review completion email.", e);
        }
    }

    private ReviewPeriodInfo getSelfReviewInfo(
                                 FeedbackRequest feedbackRequest, String name) {
        String reviewPeriodString = "";
        LocalDate closeDate = null;
        if (feedbackRequest.getReviewPeriodId() != null) {
            Optional<ReviewPeriod> reviewPeriodOpt = reviewPeriodRepository.findById(feedbackRequest.getReviewPeriodId());
            if (reviewPeriodOpt.isPresent()) {
                ReviewPeriod reviewPeriod = reviewPeriodOpt.get();
                closeDate = reviewPeriod.getCloseDate().toLocalDate();
                reviewPeriodString = String.format(" for %s", reviewPeriod.getName());
            }
        }
        return new ReviewPeriodInfo(
                       String.format("%s has finished their self-review%s.",
                                     name, reviewPeriodString), closeDate);
    }

    private Email buildReviewerEmail(FeedbackRequest feedbackRequest,
                                     MemberProfile reviewerProfile,
                                     MemberProfile currentUserProfile) {
        String reviewerName = reviewerProfile.getFirstName();
        String revieweeName = MemberProfileUtils.getFullName(currentUserProfile);
        String selfReviewURL = String.format("%s/feedback/view/responses/?request=%s", webURL, feedbackRequest.getId().toString());
        ReviewPeriodInfo info = getSelfReviewInfo(feedbackRequest, revieweeName);
        LocalDate closeDate = info.closeDate();
        String ending = closeDate == null ? "the review period closes" :
                 closeDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        String body = String.format("""
<mjml>
  <mj-head>
    <mj-title>Self-Review Completion</mj-title>
    <mj-preview>Self-Reviews Completion for Reviewer</mj-preview>
    <mj-attributes>
      <mj-class name="preheader" color="#000000" font-size="11px" font-family="Ubuntu, Helvetica, Arial, sans-serif" padding="0px"></mj-class>
    </mj-attributes>
  </mj-head>
  <mj-body background-color="#e0f2ff">
    <mj-section background-color="#2559a7">
      <mj-column>
        <mj-image src="https://objectcomputing.com/files/6416/4277/8012/ObjectComputingLogo_version2_white.png" alt="logo" width="150px"></mj-image>
      </mj-column>
    </mj-section>
    <mj-hero mode="fluid-height" background-url="https://lh3.googleusercontent.com/pw/AL9nZEXvzBSrNroLHtqfW8W5_oM296XY7FPJqz15RNP3RBcf_XEkyZ0gn5JVkDCSTWA-loYTeVL5c-ycoAEOh_3dFBpPju1UmfGt7tLPCMFQdf5IVeHipmhyOV4fZnCWSl0n-b3tsHB4THfub4Mtknvz8R4t=w900-h600-no" background-color="#FFF" padding="100px 0px">
      <mj-text padding="20px" font-family="Helvetica" align="center" font-size="45px" line-height="45px" font-weight="900"> It's Your Turn! </mj-text>
   
    </mj-hero>
    <mj-section background-color="#ffffff">
      <mj-column>
        <mj-text>
          <h2>It's time to begin your review of %s</h2>
        </mj-text>
          <mj-text font-size="16px">Hello, %s!</mj-text>
        <mj-text font-size="16px">%s has completed their self-review and it can be viewed <a href="%s">here</a>.</mj-text>   <mj-text font-size="16px">It's your turn to share your thoughts and complete your review. Please complete your review before %s. </strong></mj-text>
     
      </mj-column>
    </mj-section>
    <mj-section background-color="#feb672" padding="10px">
      <mj-column vertical-align="top" width="100%%">
        <mj-text align="center" color="#FFF" font-size="16px">Thank you for everything you do!</mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>""", revieweeName, reviewerName, revieweeName, selfReviewURL, ending);
        Email email = new Email();
        email.setSubject(info.subject());
        email.setContents(body);
        return email;
    }

    private Email buildSupervisorEmail(FeedbackRequest feedbackRequest,
                                       MemberProfile supervisorProfile,
                                       MemberProfile currentUserProfile) {
        String supervisorName = supervisorProfile == null ? "Supervisor" :
                                   supervisorProfile.getFirstName();
        String revieweeName = MemberProfileUtils.getFullName(currentUserProfile);
        String selfReviewURL = String.format("%s/feedback/view/responses/?request=%s", webURL, feedbackRequest.getId().toString());
        ReviewPeriodInfo info = getSelfReviewInfo(feedbackRequest, revieweeName);

        String body = String.format("""
<mjml>
  <mj-head>
    <mj-title>Self-Review Completion</mj-title>
    <mj-preview>Self-Reviews Completion for Supervisor</mj-preview>
    <mj-attributes>
      <mj-class name="preheader" color="#000000" font-size="11px" font-family="Ubuntu, Helvetica, Arial, sans-serif" padding="0px"></mj-class>
    </mj-attributes>
  </mj-head>
  <mj-body background-color="#e0f2ff">
    <mj-section background-color="#2559a7">
      <mj-column>
        <mj-image src="https://objectcomputing.com/files/6416/4277/8012/ObjectComputingLogo_version2_white.png" alt="logo" width="150px"></mj-image>
      </mj-column>
    </mj-section>
    <mj-hero mode="fluid-height" background-url="https://lh3.googleusercontent.com/pw/AL9nZEXvzBSrNroLHtqfW8W5_oM296XY7FPJqz15RNP3RBcf_XEkyZ0gn5JVkDCSTWA-loYTeVL5c-ycoAEOh_3dFBpPju1UmfGt7tLPCMFQdf5IVeHipmhyOV4fZnCWSl0n-b3tsHB4THfub4Mtknvz8R4t=w900-h600-no" background-color="#FFF" padding="100px 0px">
      <mj-text padding="20px" font-family="Helvetica" align="center" font-size="45px" line-height="45px" font-weight="900"> Self-Review Completion </mj-text>
   
    </mj-hero>
    <mj-section background-color="#ffffff">
      <mj-column>
        <mj-text>
          <h2>Your team member has completed their self-review!</h2>
        </mj-text>
          <mj-text font-size="16px">Hello, %s!</mj-text>
        <mj-text font-size="16px">%s has completed their self-review. You can view it <a href="%s">here</a>.</mj-text>
     
      </mj-column>
    </mj-section>
    <mj-section background-color="#feb672" padding="10px">
      <mj-column vertical-align="top" width="100%%">
        <mj-text align="center" color="#FFF" font-size="16px">Thank you for everything you do!</mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>""", supervisorName, revieweeName, selfReviewURL);

        Email email = new Email();
        email.setSubject(info.subject());
        email.setContents(body);
        return email;
    }
}

package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
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
import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.Readable;
import io.micronaut.core.io.IOUtils;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
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
    @Value("classpath:mjml/feedback_request.mjml")
    private Readable feedbackRequestTemplate;
    @Value("classpath:mjml/update_request.mjml")
    private Readable updateRequestTemplate;
    @Value("classpath:mjml/reviewer_email.mjml")
    private Readable reviewerTemplate;
    @Value("classpath:mjml/supervisor_email.mjml")
    private Readable supervisorTemplate;

    public FeedbackRequestServicesImpl(FeedbackRequestRepository feedbackReqRepository,
                                       CurrentUserServices currentUserServices,
                                       MemberProfileServices memberProfileServices,
                                       ReviewPeriodRepository reviewPeriodRepository,
                                       ReviewAssignmentRepository reviewAssignmentRepository,
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
    @RequiredPermission(Permission.CAN_CREATE_FEEDBACK_REQUEST)
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

        String newContent = String.format(
                                templateToString(feedbackRequestTemplate),
                                reviewer.getFirstName(), senderName,
                                storedRequest.getRecipientId().equals(storedRequest.getRequesteeId()) ?
                                  "" :
                                  String.format("on <strong>%s</strong> ",
                                                MemberProfileUtils.getFullName(requestee)),
                                storedRequest.getDueDate() == null ?
                                    "This request does not have a due date." :
                                    String.format("This request is due on %s %d, %d.",
                                                  storedRequest.getDueDate().getMonth(),
                                                  storedRequest.getDueDate().getDayOfMonth(),
                                                  storedRequest.getDueDate().getYear()),
                                String.format("%s/feedback/submit?request=%s",
                                              webURL, storedRequest.getId().toString()));

        emailSender.sendEmail(senderName, creator.getWorkEmail(),
                              notificationSubject, newContent,
                              reviewer.getWorkEmail());
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
            String newContent = String.format(
                                  templateToString(updateRequestTemplate),
                                  reviewer.getFirstName(), senderName,
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
    @RequiredPermission(Permission.CAN_DELETE_FEEDBACK_REQUEST)
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
    // This method cannot have
    // @RequiredPermission(Permission.CAN_ADMINISTER_FEEDBACK_REQUEST) because
    // regular members need to be able to get feedback requests.  This
    // permission is manually checked elsewhere.
    public FeedbackRequest getById(UUID id) {
        final Optional<FeedbackRequest> feedbackReq = feedbackReqRepository.findById(id);
        if (feedbackReq.isEmpty()) {
            throw new NotFoundException("No feedback req with id " + id);
        }

        if (!getIsPermitted(feedbackReq.get())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return feedbackReq.get();
    }

    @Override
    // This method cannot have
    // @RequiredPermission(Permission.CAN_ADMINISTER_FEEDBACK_REQUEST) because
    // regular members need to be able to get feedback requests.  This
    // permission is manually checked elsewhere.
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
            if (currentUserServices.hasPermission(Permission.CAN_ADMINISTER_FEEDBACK_REQUEST)) {
                visible = true;
            } else if (request != null) {
                if (currentUserId.equals(request.getCreatorId()) ||
                    isSupervisor(request.getRequesteeId(), currentUserId) ||
                    currentUserId.equals(request.getRecipientId()) ||
                    selfRevieweeIsCurrentUserReviewee(request, currentUserId)) {
                    visible = true;
                }
            }
            return visible;
        }).toList();

        return feedbackReqList;
    }

    private boolean isSupervisor(UUID requesteeId, UUID currentUserId) {
        return requesteeId != null
                && memberProfileServices.getSupervisorsForId(requesteeId).stream().anyMatch(profile -> currentUserId.equals(profile.getId()));
    }

    public boolean selfRevieweeIsCurrentUserReviewee(FeedbackRequest request,
                                                     UUID currentUserId) {
        // If we are looking at a self-review request, see if there is a review
        // request in the same review period that is assigned to the current
        // user and the requestee is the same as the self-review request.  If
        // so, this user is allowed to see the self-review request.
        if (request.getRecipientId().equals(request.getRequesteeId())) {
            List<FeedbackRequest> other = feedbackReqRepository.findByValues(
                null, request.getRecipientId().toString(),
                currentUserId.toString(), null,
                Util.nullSafeUUIDToString(request.getReviewPeriodId()),
                null);
            return (other.size() == 1);
        }
        return false;
    }

    private boolean createIsPermitted(UUID requesteeId) {
        if (currentUserServices.hasPermission(Permission.CAN_ADMINISTER_FEEDBACK_REQUEST)) {
            return true;
        }

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        MemberProfile requestee = memberProfileServices.getById(requesteeId);
        boolean isRequesteesSupervisor = isSupervisor(requesteeId, currentUserId);
        final UUID requesteePDL = requestee.getPdlId();

        //a PDL may create a request for a user who is assigned to them
        return  currentUserId.equals(requesteePDL) || isRequesteesSupervisor || currentUserId.equals(requesteeId);
    }

    private boolean getIsPermitted(FeedbackRequest feedbackReq) {
        final LocalDate sendDate = feedbackReq.getSendDate();
        final UUID requesteeId = feedbackReq.getRequesteeId();
        final UUID recipientId = feedbackReq.getRecipientId();
        final LocalDate today = LocalDate.now();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();

        // The recipient can only access the feedback request after it has been sent
        if (sendDate.isAfter(today) && currentUserId.equals(recipientId)) {
            throw new PermissionException("You are not permitted to access this request before the send date.");
        }

        return createIsPermitted(requesteeId) ||
               currentUserId.equals(recipientId) ||
               selfRevieweeIsCurrentUserReviewee(feedbackReq, currentUserId);
    }

    private boolean updateDueDateIsPermitted(FeedbackRequest feedbackRequest) {
        return currentUserCanAdministerOrOwner(feedbackRequest);
    }

    private boolean reassignIsPermitted(FeedbackRequest feedbackRequest) {
        return currentUserCanAdministerOrOwner(feedbackRequest) && !feedbackRequest.getStatus().equals("submitted");
    }

    private boolean currentUserCanAdministerOrOwner(FeedbackRequest feedbackRequest) {
        if (currentUserServices.hasPermission(Permission.CAN_ADMINISTER_FEEDBACK_REQUEST)) {
            return true;
        }

        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId.equals(feedbackRequest.getCreatorId());
    }

    private boolean updateSubmitDateIsPermitted(FeedbackRequest feedbackRequest) {
        if (currentUserServices.hasPermission(Permission.CAN_ADMINISTER_FEEDBACK_REQUEST)) {
            return true;
        }

        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (currentUserId.equals(feedbackRequest.getCreatorId()) && feedbackRequest.getSubmitDate() != null) {
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

    public void sendSelfReviewCompletionEmailToReviewers(FeedbackRequest feedbackRequest, Set<ReviewAssignment> reviewAssignmentSet) {
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

    public void sendSelfReviewCompletionEmailToSupervisor(FeedbackRequest feedbackRequest) {
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
                if (reviewPeriod.getCloseDate() != null) {
                    closeDate = reviewPeriod.getCloseDate().toLocalDate();
                }
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
        UUID requestId = feedbackRequest.getId();
        String selfReviewURL = String.format("%s/feedback/view/responses/?request=%s", webURL, requestId == null ? "none" : requestId.toString());
        ReviewPeriodInfo info = getSelfReviewInfo(feedbackRequest, revieweeName);
        LocalDate closeDate = info.closeDate();
        String ending = closeDate == null ? "the review period closes" :
                 closeDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        String body = String.format(templateToString(reviewerTemplate),
                                    revieweeName, reviewerName, revieweeName,
                                    selfReviewURL, ending);
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
        UUID requestId = feedbackRequest.getId();
        String selfReviewURL = String.format("%s/feedback/view/responses/?request=%s", webURL, requestId == null ? "none" : requestId.toString());
        ReviewPeriodInfo info = getSelfReviewInfo(feedbackRequest, revieweeName);

        String body = String.format(templateToString(supervisorTemplate),
                                    supervisorName, revieweeName,
                                    selfReviewURL);

        Email email = new Email();
        email.setSubject(info.subject());
        email.setContents(body);
        return email;
    }

    private String templateToString(Readable template) {
        try {
            return IOUtils.readText(new BufferedReader(template.asReader()));
        } catch (Exception ex) {
            LOG.error(ex.toString());
            return "";
        }
    }
}

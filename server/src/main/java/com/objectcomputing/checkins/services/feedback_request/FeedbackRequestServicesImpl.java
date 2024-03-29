package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetConfig;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class FeedbackRequestServicesImpl implements FeedbackRequestServices {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestServicesImpl.class);

    public static final String FEEDBACK_REQUEST_NOTIFICATION_SUBJECT = "check-ins.application.feedback.notifications.subject";
    public static final String FEEDBACK_REQUEST_NOTIFICATION_CONTENT = "check-ins.application.feedback.notifications.content";
    public static final String WEB_UI_URL = "check-ins.web-address";
    private final FeedbackRequestRepository feedbackReqRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private EmailSender emailSender;
    private final String notificationSubject;
    private final String webURL;

    public FeedbackRequestServicesImpl(FeedbackRequestRepository feedbackReqRepository,
                                       CurrentUserServices currentUserServices,
                                       MemberProfileServices memberProfileServices,
                                       @Named(MailJetConfig.HTML_FORMAT) EmailSender emailSender,
                                       @Property(name = FEEDBACK_REQUEST_NOTIFICATION_SUBJECT) String notificationSubject,
                                       @Property(name = WEB_UI_URL) String webURL
    )
            {
        this.feedbackReqRepository = feedbackReqRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.emailSender = emailSender;
        this.notificationSubject = notificationSubject;
        this.webURL = webURL;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
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
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (feedbackRequest.getId() != null) {
            throw new BadArgException("Attempted to save feedback request with non-auto-populated ID");
        }


        if (feedbackRequest.getDueDate() != null && feedbackRequest.getSendDate().isAfter(feedbackRequest.getDueDate())){
            throw new BadArgException("Send date of feedback request must be before the due date.");
        }

        FeedbackRequest storedRequest = feedbackReqRepository.save(feedbackRequest);
        sendNewRequestEmail(storedRequest);
        return storedRequest;
    }

    private void sendNewRequestEmail(FeedbackRequest storedRequest) {
        MemberProfile creator = memberProfileServices.getById(storedRequest.getCreatorId());
        MemberProfile requestee = memberProfileServices.getById(storedRequest.getRequesteeId());
        String senderName = creator.getFirstName() + " " + creator.getLastName();
        String newContent = "<h1>You have received a feedback request.</h1>" +
                "<p><b>" + senderName + "</b> is requesting feedback on <b>" + requestee.getFirstName() + " " + requestee.getLastName() + "</b> from you.</p>";
        if (storedRequest.getDueDate() != null) {
            newContent += "<p>This request is due on " + storedRequest.getDueDate().getMonth() + " " + storedRequest.getDueDate().getDayOfMonth()+ ", " + storedRequest.getDueDate().getYear() + ".";
        }
        newContent += "<p>Please go to your unique link at " + webURL + "/feedback/submit?request=" + storedRequest.getId() + " to complete this request.</p>";

//        LOG.warn("Pretending to send an email about the new request to "+memberProfileServices.getById(storedRequest.getRecipientId()).getFirstName());
        if(!storedRequest.getRecipientId().equals(storedRequest.getRequesteeId())) {
            emailSender.sendEmail(senderName, creator.getWorkEmail(), notificationSubject, newContent, memberProfileServices.getById(storedRequest.getRecipientId()).getWorkEmail());
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

        boolean reassignAttempted = !Objects.equals(originalFeedback.getRecipientId(), feedbackRequest.getRecipientId());
        boolean dueDateUpdateAttempted = !Objects.equals(originalFeedback.getDueDate(), feedbackRequest.getDueDate());
        boolean submitDateUpdateAttempted = !Objects.equals(originalFeedback.getSubmitDate(), feedbackRequest.getSubmitDate());

        // If a status update is made to anything other than submitted by the requestee, throw an error.
        if (!feedbackRequest.getStatus().equals("submitted") && !Objects.equals(originalFeedback.getStatus(), feedbackRequest.getStatus())) {
            if(currentUserServices.getCurrentUser().getId().equals(originalFeedback.getRequesteeId())) {
                throw new PermissionException("You are not authorized to do this operation");
            }
        }

        if (reassignAttempted) {
            if(!reassignIsPermitted(originalFeedback)) {
                throw new PermissionException("You are not authorized to do this operation");
            }
            feedbackRequest.setStatus("sent");
        }

        if (feedbackRequest.getStatus().equals("canceled") && originalFeedback.getStatus().equals("submitted")) {
            throw new BadArgException("Attempted to cancel a feedback request that was already submitted");
        }

        if (dueDateUpdateAttempted && !updateDueDateIsPermitted(originalFeedback)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (dueDateUpdateAttempted && !updateDueDateIsPermitted(originalFeedback)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (submitDateUpdateAttempted && !updateSubmitDateIsPermitted(originalFeedback)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (feedbackRequest.getDueDate() != null && originalFeedback.getSendDate().isAfter(feedbackRequest.getDueDate())){
            throw new BadArgException("Send date of feedback request must be before the due date.");
        }

        FeedbackRequest storedRequest = feedbackReqRepository.update(feedbackRequest);

        // Send email if the feedback request has been reopened for edits
        if (originalFeedback.getStatus().equals("submitted") && feedbackRequest.getStatus().equals("sent")) {
            MemberProfile creator = memberProfileServices.getById(storedRequest.getCreatorId());
            MemberProfile requestee = memberProfileServices.getById(storedRequest.getRequesteeId());
            String senderName = creator.getFirstName() + " " + creator.getLastName();
            String newContent = "<h1>You have received edit access to a feedback request.</h1>" +
                    "<p><b>" + senderName +
                    "</b> has reopened the feedback request on <b>" +
                    requestee.getFirstName() + " " + requestee.getLastName() + "</b> from you." +
                    "You may make changes to your answers, but you will need to submit the form again when finished.</p>";
            newContent += "<p>Please go to your unique link at " + webURL + "/feedback/submit?request=" + storedRequest.getId() + " to complete this request.</p>";
//            LOG.warn("Pretending to send an email about the reopened request to "+memberProfileServices.getById(storedRequest.getRecipientId()).getFirstName());
            emailSender.sendEmail(senderName, creator.getWorkEmail(), notificationSubject, newContent, memberProfileServices.getById(storedRequest.getRecipientId()).getWorkEmail());
        }

        // Send email if the feedback request has been reassigned
        if (reassignAttempted) {
            sendNewRequestEmail(storedRequest);
        }

        return storedRequest;
    }

    @Override
    public Boolean delete(UUID id) {
        final Optional<FeedbackRequest> feedbackReq = feedbackReqRepository.findById(id);
        if (feedbackReq.isEmpty()) {
            throw new NotFoundException("No feedback request with id " + id);
        }

        if (!createIsPermitted(feedbackReq.get().getRequesteeId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        feedbackReqRepository.deleteById(id);
        return true;
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
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackReq.get();
    }

    @Override
    public List<FeedbackRequest> findByValues(UUID creatorId, UUID requesteeId, UUID recipientId, LocalDate oldestDate, UUID reviewPeriodId, UUID templateId, List<UUID> requesteeIds) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (currentUserId == null) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        List<FeedbackRequest> feedbackReqList = new ArrayList<>();
        if(requesteeIds != null && !requesteeIds.isEmpty()) {
            LOG.debug("Finding feedback requests for {} requesteeIds.", requesteeIds.size());
            feedbackReqList.addAll(feedbackReqRepository.findByValues(Util.nullSafeUUIDToString(creatorId), Util.nullSafeUUIDToString(recipientId), oldestDate, Util.nullSafeUUIDToString(reviewPeriodId), Util.nullSafeUUIDToString(templateId), Util.nullSafeUUIDListToStringList(requesteeIds)));
        } else {
            LOG.debug("Finding feedback requests one or fewer requesteeIds: {}", requesteeId);
            feedbackReqList.addAll(feedbackReqRepository.findByValues(Util.nullSafeUUIDToString(creatorId), Util.nullSafeUUIDToString(requesteeId), Util.nullSafeUUIDToString(recipientId), oldestDate, Util.nullSafeUUIDToString(reviewPeriodId), Util.nullSafeUUIDToString(templateId)));
        }

        feedbackReqList = feedbackReqList.stream().filter((FeedbackRequest request) -> {
            boolean visible = false;
            if(currentUserServices.isAdmin()){
                visible = true;
            } else if(request != null) {
                if(currentUserId.equals(request.getCreatorId())) visible = true;
                if(isSupervisor(request.getRequesteeId(), currentUserId)) visible = true;
                if(currentUserId.equals(request.getRecipientId())) visible = true;
            }
            return visible;
        }).collect(Collectors.toList());

        return feedbackReqList;
    }

    private boolean isSupervisor(UUID requesteeId, UUID currentUserId) {
        return requesteeId != null ? memberProfileServices.getSupervisorsForId(requesteeId).stream().filter((profile) -> currentUserId.equals(profile.getId())).findAny().isPresent() : false;
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
        if (isAdmin) {
            return true;
        } else if (currentUserId.equals(feedbackRequest.getCreatorId())) {
            if (feedbackRequest.getSubmitDate() != null) {
                return true;
            }
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
}

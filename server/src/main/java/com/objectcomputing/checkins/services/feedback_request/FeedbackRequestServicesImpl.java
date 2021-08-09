package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.gcp.chat.GoogleChatBot;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;


@Singleton
public class FeedbackRequestServicesImpl implements FeedbackRequestServices {

    public static final String FEEDBACK_REQUEST_NOTIFICATION_SUBJECT = "check-ins.application.feedback.notifications.subject";
    public static final String FEEDBACK_REQUEST_NOTIFICATION_CONTENT = "check-ins.application.feedback.notifications.content";
    public static final String submitURL = "https://checkins.objectcomputing.com/feedback/submit?requestId=";
    private final FeedbackRequestRepository feedbackReqRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private EmailSender emailSender;
    private final String notificationSubject;
    private final String notificationContent;
    private GoogleChatBot googleChatBot;

    public FeedbackRequestServicesImpl(FeedbackRequestRepository feedbackReqRepository,
                                       CurrentUserServices currentUserServices,
                                       MemberProfileServices memberProfileServices, EmailSender emailSender,
                                       @Property(name = FEEDBACK_REQUEST_NOTIFICATION_SUBJECT) String notificationSubject,
                                       @Property(name = FEEDBACK_REQUEST_NOTIFICATION_CONTENT) String notificationContent,
                                       GoogleChatBot googleChatBot) {
        this.feedbackReqRepository = feedbackReqRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.emailSender = emailSender;
        this.notificationContent = notificationContent;
        this.notificationSubject = notificationSubject;
        this.googleChatBot = googleChatBot;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
    public void setGoogleChatBot(GoogleChatBot googleChatBot) {this.googleChatBot = googleChatBot;}

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

        FeedbackRequest storedRequest = feedbackReqRepository.save(feedbackRequest);
        String newContent =  notificationContent + "<a href=\""+submitURL+storedRequest.getId()+"\">Check-Ins application</a>.";
        String googleChatContent = "You have received a feedback request. Please go to your unique link at " + submitURL+storedRequest.getId() + " to complete this request.";
        emailSender.sendEmail(notificationSubject, newContent, memberProfileServices.getById(storedRequest.getRecipientId()).getWorkEmail());
        if (googleChatBot != null) {
            googleChatBot.sendChat(googleChatContent, memberProfileServices.getById(storedRequest.getRecipientId()).getWorkEmail());
        }
        return storedRequest;
    }

    @Override
    public FeedbackRequest update(FeedbackRequest feedbackRequest) {
        //only creator can update due date--only field they can update without making new request
        //status has to be updated with any permissions--fired on submission from any recipient
        //submit date can be updated only when the recipient is logged in--fired on submission from any recipient
        FeedbackRequest originalFeedback = null;
        if (feedbackRequest.getId() != null) {
            originalFeedback = getById(feedbackRequest.getId());
        }
        if (originalFeedback == null) {
            throw new BadArgException("Cannot update feedback request that does not exist");
        }

        validateMembers(originalFeedback);

        feedbackRequest.setCreatorId(originalFeedback.getCreatorId());
        feedbackRequest.setRecipientId(originalFeedback.getRecipientId());
        feedbackRequest.setRequesteeId(originalFeedback.getRequesteeId());
        feedbackRequest.setTemplateId(originalFeedback.getTemplateId());
        feedbackRequest.setSendDate(originalFeedback.getSendDate());
        feedbackRequest.setTemplateId(originalFeedback.getTemplateId());

        boolean dueDateUpdateAttempted = !Objects.equals(originalFeedback.getDueDate(), feedbackRequest.getDueDate());
        boolean submitDateUpdateAttempted = !Objects.equals(originalFeedback.getSubmitDate(), feedbackRequest.getSubmitDate());

        if (dueDateUpdateAttempted && !updateDueDateIsPermitted(feedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (submitDateUpdateAttempted && !updateSubmitDateIsPermitted(feedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackReqRepository.update(feedbackRequest);
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
    public List<FeedbackRequest> findByValues(UUID creatorId, UUID requesteeId, LocalDate oldestDate) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        UUID currentUserId = currentUser.getId();

        List<FeedbackRequest> feedbackReqList = new ArrayList<>();
        if (currentUserId != null) {
            //users should be able to filter by only requests they have created
            if (currentUserId.equals(creatorId) || currentUserServices.isAdmin()) {
                feedbackReqList.addAll(feedbackReqRepository.findByValues(Util.nullSafeUUIDToString(creatorId), Util.nullSafeUUIDToString(requesteeId), oldestDate));
            } else {
                throw new PermissionException("You are not authorized to do this operation");
            }

        }

        return feedbackReqList;
    }

    private boolean createIsPermitted(UUID requesteeId) {
        final boolean isAdmin = currentUserServices.isAdmin();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final UUID requesteePDL = memberProfileServices.getById(requesteeId).getPdlId();

        //a PDL may create a request for a user who is assigned to them
        return isAdmin || currentUserId.equals(requesteePDL);
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
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return isAdmin || currentUserId.equals(feedbackRequest.getCreatorId());
    }

    private boolean updateSubmitDateIsPermitted(FeedbackRequest feedbackRequest) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return isAdmin || currentUserId.equals(feedbackRequest.getRecipientId());
    }
}

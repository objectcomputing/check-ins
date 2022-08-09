package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetConfig;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class FeedbackRequestServicesImpl implements FeedbackRequestServices {

    public static final String FEEDBACK_REQUEST_NOTIFICATION_SUBJECT = "check-ins.application.feedback.notifications.subject";
    public static final String WEB_UI_URL = "check-ins.web-address";
    private final FeedbackRequestRepository feedbackReqRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private EmailSender emailSender;
    private final String notificationSubject;
    private final String webURL;

    public FeedbackRequestServicesImpl(FeedbackRequestRepository feedbackReqRepository,
                                       CurrentUserServices currentUserServices,
                                       MemberProfileRetrievalServices memberProfileRetrievalServices,
                                       @Named(MailJetConfig.HTML_FORMAT) EmailSender emailSender,
                                       @Property(name = FEEDBACK_REQUEST_NOTIFICATION_SUBJECT) String notificationSubject,
                                       @Property(name = WEB_UI_URL) String webURL
    )
            {
        this.feedbackReqRepository = feedbackReqRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.emailSender = emailSender;
        this.notificationSubject = notificationSubject;
        this.webURL = webURL;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    private void validateMembers(FeedbackRequest feedbackRequest) {
        memberProfileRetrievalServices.getById(feedbackRequest.getCreatorId()).orElseThrow(() -> {
            throw new BadArgException("Cannot save feedback request with invalid creator ID");
        });

        memberProfileRetrievalServices.getById(feedbackRequest.getRecipientId()).orElseThrow(() -> {
            throw new BadArgException("Cannot save feedback request with invalid recipient ID");
        });

        memberProfileRetrievalServices.getById(feedbackRequest.getRequesteeId()).orElseThrow(() -> {
            throw new BadArgException("Cannot save feedback request with invalid requestee ID");
        });

        validate(!feedbackRequest.getRequesteeId().equals(feedbackRequest.getRecipientId())).orElseThrow(() -> {
            throw new BadArgException("The requestee must not be the same person as the recipient");
        });
    }

    @Override
    public FeedbackRequest save(FeedbackRequest feedbackRequest) {
        validateMembers(feedbackRequest);

        validate(createIsPermitted(feedbackRequest.getRequesteeId())).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });
        validate(feedbackRequest.getId() == null).orElseThrow(() -> {
            throw new BadArgException("Attempted to save feedback request with non-auto-populated ID");
        });
        validate(feedbackRequest.getDueDate() == null || feedbackRequest.getSendDate().isBefore(feedbackRequest.getDueDate())).orElseThrow(() -> {
            throw new BadArgException("Send date of feedback request must be before the due date.");
        });

        FeedbackRequest storedRequest = feedbackReqRepository.save(feedbackRequest);
        MemberProfile creator = memberProfileRetrievalServices.getById(storedRequest.getCreatorId()).orElseThrow(() -> {
            throw new BadArgException("The creator of the feedback request does not exist");
        });
        MemberProfile requestee = memberProfileRetrievalServices.getById(storedRequest.getRequesteeId()).orElseThrow(() -> {
            throw new BadArgException("The requestee of the feedback request does not exist");
        });
        String newContent = "<h1>You have received a feedback request.</h1>" + 
        "<p><b>" + creator.getFirstName() + " " + creator.getLastName() + "</b> is requesting feedback on <b>" + requestee.getFirstName() + " " + requestee.getLastName() + "</b> from you.</p>";
        if (storedRequest.getDueDate() != null) {
            newContent += "<p>This request is due on " + storedRequest.getDueDate().getMonth() + " " + storedRequest.getDueDate().getDayOfMonth()+ ", " +storedRequest.getDueDate().getYear() + ".";
        }
        newContent += "<p>Please go to your unique link at " + webURL + "/feedback/submit?request=" + storedRequest.getId() + " to complete this request.</p>";

        emailSender.sendEmail(notificationSubject, newContent, memberProfileRetrievalServices.getById(storedRequest.getRecipientId()).orElseThrow(() -> {
            throw new BadArgException("Email recipient %s does not exist", storedRequest.getRecipientId());
        }).getWorkEmail());
        return storedRequest;
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

        validate(originalFeedback != null).orElseThrow(() -> {
            throw new BadArgException("Cannot update feedback request that does not exist");
        });

        validateMembers(originalFeedback);

        boolean dueDateUpdateAttempted = !Objects.equals(originalFeedback.getDueDate(), feedbackRequest.getDueDate());
        boolean submitDateUpdateAttempted = !Objects.equals(originalFeedback.getSubmitDate(), feedbackRequest.getSubmitDate());

        validate(!(feedbackRequest.getStatus().equals("canceled") && originalFeedback.getStatus().equals("submitted"))).orElseThrow(() -> {
            throw new BadArgException("Attempted to cancel a feedback request that was already submitted");
        });
        validate(!dueDateUpdateAttempted || updateDueDateIsPermitted(feedbackRequest)).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });
        validate(!submitDateUpdateAttempted || updateSubmitDateIsPermitted(originalFeedback)).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });
        validate(feedbackRequest.getDueDate() == null || originalFeedback.getSendDate().isBefore(feedbackRequest.getDueDate())).orElseThrow(() -> {
            throw new BadArgException("Send date of feedback request must be before the due date.");
        });

        FeedbackRequest storedRequest = feedbackReqRepository.update(feedbackRequest);

        // Send email if the feedback request has been reopened for edits
        if (originalFeedback.getStatus().equals("submitted") && feedbackRequest.getStatus().equals("sent")) {
            MemberProfile creator = memberProfileRetrievalServices.getById(storedRequest.getCreatorId()).orElseThrow();
            MemberProfile requestee = memberProfileRetrievalServices.getById(storedRequest.getRequesteeId()).orElseThrow();
            MemberProfile recipient = memberProfileRetrievalServices.getById(storedRequest.getRecipientId()).orElseThrow();
            String newContent = "<h1>You have received edit access to a feedback request.</h1>" +
                    "<p><b>" + creator.getFirstName() + " " + creator.getLastName() +
                    "</b> has reopened the feedback request on <b>" +
                    requestee.getFirstName() + " " + requestee.getLastName() + "</b> from you. " +
                    "You may make changes to your answers, but you will need to submit the form again when finished.</p>";
            newContent += "<p>Please go to your unique link at " + webURL + "/feedback/submit?request=" + storedRequest.getId() + " to complete this request.</p>";

            emailSender.sendEmail(notificationSubject, newContent, recipient.getWorkEmail());
        }

        return storedRequest;
    }

    @Override
    public Boolean delete(UUID id) {
        FeedbackRequest feedbackReq = feedbackReqRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No feedback request with id " + id);
        });

        validate(createIsPermitted(feedbackReq.getRequesteeId())).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        feedbackReqRepository.deleteById(id);
        return true;
    }

    @Override
    public FeedbackRequest getById(UUID id) {
        FeedbackRequest feedbackReq = feedbackReqRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No feedback req with id %s", id);
        });

        final LocalDate sendDate = feedbackReq.getSendDate();
        final UUID requesteeId = feedbackReq.getRequesteeId();
        final UUID recipientId = feedbackReq.getRecipientId();
        validate(getIsPermitted(requesteeId, recipientId, sendDate)).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        return feedbackReq;
    }

    @Override
    public List<FeedbackRequest> findByValues(UUID creatorId, UUID requesteeId, UUID recipientId, LocalDate oldestDate) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();

        List<FeedbackRequest> feedbackReqList = new ArrayList<>();
        if (currentUserId != null) {
            //users should be able to filter by only requests they have created
            validate(currentUserId.equals(creatorId) || currentUserId.equals(recipientId) || currentUserServices.isAdmin()).orElseThrow(() -> {
                throw new PermissionException("You are not authorized to do this operation");
            });

            feedbackReqList.addAll(feedbackReqRepository.findByValues(Util.nullSafeUUIDToString(creatorId), Util.nullSafeUUIDToString(requesteeId), Util.nullSafeUUIDToString(recipientId), oldestDate));
        }

        return feedbackReqList;
    }

    private boolean createIsPermitted(UUID requesteeId) {
        final boolean isAdmin = currentUserServices.isAdmin();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final UUID requesteePDL = memberProfileRetrievalServices.getById(requesteeId).orElseThrow(() -> {
            throw new BadArgException("Requestee with member ID %s does not exist", requesteeId);
        }).getPdlId();

        //a PDL may create a request for a user who is assigned to them
        return isAdmin || currentUserId.equals(requesteePDL);
    }

    private boolean getIsPermitted(UUID requesteeId, UUID recipientId, LocalDate sendDate) {
        LocalDate today = LocalDate.now();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();

        // The recipient can only access the feedback request after it has been sent
        if (currentUserId.equals(recipientId)) {
            validate(sendDate.isBefore(today)).orElseThrow(() -> {
                throw new PermissionException("You are not permitted to access this request before the send date.");
            });
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

        return feedbackRequest;
    }
}

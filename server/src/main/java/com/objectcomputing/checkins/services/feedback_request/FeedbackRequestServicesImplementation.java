package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;


@Singleton
public class FeedbackRequestServicesImplementation implements FeedbackRequestServices {

    private final FeedbackRequestRepository feedbackReqRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestServicesImplementation.class);

    public FeedbackRequestServicesImplementation(FeedbackRequestRepository feedbackReqRepository,
                                                 CurrentUserServices currentUserServices,
                                                 MemberProfileServices memberProfileServices) {
        this.feedbackReqRepository = feedbackReqRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    private void validateMembers(FeedbackRequest feedbackRequest) {
        if (feedbackRequest == null) {
            throw new BadArgException("Cannot validate members; feedback request does not exist");
        }

        try {
            memberProfileServices.getById(feedbackRequest.getCreatorId());
        } catch (NotFoundException e) {
            throw new BadArgException("The creator ID is invalid");
        }

        try {
            memberProfileServices.getById(feedbackRequest.getRecipientId());
        } catch (NotFoundException e) {
            throw new BadArgException("The recipient ID is invalid");
        }

        try {
            memberProfileServices.getById(feedbackRequest.getRequesteeId());
        } catch (NotFoundException e) {
            throw new BadArgException("The requestee ID is invalid");
        }
    }

    @Override
    public FeedbackRequest save(FeedbackRequest feedbackRequest) {
        validateMembers(feedbackRequest);
        if (!createIsPermitted(feedbackRequest.getRequesteeId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (feedbackRequest.getId() == null) {
            return feedbackReqRepository.save(feedbackRequest);
        }

        return feedbackReqRepository.update(feedbackRequest);
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

        boolean statusUpdateAttempted = !originalFeedback.getStatus().equals(feedbackRequest.getStatus());
        boolean dueDateUpdateAttempted = !Objects.equals(originalFeedback.getDueDate(), feedbackRequest.getDueDate());
        boolean submitDateUpdateAttempted = !Objects.equals(originalFeedback.getSubmitDate(), feedbackRequest.getSubmitDate());

        if (statusUpdateAttempted && !updateStatusIsPermitted(feedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (dueDateUpdateAttempted && !updateDueDateIsPermitted(feedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (submitDateUpdateAttempted && !updateSubmitDateIsPermitted(feedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        FeedbackRequest response = feedbackReqRepository.update(feedbackRequest);
        LOG.info(response.toString());
        return response;

}


    @Override
    public Boolean delete(UUID id) {
        final Optional<FeedbackRequest> feedbackReq = feedbackReqRepository.findById(id);
        if (!feedbackReq.isPresent()) {
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
        if (!feedbackReq.isPresent()) {
            throw new NotFoundException("No feedback req with id " + id);
        }

        final UUID requesteeId = feedbackReq.get().getRequesteeId();
        final UUID recipientId = feedbackReq.get().getRecipientId();
        if (!getIsPermitted(requesteeId, recipientId)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackReq.get();
    }

    @Override
    public List<FeedbackRequest> findByValues(UUID creatorId, UUID requesteeId, UUID templateId, LocalDate oldestDate) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        UUID currentUserId = currentUser.getId();

        List<FeedbackRequest> feedbackReqList = new ArrayList<>();
        if (currentUserId != null) {
            //users should be able to filter by only requests they have created
            if (currentUserId.equals(creatorId) || currentUserServices.isAdmin()) {
                feedbackReqList.addAll(feedbackReqRepository.findByValues(creatorId, requesteeId, templateId, oldestDate));
            } else {
                throw new PermissionException("You are not authorized to do this operation");
            }

        }

        return feedbackReqList;
    }

    private boolean createIsPermitted(@NotNull UUID requesteeId) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();

        if (currentUserServices.isAdmin()) {
            return true;
        }
        final UUID requesteePDL = memberProfileServices.getById(requesteeId).getPdlId();

        //a PDL may create a request for a user who is assigned to them
        if (currentUserId.equals(requesteePDL)) {
            return true;
        }
        //TODO: Can a person's supervisor send a feedback request?
        return false;
    }

    private boolean getIsPermitted(@NotNull UUID requesteeId, @NotNull UUID recipientId) {
        return createIsPermitted(requesteeId) || currentUserServices.getCurrentUser().getId().equals(recipientId);
    }

    private boolean updateStatusIsPermitted(FeedbackRequest feedbackRequest) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return isAdmin
                || currentUserId.equals(feedbackRequest.getRecipientId())
                || currentUserId.equals(feedbackRequest.getCreatorId());
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

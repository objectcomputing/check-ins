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
        //TODO: should overdue status be made part of backend, or should it be something calculated only on front end from
        //dueDate and the current date?
        FeedbackRequest updatedFeedback = null;
        if (feedbackRequest.getId() != null) {
            updatedFeedback = getById(feedbackRequest.getId());
        }
        if (updatedFeedback != null) {
            LOG.info("updated feedback object: {}", updatedFeedback.toString());
            LOG.info("feedback request passed into function: {}", feedbackRequest.toString());
            try {
                memberProfileServices.getById(updatedFeedback.getCreatorId());
                memberProfileServices.getById(updatedFeedback.getRequesteeId());
            } catch (NotFoundException e) {
                throw new BadArgException("Either the creator id or the requestee id is invalid");
            }
            UUID currentUserId = currentUserServices.getCurrentUser().getId();

            if (currentUserId.equals(updatedFeedback.getCreatorId()) && feedbackRequest.getDueDate()!=null) {
                    LOG.info("going to update due date");
                    LOG.info(feedbackRequest.getId().toString());
                    LOG.info(feedbackRequest.getDueDate().toString());
                    FeedbackRequest response = feedbackReqRepository.updateDueDate(feedbackRequest.getId(), feedbackRequest.getDueDate());
                    LOG.info(response.toString());
                    return feedbackReqRepository.findById(feedbackRequest.getId()).get();

            }
            if (currentUserId.equals(updatedFeedback.getRecipientId())) {
                feedbackReqRepository.update(feedbackRequest.getId(),feedbackRequest.getStatus(), feedbackRequest.getSubmitDate());
                return feedbackReqRepository.findById(feedbackRequest.getId()).get();
            }
        }
        throw new PermissionException("You are not authorized to do any updates");

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
    public List<FeedbackRequest> findByValues(UUID creatorId, UUID requesteeId, UUID templateId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        UUID currentUserId = currentUser.getId();
        List<FeedbackRequest> feedbackReqList = new ArrayList<>();
        if (currentUserId != null) {
            //users should be able to filter by only requests they have created
            if (currentUserId.equals(creatorId) || currentUserServices.isAdmin()) {
                feedbackReqList.addAll(feedbackReqRepository.findByCreatorId(creatorId));
                if (requesteeId != null && templateId !=null) {
                    feedbackReqList.retainAll(feedbackReqRepository.findByRequesteeIdAndTemplateId(requesteeId, templateId));
                }
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
}

package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;


@Singleton
public class FeedbackRequestServicesImplementation implements FeedbackRequestServices {

    private final FeedbackRequestRepository feedbackReqRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public FeedbackRequestServicesImplementation(FeedbackRequestRepository feedbackReqRepository,
                                                 CurrentUserServices currentUserServices,
                                                 MemberProfileServices memberProfileServices) {
        this.feedbackReqRepository = feedbackReqRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public FeedbackRequest save(FeedbackRequest feedbackRequest) {
        try {
            memberProfileServices.getById(feedbackRequest.getCreatorId());
            memberProfileServices.getById(feedbackRequest.getRequesteeId());
        } catch (NotFoundException e) {
            throw new BadArgException("Either the creator id or the requestee id is invalid");
        }
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
        FeedbackRequest updatedFeedback = null;
        if (feedbackRequest.getId() != null) {
            updatedFeedback = getById(feedbackRequest.getId());
        }
        if (updatedFeedback != null) {
            try {
                memberProfileServices.getById(updatedFeedback.getCreatorId());
                memberProfileServices.getById(updatedFeedback.getRequesteeId());
            } catch (NotFoundException e) {
                throw new BadArgException("Either the creator id or the requestee id is invalid");
            }

            if (!createIsPermitted(feedbackRequest.getRequesteeId())) {
                throw new PermissionException("You are not authorized to do this operation");
            }
        }

        return feedbackReqRepository.update(feedbackRequest);
    }

    @Override
    public Boolean delete(UUID id) {
        final Optional<FeedbackRequest> feedbackReq = feedbackReqRepository.findById(id);
        if (!feedbackReq.isPresent()) {
            throw new NotFoundException("No feedback request with id " + id);
        }

        if (!createIsPermitted(feedbackReq.get().getCreatorId())) {
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
        if (!getIsPermitted(requesteeId)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackReq.get();
    }


    @Override
    public List<FeedbackRequest> findByValue(UUID creatorId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        UUID currentUserId = currentUser.getId();
        List<FeedbackRequest> feedbackReqList = new ArrayList<>();
        if (currentUserId != null) {
            //users should be able to filter by only requests they have created
            if (currentUserId.equals(creatorId)) {
                feedbackReqList.addAll(feedbackReqRepository.findByCreatorId(creatorId));
            } else {
            throw new PermissionException("You are not authorized to do this operation");
            }
        }

        return feedbackReqList;
    }

    private boolean getIsPermitted(@NotNull UUID requesteeId) {
        return createIsPermitted(requesteeId) || currentUserServices.getCurrentUser().getId() == requesteeId;
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
}

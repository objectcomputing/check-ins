package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleServices;

import javax.inject.Singleton;

import com.objectcomputing.checkins.services.role.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;


@Singleton
public class FeedbackRequestServicesImplementation implements FeedbackRequestServices {

    private final FeedbackRequestRepository feedbackReqRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final RoleServices roleServices;

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestServicesImplementation.class);

    public FeedbackRequestServicesImplementation(FeedbackRequestRepository feedbackReqRepository,
                                                 CurrentUserServices currentUserServices,
                                                 MemberProfileServices memberProfileServices,
                                                 RoleServices roleServices) {
        this.feedbackReqRepository = feedbackReqRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.roleServices = roleServices;
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
//        FeedbackRequest updatedFeedbackRequest = null;
//        if (feedbackRequest.getId() != null) {
//            updatedFeedbackRequest = getById(feedbackRequest.getId());
//        }
//        if (updatedFeedbackRequest != null) {
//            try {
//                memberProfileServices.getById(updatedFeedbackRequest.getCreatorId());
//                memberProfileServices.getById(updatedFeedbackRequest.getRequesteeId());
//            } catch (NotFoundException e) {
//                throw new BadArgException("Either the sender id or the requestee id is invalid");
//            }
//
//            if (!isPermitted(currentUserServices.getCurrentUser().getId())) {
//                throw new PermissionException("You are not authorized to do this operation");
//            }
//            feedbackRequest.setSentBy(updatedFeedback.getSentBy());
//            feedbackRequest.setSentTo(updatedFeedback.getSentTo());
//            feedbackRequest.setCreatedOn(updatedFeedback.getCreatedOn());
//
//            return feedbackReqRepository.update(feedbackRequest);
//        } else {
//            throw new NotFoundException("This feedback req does not exist");
//        }
        return null;
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

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final UUID creatorId = feedbackReq.get().getCreatorId();
        final UUID requesteeId = feedbackReq.get().getRequesteeId();
        final UUID requesteePDL = memberProfileServices.getById(requesteeId).getPdlId();

        if (!getIsPermitted(requesteeId)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackReq.get();


    }

    @Override
    public List<FeedbackRequest> findByRequesteeId(UUID requesteeId) {
        return null;
    }

    @Override
    public List<FeedbackRequest> findByCreatorId(UUID creatorId) {
        return null;
    }

    private boolean getIsPermitted(@NotNull UUID requesteeId) {
        return createIsPermitted(requesteeId) || currentUserServices.getCurrentUser().getId() == requesteeId;
    }

    private boolean createIsPermitted(@NotNull UUID requesteeId) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final Role currentUserRole = roleServices.read(currentUserId);

        if (currentUserServices.isAdmin()) {
            return true;
        }
        final UUID requesteePDL = memberProfileServices.getById(requesteeId).getPdlId();

        if (currentUserId.equals(requesteePDL)) {
            return true;
        }
        //TODO: Can a person's supervisor send a feedback request?

        return false;
    }
}

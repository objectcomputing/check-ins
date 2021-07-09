package com.objectcomputing.checkins.services.feedback_request_questions;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FeedbackRequestQuestionServicesImpl implements FeedbackRequestQuestionServices{
    private final FeedbackRequestServices feedbackReqServices;
    private final FeedbackRequestQuestionRepository feedbackReqQuestionRepo;
    private final CurrentUserServices currentUserServices;
    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestQuestionServicesImpl.class);

    public FeedbackRequestQuestionServicesImpl(FeedbackRequestServices feedbackReqServices,
                                           FeedbackRequestQuestionRepository feedbackReqQuestionRepo,
                                           CurrentUserServices currentUserServices) {
        this.feedbackReqServices = feedbackReqServices;
        this.feedbackReqQuestionRepo = feedbackReqQuestionRepo;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public FeedbackRequestQuestion save(FeedbackRequestQuestion feedbackRequestQuestion) {
        FeedbackRequest feedbackRequest = null;
        if (feedbackRequestQuestion.getRequestId() != null) {
            try {
                feedbackRequest = feedbackReqServices.getById(feedbackRequestQuestion.getRequestId());
            } catch (NotFoundException e) {
                throw new NotFoundException("Request does not exist");
            }

        }
        UUID creatorId = feedbackRequest.getCreatorId();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        LOG.info("creatorid:{}", creatorId);
        LOG.info("current user id:{}", currentUserId);
        if (creatorId != null) {
            if (!creatorId.equals(currentUserId) && !currentUserServices.isAdmin()) {
                LOG.info("Creator id equals current user {} ", !creatorId.equals(currentUserServices.getCurrentUser()));
                LOG.info("current user is admin {}", !currentUserServices.isAdmin());
                LOG.info("{}",!creatorId.equals(currentUserServices.getCurrentUser().getId()) || !currentUserServices.isAdmin());
                throw new PermissionException("You are not authorized to do this operation ");
            }
        }
        if (feedbackRequestQuestion.getId() != null) {
            return feedbackReqQuestionRepo.update(feedbackRequestQuestion);
        }
        return feedbackReqQuestionRepo.save(feedbackRequestQuestion);


    }

    @Override
    public FeedbackRequestQuestion update(FeedbackRequestQuestion feedbackRequestQuestion) {
        Optional<FeedbackRequestQuestion> oldQuestion;
        if (feedbackRequestQuestion.getId() != null) {
            oldQuestion = feedbackReqQuestionRepo.findById(feedbackRequestQuestion.getId());
            if (oldQuestion.isEmpty()) {
                throw new NotFoundException("Could not find question with that ID");
            }
            feedbackRequestQuestion.setRequestId(oldQuestion.get().getRequestId());
            feedbackRequestQuestion.setQuestionContent(oldQuestion.get().getQuestionContent());
            feedbackRequestQuestion.setOrderNum(oldQuestion.get().getOrderNum());
        }
        FeedbackRequest request = feedbackReqServices.getById(feedbackRequestQuestion.getRequestId());
        if (request == null) {
            throw new NotFoundException("Request for question does not exist");
        }
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (!currentUserId.equals(request.getRecipientId()) || currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to do this operation");

        }
        return feedbackReqQuestionRepo.update(feedbackRequestQuestion);


    }

    @Override
    public Boolean delete(UUID id) {
        final Optional<FeedbackRequestQuestion> feedbackReqQ = feedbackReqQuestionRepo.findById(id);
        FeedbackRequest req;
        if (feedbackReqQ.isEmpty()) {
            throw new NotFoundException("No feedback request with id " + id);
        }
        req = feedbackReqServices.getById(feedbackReqQ.get().getRequestId());
        if (req == null) {
            throw new NotFoundException("Could not find request with that ID");
        }
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        feedbackReqQuestionRepo.deleteById(id);
        return true;
    }

    @Override
    public FeedbackRequestQuestion getById(UUID id) {
        final Optional<FeedbackRequestQuestion> feedbackReqQuestion = feedbackReqQuestionRepo.findById(id);
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        FeedbackRequest feedbackRequest;
        if (feedbackReqQuestion.isEmpty()) {
            throw new NotFoundException("No feedback request question with id " + id);
        }
        feedbackRequest = feedbackReqServices.getById(feedbackReqQuestion.get().getRequestId());
        if (feedbackRequest == null) {
            throw new NotFoundException("Attached request for question not found");
        }

        if (!currentUser.getId().equals(feedbackRequest.getRecipientId()) || !currentUser.getId().equals(feedbackRequest.getCreatorId()) || currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackReqQuestion.get();
    }

    @Override
    public List<FeedbackRequestQuestion> findByValues(UUID requestId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        FeedbackRequest feedbackRequest = feedbackReqServices.getById(requestId);
        List<FeedbackRequestQuestion> feedbackRequestQuestionList = new ArrayList<>();
        if (feedbackRequest == null) {
            throw new NotFoundException("Feedback request in search not found");
        }
        UUID creatorId = feedbackRequest.getCreatorId();
        UUID recipientId = feedbackRequest.getRecipientId();
        if (!currentUser.getId().equals(creatorId) || !currentUser.getId().equals(recipientId)) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        feedbackRequestQuestionList.addAll(feedbackReqQuestionRepo.findByRequestId(Util.nullSafeUUIDToString(requestId)));
        return feedbackRequestQuestionList;
    }
}

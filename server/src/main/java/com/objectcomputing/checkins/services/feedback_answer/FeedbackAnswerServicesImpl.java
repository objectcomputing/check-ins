package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_request_questions.FeedbackRequestQuestion;
import com.objectcomputing.checkins.services.feedback_request_questions.FeedbackRequestQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FeedbackAnswerServicesImpl implements FeedbackAnswerServices {

    private final FeedbackAnswerRepository feedbackAnswerRepository;
    private final CurrentUserServices currentUserServices;
    private final FeedbackRequestQuestionServices feedbackReqQuestionServices;
    private final FeedbackRequestServices feedbackRequestServices;

    public FeedbackAnswerServicesImpl(FeedbackAnswerRepository feedbackAnswerRepository,
                                      CurrentUserServices currentUserServices,
                                      FeedbackRequestQuestionServices feedbackReqQuestionServices,
                                      FeedbackRequestServices feedbackRequestServices) {
        this.feedbackAnswerRepository = feedbackAnswerRepository;
        this.currentUserServices = currentUserServices;
        this.feedbackReqQuestionServices = feedbackReqQuestionServices;
        this.feedbackRequestServices = feedbackRequestServices;
    }

    @Override
    public FeedbackAnswer save(FeedbackAnswer feedbackAnswer) {

        if (feedbackAnswer.getId() != null) {
            throw new BadArgException("Attempted to save feedback answer with non-auto-populated ID");
        }

        FeedbackRequest relatedFeedbackRequest = getRelatedFeedbackRequest(feedbackAnswer);
        if (!createIsPermitted(relatedFeedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackAnswerRepository.save(feedbackAnswer);
    }

    @Override
    public FeedbackAnswer update(FeedbackAnswer feedbackAnswer) {
        FeedbackAnswer updatedFeedbackAnswer;

        if (feedbackAnswer.getId() != null) {
            updatedFeedbackAnswer = getById(feedbackAnswer.getId());
        } else {
            throw new BadArgException("Feedback answer does not exist; cannot update");
        }

        feedbackAnswer.setQuestionId(updatedFeedbackAnswer.getQuestionId());

        FeedbackRequest relatedFeedbackRequest = getRelatedFeedbackRequest(feedbackAnswer);
        if (!updateIsPermitted(relatedFeedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackAnswerRepository.update(feedbackAnswer);
    }

    @Override
    public FeedbackAnswer getById(UUID id) {
        final Optional<FeedbackAnswer> feedbackAnswer = feedbackAnswerRepository.findById(id);
        if (feedbackAnswer.isEmpty()) {
            throw new NotFoundException("No feedback answer with id " + id);
        }

        FeedbackRequest relatedFeedbackRequest = getRelatedFeedbackRequest(feedbackAnswer.get());
        if (!getIsPermitted(relatedFeedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackAnswer.get();
    }

    public FeedbackRequest getRelatedFeedbackRequest(FeedbackAnswer feedbackAnswer) {
        FeedbackRequestQuestion question;
        FeedbackRequest feedbackRequest;
        try {
            question = feedbackReqQuestionServices.getById(feedbackAnswer.getQuestionId());
        } catch (NotFoundException e) {
            throw new BadArgException("Attempted to save answer with invalid question ID " + feedbackAnswer.getQuestionId());
        }

        try {
            feedbackRequest = feedbackRequestServices.getById(question.getRequestId());
        } catch (NotFoundException e) {
            throw new BadArgException("Attempted to save answer with nonexistent request ID " + question.getRequestId());
        }

        return feedbackRequest;
    }

    public boolean createIsPermitted(FeedbackRequest feedbackRequest) {
        final UUID recipientId = feedbackRequest.getRecipientId();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return recipientId.equals(currentUserId);
    }

    public boolean updateIsPermitted(FeedbackRequest feedbackRequest) {
        return createIsPermitted(feedbackRequest);
    }

    public boolean getIsPermitted(FeedbackRequest feedbackRequest) {
        final boolean isAdmin = currentUserServices.isAdmin();
        final UUID requestCreatorId = feedbackRequest.getCreatorId();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final UUID recipientId = feedbackRequest.getRecipientId();
        return isAdmin || requestCreatorId.equals(currentUserId) || recipientId.equals(currentUserId);
    }
}

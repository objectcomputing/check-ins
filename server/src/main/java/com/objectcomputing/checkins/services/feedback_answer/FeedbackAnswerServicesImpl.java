package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FeedbackAnswerServicesImpl implements FeedbackAnswerServices {

    private final FeedbackAnswerRepository feedbackAnswerRepository;
    private final CurrentUserServices currentUserServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final FeedbackRequestServices feedbackRequestServices;

    public FeedbackAnswerServicesImpl(FeedbackAnswerRepository feedbackAnswerRepository,
                                      CurrentUserServices currentUserServices,
                                      TemplateQuestionServices templateQuestionServices,
                                      FeedbackRequestServices feedbackRequestServices) {
        this.feedbackAnswerRepository = feedbackAnswerRepository;
        this.currentUserServices = currentUserServices;
        this.templateQuestionServices = templateQuestionServices;
        this.feedbackRequestServices = feedbackRequestServices;
    }

    @Override
    public FeedbackAnswer save(FeedbackAnswer feedbackAnswer) {

        // Ensure that related question exists
        templateQuestionServices.getById(feedbackAnswer.getQuestionId());

        FeedbackRequest relatedFeedbackRequest = getRelatedFeedbackRequest(feedbackAnswer);
        if (!createIsPermitted(relatedFeedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (feedbackAnswer.getId() != null) {
            return update(feedbackAnswer);
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
        feedbackAnswer.setRequestId(updatedFeedbackAnswer.getRequestId());

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

        if (getIsPermitted(relatedFeedbackRequest)) {
            return feedbackAnswer.get();
        } else {
            throw new PermissionException("You are not authorized to do this operation :(");
        }
    }

    @Override
    public FeedbackAnswer findByValues(UUID questionId, UUID requestId) {
        if (questionId == null || requestId == null) {
            throw new BadArgException("Question ID and request ID must be present for find");
        }
        FeedbackRequest feedbackRequest;
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        try {
            feedbackRequest = feedbackRequestServices.getById(requestId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Cannot find attached request for search");
        }
        if (currentUserId.equals(feedbackRequest.getCreatorId()) || currentUserId.equals(feedbackRequest.getRecipientId()) || currentUserServices.isAdmin()) {
            return feedbackAnswerRepository.getByQuestionIdAndRequestId(Util.nullSafeUUIDToString(questionId), Util.nullSafeUUIDToString(requestId));
        }

        throw new PermissionException("You are not authorized to do that operation");

    }

    public FeedbackRequest getRelatedFeedbackRequest(FeedbackAnswer feedbackAnswer) {
        FeedbackRequest feedbackRequest;

        try {
            feedbackRequest = feedbackRequestServices.getById(feedbackAnswer.getRequestId());
        } catch (NotFoundException e) {
            throw new BadArgException("Attempted to save answer with nonexistent request ID " + feedbackAnswer.getRequestId());
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

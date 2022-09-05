package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestStatus;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import java.util.List;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Validation.validate;

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
        validate(createIsPermitted(relatedFeedbackRequest)).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });
        validate(relatedFeedbackRequest.getStatus() != FeedbackRequestStatus.CANCELED).orElseThrow(() -> {
            throw new BadArgException("Attempted to save an answer for a canceled feedback request");
        });

        if (feedbackAnswer.getId() != null) {
            return update(feedbackAnswer);
        }

        return feedbackAnswerRepository.save(feedbackAnswer);
    }

    @Override
    public FeedbackAnswer update(FeedbackAnswer feedbackAnswer) {
        validate(feedbackAnswer.getId() != null).orElseThrow(() -> {
            throw new BadArgException("Feedback answer does not exist; cannot update");
        });
        FeedbackAnswer updatedFeedbackAnswer = getById(feedbackAnswer.getId());

        feedbackAnswer.setQuestionId(updatedFeedbackAnswer.getQuestionId());
        feedbackAnswer.setRequestId(updatedFeedbackAnswer.getRequestId());

        FeedbackRequest relatedFeedbackRequest = getRelatedFeedbackRequest(feedbackAnswer);
        validate(updateIsPermitted(relatedFeedbackRequest)).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        return feedbackAnswerRepository.update(feedbackAnswer);
    }

    @Override
    public FeedbackAnswer getById(UUID id) {
        FeedbackAnswer feedbackAnswer = feedbackAnswerRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No feedback answer with id %s", id);
        });

        FeedbackRequest relatedFeedbackRequest = getRelatedFeedbackRequest(feedbackAnswer);

        validate(getIsPermitted(relatedFeedbackRequest)).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation :(");
        });

        return feedbackAnswer;
    }

    @Override
    public List<FeedbackAnswer> findByValues(@Nullable UUID questionId, @Nullable UUID requestId) {
        FeedbackRequest feedbackRequest;
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        try {
            feedbackRequest = feedbackRequestServices.getById(requestId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Cannot find attached request for search");
        }

        boolean isCreator = currentUserId.equals(feedbackRequest.getCreatorId());
        boolean isRecipient = currentUserId.equals(feedbackRequest.getRecipientId());
        boolean isAdmin = currentUserServices.isAdmin();
        validate(isCreator || isRecipient || isAdmin).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do that operation");
        });

        return new ArrayList<>(feedbackAnswerRepository.getByQuestionIdAndRequestId(Util.nullSafeUUIDToString(questionId), Util.nullSafeUUIDToString(requestId)));
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

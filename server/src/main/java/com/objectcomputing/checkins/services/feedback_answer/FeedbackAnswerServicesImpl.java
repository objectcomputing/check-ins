package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.frozen_template.FrozenTemplate;
import com.objectcomputing.checkins.services.frozen_template.FrozenTemplateServices;
import com.objectcomputing.checkins.services.frozen_template_questions.FrozenTemplateQuestion;
import com.objectcomputing.checkins.services.frozen_template_questions.FrozenTemplateQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FeedbackAnswerServicesImpl implements FeedbackAnswerServices {

    private final FeedbackAnswerRepository feedbackAnswerRepository;
    private final CurrentUserServices currentUserServices;
    private final FrozenTemplateQuestionServices frozenTemplateQServices;
    private final FeedbackRequestServices feedbackRequestServices;
    private final FrozenTemplateServices frozenTemplateServices;

    public FeedbackAnswerServicesImpl(FeedbackAnswerRepository feedbackAnswerRepository,
                                      CurrentUserServices currentUserServices,
                                      FrozenTemplateQuestionServices frozenTemplateQServices,
                                      FeedbackRequestServices feedbackRequestServices,
                                      FrozenTemplateServices frozenTemplateServices) {
        this.feedbackAnswerRepository = feedbackAnswerRepository;
        this.currentUserServices = currentUserServices;
        this.frozenTemplateQServices = frozenTemplateQServices;
        this.feedbackRequestServices = feedbackRequestServices;
        this.frozenTemplateServices = frozenTemplateServices;
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
        boolean permitted = getIsPermitted(relatedFeedbackRequest);

        if (getIsPermitted(relatedFeedbackRequest)) {
            return feedbackAnswer.get();
        } else {
            throw new PermissionException("You are not authorized to do this operation :(");
        }
    }

    public FeedbackRequest getRelatedFeedbackRequest(FeedbackAnswer feedbackAnswer) {
        FrozenTemplateQuestion question;
        FeedbackRequest feedbackRequest;
        try {
            question = frozenTemplateQServices.getById(feedbackAnswer.getQuestionId());
        } catch (NotFoundException e) {
            throw new BadArgException("Attempted to save answer with invalid question ID " + feedbackAnswer.getQuestionId());
        }

        FrozenTemplate template;

        try {
            template = frozenTemplateServices.getById(question.getFrozenTemplateId());
        } catch(NotFoundException e) {
            throw new BadArgException("Attempted to save answer with invalid template attached");
        }

        try {
            feedbackRequest = feedbackRequestServices.getById(template.getRequestId());
        } catch (NotFoundException e) {
            throw new BadArgException("Attempted to save answer with nonexistent request ID " + template.getRequestId());
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

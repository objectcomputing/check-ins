package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FeedbackAnswerServicesImpl implements FeedbackAnswerServices {

    private final FeedbackAnswerRepository feedbackAnswerRepository;

    public FeedbackAnswerServicesImpl(FeedbackAnswerRepository feedbackAnswerRepository) {
        this.feedbackAnswerRepository = feedbackAnswerRepository;
    }

    @Override
    public FeedbackAnswer save(FeedbackAnswer feedbackAnswer) {

        // TODO: Validate that the feedback question ID is valid

        if (!createIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (feedbackAnswer.getId() != null) {
            throw new BadArgException("Attempted to save feedback answer with non-auto-populated ID");
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

        if (!updateIsPermitted()) {
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

        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackAnswer.get();
    }

    public boolean createIsPermitted() {
        // TODO: Check that feedback request recipient ID matches current user
        return true;
    }

    public boolean updateIsPermitted() {
        return createIsPermitted();
    }

    public boolean getIsPermitted() {
        // Current recipient
        // Admin
        // Sender
        return true;
    }
}

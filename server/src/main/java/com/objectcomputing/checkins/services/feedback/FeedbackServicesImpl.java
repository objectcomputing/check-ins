package com.objectcomputing.checkins.services.feedback;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FeedbackServicesImpl implements FeedbackServices {

    private final FeedbackRepository feedbackRepository;
    private final CurrentUserServices currentUserServices;

    public FeedbackServicesImpl(FeedbackRepository feedbackRepository,
                                CurrentUserServices currentUserServices) {
        this.feedbackRepository = feedbackRepository;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Feedback save(@NotNull Feedback feedback) {
        if (feedback.getId() == null) {
            return feedbackRepository.save(feedback);
        }

        if (!isPermitted(feedback.getSentBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackRepository.update(feedback);
    }

    @Override
    public Boolean delete(@NotNull UUID id) {
        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (!feedback.isPresent()) {
            throw new NotFoundException("No feedback for id %s" + id);
        }

        if (!isPermitted(feedback.get().getSentBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        feedbackRepository.deleteById(id);
        return true;
    }

    public List<Feedback> get() {
        return null;
    }

    private boolean isPermitted(@NotNull UUID ownerId) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserServices.isAdmin() || currentUserId.equals(ownerId);
    }
}

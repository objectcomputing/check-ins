package com.objectcomputing.checkins.services.feedback;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
            throw new NotFoundException("No feedback with id %s" + id);
        }

        if (!isPermitted(feedback.get().getSentBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        feedbackRepository.deleteById(id);
        return true;
    }

    @Override
    public Feedback getById(UUID id) {
        /*
        If the current user is not admin, and the feedback is private, and
        the current user is neither the creator nor the receiver of the feedback,
        then thrown permission exception.
        Else, return the feedback.
        * */
        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (!feedback.isPresent()) {
            throw new NotFoundException("No feedback with id %s" + id);
        }

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final UUID creatorId = feedback.get().getSentBy();
        final UUID receiverId = feedback.get().getSentTo();

        if (!currentUserServices.isAdmin() && feedback.get().getConfidential() &&
                !currentUserId.equals(creatorId) && !currentUserId.equals(receiverId)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedback.get();
    }

    @Override
    public List<Feedback> getByValues(@Nullable UUID sentBy,
                                      @Nullable UUID sentTo,
                                      @Nullable Boolean confidential) {
        ArrayList<Feedback> result = new ArrayList<>(feedbackRepository.searchByValues(Util.nullSafeUUIDToString(sentBy),
                Util.nullSafeUUIDToString(sentTo), confidential));

        /*
        If current user is admin, return all entries.
        Else go through all entries:
          - If the entry is public, include it in the final result
          - If the entry is private and the current user is either the creator or
          receiver of the feedback, also include it in the final result
        * */
        return result;
    }

    private boolean isPermitted(@NotNull UUID ownerId) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserServices.isAdmin() || currentUserId.equals(ownerId);
    }
}

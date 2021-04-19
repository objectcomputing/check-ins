package com.objectcomputing.checkins.services.feedback;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class FeedbackServicesImpl implements FeedbackServices {

    private final FeedbackRepository feedbackRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public FeedbackServicesImpl(FeedbackRepository feedbackRepository,
                                CurrentUserServices currentUserServices,
                                MemberProfileServices memberProfileServices) {
        this.feedbackRepository = feedbackRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public Feedback save(@NotNull Feedback feedback) {
        try {
            memberProfileServices.getById(feedback.getSentBy());
            memberProfileServices.getById(feedback.getSentTo());
        } catch (NotFoundException e) {
            throw new BadArgException("Either the sender id or the receiver id is invalid");
        }

        if (feedback.getId() == null) {
            return feedbackRepository.save(feedback);
        }

        if (!isPermitted(feedback.getSentBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackRepository.update(feedback);
    }

    @Override
    public Feedback update(@NotNull Feedback feedback) {
        Feedback updatedFeedback = null;
        if (feedback.getId() != null) {
            updatedFeedback = getById(feedback.getId());
        }
        if (updatedFeedback != null) {
            try {
                memberProfileServices.getById(updatedFeedback.getSentBy());
                memberProfileServices.getById(updatedFeedback.getSentTo());
            } catch (NotFoundException e) {
                throw new BadArgException("Either the sender id or the receiver id is invalid");
            }

            if (!isPermitted(currentUserServices.getCurrentUser().getId())) {
                throw new PermissionException("You are not authorized to do this operation");
            }
            feedback.setSentBy(updatedFeedback.getSentBy());
            feedback.setSentTo(updatedFeedback.getSentTo());
            feedback.setCreatedOn(updatedFeedback.getCreatedOn());

            return feedbackRepository.update(feedback);
        } else {
            throw new NotFoundException("This feedback does not exist");
        }
    }

    @Override
    public Boolean delete(@NotNull UUID id) {
        final Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (!feedback.isPresent()) {
            throw new NotFoundException("No feedback with id " + id);
        }

        if (!isPermitted(feedback.get().getSentBy())) {
            throw new PermissionException("You are not authorized to delete this feedback");
        }

        feedbackRepository.deleteById(id);
        return true;
    }

    @Override
    public Feedback getById(@NotNull UUID id) {
        final Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (!feedback.isPresent()) {
            throw new NotFoundException("No feedback with id " + id);
        }

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final UUID creatorId = feedback.get().getSentBy();
        final UUID receiverId = feedback.get().getSentTo();

        if (!currentUserServices.isAdmin() && feedback.get().getConfidential() &&
                !currentUserId.equals(creatorId) && !currentUserId.equals(receiverId)) {
            throw new PermissionException("You are not authorized to read this feedback");
        }

        return feedback.get();
    }

    @Override
    public List<Feedback> getByValues(@Nullable UUID sentBy,
                                      @Nullable UUID sentTo,
                                      @Nullable Boolean confidential) {
        final ArrayList<Feedback> result = new ArrayList<>(feedbackRepository.searchByValues(Util.nullSafeUUIDToString(sentBy),
                Util.nullSafeUUIDToString(sentTo), confidential));

        if (currentUserServices.isAdmin()) {
            return result;
        }

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final ArrayList<Feedback> toRemove = new ArrayList<>();

        for (Feedback feedback : result) {
            if (feedback.getConfidential() && !currentUserId.equals(feedback.getSentBy())
                    && !currentUserId.equals(feedback.getSentTo())) {
                toRemove.add(feedback);
            }
        }
        result.removeAll(toRemove);

        return result;
    }

    private boolean isPermitted(@NotNull UUID ownerId) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserServices.isAdmin() || currentUserId.equals(ownerId);
    }
}

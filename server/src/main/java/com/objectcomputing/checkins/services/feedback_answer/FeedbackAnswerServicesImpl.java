package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class FeedbackAnswerServicesImpl implements FeedbackAnswerServices {

    private final FeedbackAnswerRepository feedbackAnswerRepository;
    private final CurrentUserServices currentUserServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final FeedbackRequestServices feedbackRequestServices;
    private final MemberProfileServices memberProfileServices;

    public FeedbackAnswerServicesImpl(FeedbackAnswerRepository feedbackAnswerRepository,
                                      CurrentUserServices currentUserServices,
                                      TemplateQuestionServices templateQuestionServices,
                                      FeedbackRequestServices feedbackRequestServices,
                                      MemberProfileServices memberProfileServices) {
        this.feedbackAnswerRepository = feedbackAnswerRepository;
        this.currentUserServices = currentUserServices;
        this.templateQuestionServices = templateQuestionServices;
        this.feedbackRequestServices = feedbackRequestServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public FeedbackAnswer save(FeedbackAnswer feedbackAnswer) {
        // Ensure that related question exists
        templateQuestionServices.getById(feedbackAnswer.getQuestionId());

        FeedbackRequest relatedFeedbackRequest = getRelatedFeedbackRequest(feedbackAnswer);
        if (!createIsPermitted(relatedFeedbackRequest)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        } else if (relatedFeedbackRequest.getStatus().equals("canceled")) {
            throw new BadArgException("Attempted to save an answer for a canceled feedback request");
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
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return feedbackAnswerRepository.update(feedbackAnswer);
    }

    @Override
    // This method cannot have
    // @RequiredPermission(Permission.CAN_VIEW_FEEDBACK_ANSWER) because regular
    // members need to be able to find their answers.  This permission is
    // manually checked inside getIsPermitted().
    public FeedbackAnswer getById(UUID id) {
        final Optional<FeedbackAnswer> feedbackAnswer = feedbackAnswerRepository.findById(id);
        if (feedbackAnswer.isEmpty()) {
            throw new NotFoundException("No feedback answer with id " + id);
        }

        FeedbackRequest relatedFeedbackRequest = getRelatedFeedbackRequest(feedbackAnswer.get());

        if (getIsPermitted(relatedFeedbackRequest)) {
            return feedbackAnswer.get();
        } else {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
    }

    @Override
    // This method cannot have
    // @RequiredPermission(Permission.CAN_VIEW_FEEDBACK_ANSWER) because regular
    // members need to be able to find their answers.  This permission is
    // manually checked below.
    public List<FeedbackAnswer> findByValues(@Nullable UUID questionId, @Nullable UUID requestId) {
        List<FeedbackAnswer> response = new ArrayList<>();
        FeedbackRequest feedbackRequest;
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        try {
            feedbackRequest = feedbackRequestServices.getById(requestId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Cannot find attached request for search");
        }
        final UUID requestCreatorId = feedbackRequest.getCreatorId();
        final UUID requesteeId = feedbackRequest.getRequesteeId();
        final UUID recipientId = feedbackRequest.getRecipientId();
        boolean isRequesteesSupervisor = requesteeId != null ? memberProfileServices.getSupervisorsForId(requesteeId).stream().anyMatch(profile -> currentUserId.equals(profile.getId())) : false;
        MemberProfile requestee = memberProfileServices.getById(requesteeId);
        final UUID requesteePDL = requestee.getPdlId();
        if (currentUserServices.hasPermission(Permission.CAN_VIEW_FEEDBACK_ANSWER) ||
            currentUserId.equals(requesteePDL) || isRequesteesSupervisor ||
            requestCreatorId.equals(currentUserId) ||
            recipientId.equals(currentUserId) ||
            feedbackRequestServices.selfRevieweeIsCurrentUserReviewee(feedbackRequest, currentUserId)) {
            // All checks passed...
            response.addAll(feedbackAnswerRepository.getByQuestionIdAndRequestId(Util.nullSafeUUIDToString(questionId), Util.nullSafeUUIDToString(requestId)));
            return response;
        }

        throw new PermissionException(NOT_AUTHORIZED_MSG);

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
        // Users with the CAN_ADMINISTER_FEEDBACK_ANSWER permission can always
        // get questions and answers.
        if (currentUserServices.hasPermission(Permission.CAN_ADMINISTER_FEEDBACK_ANSWER)) {
            return true;
        }

        // See if the current user is the requestee's supervisor.
        final UUID requesteeId = feedbackRequest.getRequesteeId();
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (requesteeId != null &&
            memberProfileServices.getSupervisorsForId(requesteeId).stream().anyMatch(profile -> currentUserId.equals(profile.getId()))) {
            return true;
        }

        // See if the current user is the requestee's PDL.
        final MemberProfile requestee = memberProfileServices.getById(requesteeId);
        if (currentUserId.equals(requestee.getPdlId())) {
            return true;
        }

        // See if the current user is the request creator or the recipient of
        // the request.
        final UUID requestCreatorId = feedbackRequest.getCreatorId();
        final UUID recipientId = feedbackRequest.getRecipientId();
        if (requestCreatorId.equals(currentUserId) ||
            recipientId.equals(currentUserId)) {
            return true;
        }


        return feedbackRequestServices.selfRevieweeIsCurrentUserReviewee(
                   feedbackRequest, currentUserId);
    }
}

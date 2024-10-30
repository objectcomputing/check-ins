package com.objectcomputing.checkins.services.feedback_answer;

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
    public List<FeedbackAnswer> findByValues(@Nullable UUID questionId, @Nullable UUID requestId, @Nullable UUID externalRecipientId) {
        List<FeedbackAnswer> response = new ArrayList<>();
        FeedbackRequest feedbackRequest;
        MemberProfile currentUser;

        try {
            currentUser = currentUserServices.getCurrentUser();

        } catch (NotFoundException e) {
            currentUser = null;

        }
        final UUID currentUserId = (currentUser != null) ? currentUser.getId() : null;

        try {
            feedbackRequest = feedbackRequestServices.getById(requestId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Cannot find attached request for search");
        }
        final UUID requestCreatorId = feedbackRequest.getCreatorId();
        final UUID requesteeId = feedbackRequest.getRequesteeId();
        final UUID recipientId = feedbackRequest.getRecipientId();
        boolean isRequesteesSupervisor = requesteeId != null && currentUserId != null ? memberProfileServices.getSupervisorsForId(requesteeId).stream().anyMatch(profile -> currentUserId.equals(profile.getId())) : false;
        MemberProfile requestee = memberProfileServices.getById(requesteeId);
        final UUID requesteePDL = requestee.getPdlId();
        if (currentUserServices.isAdmin() || (currentUserId != null && currentUserId.equals(requesteePDL)) || isRequesteesSupervisor || requestCreatorId.equals(currentUserId) || (recipientId != null && recipientId.equals(currentUserId))) {
            response.addAll(feedbackAnswerRepository.getByQuestionIdAndRequestId(Util.nullSafeUUIDToString(questionId), Util.nullSafeUUIDToString(requestId)));
            return response;
        } else if (externalRecipientId != null) {
            response.addAll(feedbackAnswerRepository.getByQuestionIdAndRequestId(Util.nullSafeUUIDToString(questionId), Util.nullSafeUUIDToString(requestId), externalRecipientId.toString()));
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
        MemberProfile currentUser;
        UUID currentUserId;

        try {
            currentUser = currentUserServices.getCurrentUser();
            currentUserId = currentUser.getId();
        } catch (NotFoundException e) {
            currentUser = null;
            currentUserId = null;
        }
        return (recipientId != null && recipientId.equals(currentUserId)) || (currentUserId == null && feedbackRequest.getExternalRecipientId() != null);
    }

    public boolean updateIsPermitted(FeedbackRequest feedbackRequest) {
        return createIsPermitted(feedbackRequest);
    }

    public boolean getIsPermitted(FeedbackRequest feedbackRequest) {
        MemberProfile currentUser;

        try {
            currentUser = currentUserServices.getCurrentUser();
        } catch (NotFoundException e) {
            currentUser = null;
        }
        final UUID currentUserId = (currentUser != null) ? currentUserServices.getCurrentUser().getId() : null;

        final boolean isAdmin = currentUserServices.isAdmin();
        final UUID requestCreatorId = feedbackRequest.getCreatorId();
        UUID requesteeId = feedbackRequest.getRequesteeId();
        MemberProfile requestee = memberProfileServices.getById(requesteeId);
        final UUID recipientId = feedbackRequest.getRecipientId();
        boolean isRequesteesSupervisor;

        if  (requesteeId != null && currentUserId != null) {
            isRequesteesSupervisor = memberProfileServices.getSupervisorsForId(requesteeId).stream().anyMatch(profile -> currentUserId.equals(profile.getId()));
        } else {
            isRequesteesSupervisor = false;
        }

        final UUID requesteePDL = requestee.getPdlId();

        return isAdmin || (currentUserId != null && currentUserId.equals(requesteePDL)) || isRequesteesSupervisor || requestCreatorId.equals(currentUserId) || (recipientId != null && recipientId.equals(currentUserId)) || feedbackRequest.getExternalRecipientId()!= null;
    }
}

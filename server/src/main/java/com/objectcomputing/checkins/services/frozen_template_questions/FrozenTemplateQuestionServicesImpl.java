package com.objectcomputing.checkins.services.frozen_template_questions;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.frozen_template.FrozenTemplate;
import com.objectcomputing.checkins.services.frozen_template.FrozenTemplateServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FrozenTemplateQuestionServicesImpl implements FrozenTemplateQuestionServices {
    private final FrozenTemplateServices frozenTemplateServices;
    private final FrozenTemplateQuestionRepository frozenTemplateQuestionRepository;
    private final CurrentUserServices currentUserServices;
    private final FeedbackRequestServices feedbackReqServices;

    public FrozenTemplateQuestionServicesImpl(FrozenTemplateServices frozenTemplateServices,
                                              FrozenTemplateQuestionRepository frozenTemplateQuestionRepository,
                                              CurrentUserServices currentUserServices,
                                              FeedbackRequestServices feedbackReqServices) {
        this.frozenTemplateServices = frozenTemplateServices;
        this.frozenTemplateQuestionRepository = frozenTemplateQuestionRepository;
        this.currentUserServices = currentUserServices;
        this.feedbackReqServices = feedbackReqServices;
    }

    @Override
    public FrozenTemplateQuestion save(FrozenTemplateQuestion frozenTemplateQuestion) {

        if (frozenTemplateQuestion.getFrozenTemplateId()== null) {
            throw new NotFoundException("Template ID is null");
        }
            FrozenTemplate frozenTemplate;
            frozenTemplate = frozenTemplateServices.getById(frozenTemplateQuestion.getFrozenTemplateId());

            if (frozenTemplate == null) {
                throw new NotFoundException("Attached archived template does not exist");
            }

        UUID requestId = frozenTemplate.getRequestId();
        FeedbackRequest feedbackRequest;
        feedbackRequest= feedbackReqServices.getById(requestId);
        if (feedbackRequest == null) {
            throw new NotFoundException("Attached request does not exist");
        }
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (feedbackRequest.getCreatorId() != null) {
            if (!feedbackRequest.getCreatorId().equals(currentUserId) && !currentUserServices.isAdmin()) {
                throw new PermissionException("You are not authorized to do this operation ");
            }
        }
        if (frozenTemplateQuestion.getId() != null) {
            throw new BadArgException("Can't save a question with that ID");
        }
        return frozenTemplateQuestionRepository.save(frozenTemplateQuestion);
    }


    @Override
    public FrozenTemplateQuestion getById(UUID id) {
        final Optional<FrozenTemplateQuestion> frozenTemplateQuestion = frozenTemplateQuestionRepository.findById(id);
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        FrozenTemplate frozenTemplate;
        if (frozenTemplateQuestion.isEmpty()) {
            throw new NotFoundException("No feedback request question with id " + id);
        }
        frozenTemplate = frozenTemplateServices.getById(frozenTemplateQuestion.get().getFrozenTemplateId());
        if (frozenTemplate == null) {
            throw new NotFoundException("Attached template for question not found");
        }

        UUID requestId = frozenTemplate.getRequestId();
        FeedbackRequest feedbackRequest;
        feedbackRequest= feedbackReqServices.getById(requestId);
        if (feedbackRequest == null) {
            throw new NotFoundException("Attached request does not exist");
        }
        if (currentUser.getId().equals(feedbackRequest.getCreatorId()) || currentUser.getId().equals(feedbackRequest.getRecipientId()) || currentUser.getId().equals(feedbackRequest.getRequesteeId()) || currentUserServices.isAdmin()) {
            return frozenTemplateQuestion.get();
        } else {
            throw new PermissionException("You are not authorized to do this operation");
        }
    }

    @Override
    public List<FrozenTemplateQuestion> findByValues(UUID frozenTemplateId) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        FrozenTemplate ft = frozenTemplateServices.getById(frozenTemplateId);
        if (ft == null) {
            throw new NotFoundException("Attached frozen template in search not found");
        }
        UUID requestId = ft.getRequestId();
        FeedbackRequest feedbackRequest;
        feedbackRequest= feedbackReqServices.getById(requestId);
        if (feedbackRequest == null) {
            throw new NotFoundException("Attached request does not exist");
        }
        UUID creatorId = feedbackRequest.getCreatorId();
        UUID recipientId = feedbackRequest.getRecipientId();
        if (currentUser.getId().equals(creatorId) || currentUser.getId().equals(recipientId) || currentUserServices.isAdmin()) {
            return new ArrayList<>(frozenTemplateQuestionRepository.findByFrozenTemplateId(Util.nullSafeUUIDToString(frozenTemplateId)));
        } else {
            throw new PermissionException("You are not authorized to do this operation");
        }
    }
}

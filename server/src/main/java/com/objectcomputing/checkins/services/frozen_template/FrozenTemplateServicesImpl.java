package com.objectcomputing.checkins.services.frozen_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FrozenTemplateServicesImpl implements FrozenTemplateServices{

    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final FrozenTemplateRepository frozenTemplateRepository;
    private final FeedbackRequestServices feedbackRequestServices;

    public FrozenTemplateServicesImpl(CurrentUserServices currentUserServices,
                                      MemberProfileServices memberProfileServices,
                                      FrozenTemplateRepository frozenTemplateRepository,
                                      FeedbackRequestServices feedbackRequestServices
                                      ) {
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.frozenTemplateRepository = frozenTemplateRepository;
        this.feedbackRequestServices = feedbackRequestServices;
    }


    @Override
    public FrozenTemplate save(FrozenTemplate ft) {
        FeedbackRequest req = feedbackRequestServices.getById(ft.getRequestId());
        UUID creatorId = req.getCreatorId();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        try {
            memberProfileServices.getById(creatorId);
        } catch (NotFoundException e) {
            throw new BadArgException("Creator ID is invalid");
        }

        if (!creatorId.equals(currentUserId)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (ft.getId() != null) {
            throw new BadArgException("Attempted to save feedback template with duplicate ID");
        }

        return frozenTemplateRepository.save(ft);

    }

    @Override
    public FrozenTemplate getById(UUID id) {
        final Optional<FrozenTemplate> template= frozenTemplateRepository.findById(id);
        if (template.isEmpty()) {
            throw new NotFoundException("No feedback template with id " + id);
        }
        FeedbackRequest req = feedbackRequestServices.getById(template.get().getRequestId());

        UUID creatorId = req.getCreatorId();
        UUID requesteeId = req.getRequesteeId();
        UUID recipientId = req.getRecipientId();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (!(creatorId.equals(currentUserId) || requesteeId.equals(currentUserId) || recipientId.equals(currentUserId) || currentUserServices.isAdmin() )) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        return template.get();

    }

    @Override
    public FrozenTemplate findByValues(UUID requestId) {
        FeedbackRequest req = feedbackRequestServices.getById(requestId);
        if (req == null) {
            throw new NotFoundException("Request does not exist");
        }
        UUID creatorId = req.getCreatorId();
        UUID requesteeId = req.getRequesteeId();
        UUID recipientId = req.getRecipientId();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (!(creatorId.equals(currentUserId) || requesteeId.equals(currentUserId) || recipientId.equals(currentUserId) || currentUserServices.isAdmin() )) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return frozenTemplateRepository.findByRequestId(Util.nullSafeUUIDToString(requestId));
    }

}

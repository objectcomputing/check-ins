package com.objectcomputing.checkins.services.frozen_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

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
                                      FeedbackRequestServices feedbackRequestServices) {
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.frozenTemplateRepository = frozenTemplateRepository;
        this.feedbackRequestServices = feedbackRequestServices;
    }


    @Override
    public FrozenTemplate save(FrozenTemplate ft) {
        FeedbackRequest req;
        try {
            memberProfileServices.getById(ft.getCreatedBy());
        } catch (NotFoundException e) {
            throw new BadArgException("Creator ID is invalid");
        }

        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (ft.getId() != null) {
            throw new BadArgException("Attempted to save feedback template with duplicate ID");
        }
        return frozenTemplateRepository.save(ft);

    }

    @Override
    public FrozenTemplate getById(UUID id) {
        final Optional<FrozenTemplate> template= frozenTemplateRepository.findById(id);
        if (!template.isPresent()) {
            throw new NotFoundException("No feedback template with id " + id);
        }
        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        return template.get();

    }

    //
}

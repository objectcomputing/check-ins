package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.feedback.FeedbackRepository;
import com.objectcomputing.checkins.services.feedback.FeedbackServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class FeedbackTemplateServicesImpl implements FeedbackTemplateServices {

    private final FeedbackTemplateRepository feedbackTemplateRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public FeedbackTemplateServicesImpl(FeedbackTemplateRepository feedbackTemplateRepository,
                                CurrentUserServices currentUserServices,
                                MemberProfileServices memberProfileServices) {
        this.feedbackTemplateRepository = feedbackTemplateRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }


    @Override
    public FeedbackTemplate save(FeedbackTemplate feedbackTemplate) {
        try {
            memberProfileServices.getById(feedbackTemplate.getId());
        } catch (NotFoundException e) {
            throw new BadArgException("Creator ID is invalid");
        }

        if (feedbackTemplate.getId() == null) {
            return feedbackTemplateRepository.save(feedbackTemplate);
        }

        if (!isPermitted()){
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.save(feedbackTemplate);

    }

    @Override
    public Feedback update(FeedbackTemplate feedbackTemplate) {
        return null;
    }

    @Override
    public Boolean delete(UUID id) {
        return null;
    }

    @Override
    public Feedback getById(UUID id) {
        return null;
    }

    @Override
    public List<FeedbackTemplate> getByValues(String title, String description) {
        return null;
    }

    public Boolean isPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (currentUserId == null) {
            return false;
        }
        return true;
    }
}

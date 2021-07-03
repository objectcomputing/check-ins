package com.objectcomputing.checkins.services.feedback_question;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FeedbackQuestionServicesImpl implements FeedbackQuestionServices {

    private final FeedbackQuestionRepository feedbackQuestionRepository;
    private final CurrentUserServices currentUserServices;
    private final FeedbackTemplateServices feedbackTemplateServices;

    public FeedbackQuestionServicesImpl(FeedbackQuestionRepository feedbackQuestionRepository,
                                        CurrentUserServices currentUserServices,
                                        MemberProfileServices memberProfileServices,
                                        FeedbackTemplateServices feedbackTemplateServices) {
        this.feedbackQuestionRepository = feedbackQuestionRepository;
        this.currentUserServices = currentUserServices;
        this.feedbackTemplateServices = feedbackTemplateServices;
    }

    @Override
    public FeedbackQuestion save(FeedbackQuestion feedbackQuestion) {
        FeedbackTemplate feedbackTemplate;
        try {
            feedbackTemplate = feedbackTemplateServices.getById(feedbackQuestion.getTemplateId());
        } catch (NotFoundException e) {
            throw new BadArgException("Template ID does not exist");
        }

        if (!createIsPermitted(feedbackTemplate.getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (feedbackQuestion.getId() != null) {
            throw new BadArgException("Attempted to save feedback question with duplicate ID");
        }

        return feedbackQuestionRepository.save(feedbackQuestion);
    }

    @Override
    public FeedbackQuestion getById(UUID id) {
        final Optional<FeedbackQuestion> feedbackQuestion = feedbackQuestionRepository.findById(id);
        if (!feedbackQuestion.isPresent()) {
            throw new NotFoundException("No feedback question with ID " + id);
        }

        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackQuestion.get();
    }

    // only the creator of the template can add questions to it
    public boolean createIsPermitted(UUID templateCreatorId) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null && (isAdmin || currentUserId.equals(templateCreatorId));
    }

    public boolean getIsPermitted() {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return isAdmin || currentUserId != null;
    }
}

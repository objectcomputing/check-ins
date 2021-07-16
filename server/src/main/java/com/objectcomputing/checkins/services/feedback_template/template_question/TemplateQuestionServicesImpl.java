package com.objectcomputing.checkins.services.feedback_template.template_question;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateRepository;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class TemplateQuestionServicesImpl implements TemplateQuestionServices {

    private final TemplateQuestionRepository templateQuestionRepository;
    private final CurrentUserServices currentUserServices;
    private final FeedbackTemplateRepository feedbackTemplateRepo;

    public TemplateQuestionServicesImpl(TemplateQuestionRepository templateQuestionRepository,
                                        CurrentUserServices currentUserServices,
                                        FeedbackTemplateRepository feedbackTemplateRepo) {
        this.templateQuestionRepository = templateQuestionRepository;
        this.currentUserServices = currentUserServices;
        this.feedbackTemplateRepo = feedbackTemplateRepo;
    }

    @Override
    public TemplateQuestion save(TemplateQuestion templateQuestion) {
        if (templateQuestion == null) {
            throw new BadArgException("Attempted to save null template question");
        } else if (templateQuestion.getTemplateId() == null) {
            throw new BadArgException("Attempted to save question with no template ID");
        }

        Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());
        if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Template ID does not exist");
        }

        if (!createIsPermitted(feedbackTemplate.get().getCreatorId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (templateQuestion.getId() != null) {
            return templateQuestionRepository.update(templateQuestion);
        }

        return templateQuestionRepository.save(templateQuestion);
    }

    @Override
    public TemplateQuestion update(TemplateQuestion templateQuestion) {
        if (templateQuestion == null) {
            throw new BadArgException("Attempted to save null template question");
        } else if (templateQuestion.getTemplateId() == null) {
            throw new BadArgException("Attempted to save question with no template ID");
        }

        Optional<FeedbackTemplate> feedbackTemplate;
        TemplateQuestion oldTemplateQuestion;

        if (templateQuestion.getId() != null) {
             oldTemplateQuestion = getById(templateQuestion.getId());
                if (oldTemplateQuestion == null) {
                    throw new NotFoundException("Question with that ID not found");
                }

        } else {
            throw new BadArgException("Question ID does not exist");
        }

            templateQuestion.setTemplateId(oldTemplateQuestion.getTemplateId());
            feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());
         if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Template ID does not exist");
         }
        else if (!createIsPermitted(feedbackTemplate.get().getCreatorId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        return templateQuestionRepository.update(templateQuestion);

    }

    @Override
    public Boolean delete(UUID id) {
        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(id);
        if (templateQuestion.isEmpty()) {
            throw new BadArgException("Could not find template question with ID " + id);
        } else if (templateQuestion.get().getTemplateId() == null) {
            throw new BadArgException("Attempted to delete question with null template ID");
        }

        Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.get().getTemplateId());
        if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Could not find feedback template with ID " + templateQuestion.get().getTemplateId());
        }

        if (!createIsPermitted(feedbackTemplate.get().getCreatorId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        FeedbackTemplate updatedTemplate = new FeedbackTemplate(
                feedbackTemplate.get().getId(),
                feedbackTemplate.get().getTitle(),
                feedbackTemplate.get().getDescription(),
                currentUserServices.getCurrentUser().getId()
        );

        // delete the question
        templateQuestionRepository.deleteById(id);

        // indicate that the template has been updated
        feedbackTemplateRepo.update(updatedTemplate);
        return true;
    }

    @Override
    public TemplateQuestion getById(UUID id) {
        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(id);
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (templateQuestion.isEmpty()) {
            throw new NotFoundException("No feedback question with ID " + id);
        }

        return templateQuestion.get();
    }


    @Override
    public List<TemplateQuestion> findByFields(UUID templateId) {
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return new ArrayList<>(templateQuestionRepository.findByTemplateId(Util.nullSafeUUIDToString(templateId)));
    }

    // only admins or the creator of the template can add questions to it
    public boolean createIsPermitted(UUID templateCreatorId) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null && (isAdmin || currentUserId.equals(templateCreatorId));
    }

    public boolean getIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }
}

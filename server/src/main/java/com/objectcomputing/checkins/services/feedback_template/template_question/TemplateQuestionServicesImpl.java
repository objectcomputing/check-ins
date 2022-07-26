package com.objectcomputing.checkins.services.feedback_template.template_question;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.Validation.validate;

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

        validate(templateQuestion != null).orElseThrow(() -> {
            throw new BadArgException("Attempted to save null template question");
        });
        validate(templateQuestion.getId() == null).orElseThrow(() -> {
            throw new BadArgException("Attempted to save question with non-auto-populated ID");
        });
        validate(templateQuestion.getTemplateId() != null).orElseThrow(() -> {
            throw new BadArgException("Attempted to save question with no template ID");
        });
        validate(templateQuestion.getInputType() != null).orElseThrow(() -> {
            throw new BadArgException("Attempted to save question with input type not specified");
        });

        Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());
        validate(feedbackTemplate.isPresent()).orElseThrow(() -> {
            throw new NotFoundException("Template ID %s does not exist", templateQuestion.getTemplateId());
        });

        // For a given template ID, each question number must be unique
        List<TemplateQuestion> conflictingQuestions = templateQuestionRepository.search(Util.nullSafeUUIDToString(templateQuestion.getTemplateId()), templateQuestion.getQuestionNumber());
        validate(conflictingQuestions.isEmpty()).orElseThrow(() -> {
            throw new BadArgException("Attempted to save question on template %s with duplicate question number %s", templateQuestion.getTemplateId(), templateQuestion.getQuestionNumber());
        });

        validate(createIsPermitted(feedbackTemplate.get().getCreatorId())).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        return templateQuestionRepository.save(templateQuestion);
    }

    @Override
    public TemplateQuestion update(TemplateQuestion templateQuestion) {
        validate(templateQuestion != null).orElseThrow(() -> {
            throw new BadArgException("Attempted to save null template question");
        });
        validate(templateQuestion.getId() != null).orElseThrow(() -> {
            throw new BadArgException("Attempted to save question with null ID");
        });
        validate(templateQuestion.getInputType() != null).orElseThrow(() -> {
            throw new BadArgException("Attempted to update question with input type not specified");
        });

        TemplateQuestion oldTemplateQuestion = getById(templateQuestion.getId());
        templateQuestion.setTemplateId(oldTemplateQuestion.getTemplateId());
        Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());

        validate(feedbackTemplate.isPresent()).orElseThrow(() -> {
            throw new NotFoundException("Could not find feedback template with ID %s", templateQuestion.getTemplateId());
        });

        // For a given template ID, each question number must be unique
        List<TemplateQuestion> conflictingQuestions = templateQuestionRepository.search(Util.nullSafeUUIDToString(templateQuestion.getTemplateId()), templateQuestion.getQuestionNumber());
        validate(conflictingQuestions.isEmpty()).orElseThrow(() -> {
            throw new BadArgException("Attempted to update question on template %s with duplicate question number %s", templateQuestion.getTemplateId(), templateQuestion.getQuestionNumber());
        });

        validate(updateIsPermitted(feedbackTemplate.get().getCreatorId())).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        return templateQuestionRepository.update(templateQuestion);
    }

    @Override
    public Boolean delete(UUID id) {
        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(id);

        validate(templateQuestion.isPresent()).orElseThrow(() -> {
            throw new NotFoundException("Could not find template question with ID %s", id);
        });
        validate(templateQuestion.get().getTemplateId() != null).orElseThrow(() -> {
            throw new BadArgException("Attempted to delete question with null template ID");
        });

        FeedbackTemplate feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.get().getTemplateId()).orElseThrow(() -> {
            throw new NotFoundException("Could not find feedback template with ID %s", templateQuestion.get().getTemplateId());
        });
        validate(deleteIsPermitted(feedbackTemplate.getCreatorId())).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        // Delete the question
        templateQuestionRepository.deleteById(id);
        return true;
    }

    @Override
    public TemplateQuestion getById(UUID id) {
        validate(getIsPermitted()).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        return templateQuestionRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No feedback question with ID %s", id);
        });
    }

    @Override
    public List<TemplateQuestion> findByFields(UUID templateId) {
        validate(templateId != null).orElseThrow(() -> {
            throw new BadArgException("Cannot find template questions for null template ID");
        });
        validate(getIsPermitted()).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        return new ArrayList<>(templateQuestionRepository.findByTemplateId(Util.nullSafeUUIDToString(templateId)));
    }

    // only admins or the creator of the template can add questions to it
    public boolean createIsPermitted(UUID templateCreatorId) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null && (isAdmin || currentUserId.equals(templateCreatorId));
    }

    public boolean updateIsPermitted(UUID templateCreatorId) {
        return createIsPermitted(templateCreatorId);
    }

    public boolean deleteIsPermitted(UUID templateCreatorId) {
        return createIsPermitted(templateCreatorId);
    }

    public boolean getIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }
}

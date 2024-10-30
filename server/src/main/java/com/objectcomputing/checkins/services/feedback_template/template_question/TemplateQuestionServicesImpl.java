package com.objectcomputing.checkins.services.feedback_template.template_question;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

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
        } else if (templateQuestion.getId() != null) {
            throw new BadArgException("Attempted to save question with non-auto-populated ID");
        } else if (templateQuestion.getTemplateId() == null) {
            throw new BadArgException("Attempted to save question with no template ID");
        } else if (templateQuestion.getInputType() == null) {
            throw new BadArgException("Attempted to save question with input type not specified");
        }

        Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());
        if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Template ID " + templateQuestion.getTemplateId() + " does not exist");
        }

        // For a given template ID, each question number must be unique
        List<TemplateQuestion> conflictingQuestions = templateQuestionRepository.search(Util.nullSafeUUIDToString(templateQuestion.getTemplateId()), templateQuestion.getQuestionNumber());
        if (!conflictingQuestions.isEmpty()) {
            throw new BadArgException("Attempted to save question on template " + templateQuestion.getTemplateId() + " with duplicate question number " + templateQuestion.getQuestionNumber());
        }

        if (!createIsPermitted(feedbackTemplate.get().getCreatorId())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return templateQuestionRepository.save(templateQuestion);
    }

    @Override
    public TemplateQuestion update(TemplateQuestion templateQuestion) {
        if (templateQuestion == null) {
            throw new BadArgException("Attempted to save null template question");
        } else if (templateQuestion.getId() == null) {
            throw new BadArgException("Attempted to save question with null ID");
        } else if (templateQuestion.getInputType() == null) {
            throw new BadArgException("Attempted to update question with input type not specified");
        }

        TemplateQuestion oldTemplateQuestion = getById(templateQuestion.getId());
        templateQuestion.setTemplateId(oldTemplateQuestion.getTemplateId());
        Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());

        if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Could not find feedback template with ID " + templateQuestion.getTemplateId());
        }

        // For a given template ID, each question number must be unique
        List<TemplateQuestion> conflictingQuestions = templateQuestionRepository.search(Util.nullSafeUUIDToString(templateQuestion.getTemplateId()), templateQuestion.getQuestionNumber());
        if (!conflictingQuestions.isEmpty()) {
            throw new BadArgException("Attempted to update question on template " + templateQuestion.getTemplateId() + " with duplicate question number " + templateQuestion.getQuestionNumber());
        }

        if (!updateIsPermitted(feedbackTemplate.get().getCreatorId())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return templateQuestionRepository.update(templateQuestion);
    }

    @Override
    public void delete(UUID id) {
        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(id);
        if (templateQuestion.isEmpty()) {
            throw new NotFoundException("Could not find template question with ID " + id);
        } else if (templateQuestion.get().getTemplateId() == null) {
            throw new BadArgException("Attempted to delete question with null template ID");
        }

        Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.get().getTemplateId());
        if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Could not find feedback template with ID " + templateQuestion.get().getTemplateId());
        } else if (!deleteIsPermitted(feedbackTemplate.get().getCreatorId())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        // Delete the question
        templateQuestionRepository.deleteById(id);
    }

    @Override
    public TemplateQuestion getById(UUID id) {
        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(id);
        if (!getIsPermitted(id)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (templateQuestion.isEmpty()) {
            throw new NotFoundException("No feedback question with ID " + id);
        }

        return templateQuestion.get();
    }

    @Override
    public List<TemplateQuestion> findByFields(UUID templateId) {
        if (templateId == null) {
            throw new BadArgException("Cannot find template questions for null template ID");
        } else if (!getIsPermitted(templateId)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

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

    public boolean getIsPermitted(UUID templateQuestionId) {
        UUID currentUserId;
        MemberProfile currentUser;

        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(templateQuestionId);
        final Optional<FeedbackTemplate> feedbackTemplate = (templateQuestion.isPresent()) ? feedbackTemplateRepo.findById(templateQuestion.get().getTemplateId()) : null;

        try {
            currentUser = currentUserServices.getCurrentUser();
            currentUserId = currentUser.getId();
        } catch (NotFoundException e) {
            currentUser = null;
            currentUserId = null;
        }

        return (currentUserId != null || (feedbackTemplate.isPresent() && feedbackTemplate.get().getIsForExternalRecipient() != null && feedbackTemplate.get().getIsForExternalRecipient() == true));
    }
}

package com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.template_questions.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.template_questions.TemplateQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class TemplateQuestionValueServicesImpl implements TemplateQuestionValueServices {
    private final CurrentUserServices currentUserServices;
    private final TemplateQuestionValueRepository templateQuestionValueRepo;

    public TemplateQuestionValueServicesImpl(CurrentUserServices currentUserServices,
                                        TemplateQuestionValueRepository templateQuestionValueRepo) {
        this.currentUserServices = currentUserServices;
        this.templateQuestionValueRepo = templateQuestionValueRepo;
    }

    @Override
    public TemplateQuestionValue save(TemplateQuestionValue templateQuestionValue) {
//        TemplateQuestion templateQuestion = templateQuestionServices.getById(templateQuestionValue.getQuestionId());
//        if (templateQuestion == null) {
//            throw new BadArgException("Attempted to save option for question that doesn't exist");
//        }
        // For a given template ID, each question number must be unique
        List<TemplateQuestionValue> conflictingQuestions = templateQuestionValueRepo.search(Util.nullSafeUUIDToString(templateQuestionValue.getQuestionId()), templateQuestionValue.getOptionNumber());
        if (!conflictingQuestions.isEmpty()) {
            throw new BadArgException("Attempted to save question on template " + templateQuestionValue.getQuestionId() + " with duplicate question number " + templateQuestionValue.getOptionNumber());
        }

//        if (!createIsPermitted(feedbackTemplateServices.getById(templateQuestion.getTemplateId()).getCreatorId())) {
//            throw new PermissionException("You are not authorized to do this operation");
//        }

        return templateQuestionValueRepo.save(templateQuestionValue);
    }

    @Override
    public TemplateQuestionValue update(TemplateQuestionValue templateQuestionValue) {
//        TemplateQuestion templateQuestion = templateQuestionServices.getById(templateQuestionValue.getQuestionId());
//        if (templateQuestion == null) {
//            throw new BadArgException("Attempted to save null template question");
//        } else if (templateQuestion.getId() == null) {
//            throw new BadArgException("Attempted to save question with null ID");
//        }
//
//        TemplateQuestion oldTemplateQuestion = getById(templateQuestion.getId());
//        templateQuestion.setTemplateId(oldTemplateQuestion.getTemplateId());
//        Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());
//
//        if (feedbackTemplate.isEmpty()) {
//            throw new NotFoundException("Could not find feedback template with ID " + templateQuestion.getTemplateId());
//        }

        // For a given template ID, each question number must be unique
        List<TemplateQuestionValue> conflictingQuestions = templateQuestionValueRepo.search(Util.nullSafeUUIDToString(templateQuestionValue.getQuestionId()), templateQuestionValue.getOptionNumber());
        if (!conflictingQuestions.isEmpty()) {
            throw new BadArgException("Attempted to update options on question " + templateQuestionValue.getQuestionId() + " with duplicate option number" + templateQuestionValue.getOptionNumber());
        }

//        if (!updateIsPermitted(feedbackTemplate.get().getCreatorId())) {
//            throw new PermissionException("You are not authorized to do this operation");
//        }

        return templateQuestionValueRepo.update(templateQuestionValue);
    }

    @Override
    public Boolean delete(UUID id) {
        final Optional<TemplateQuestionValue> templateQuestionValue = templateQuestionValueRepo.findById(id);
        if (templateQuestionValue.isEmpty()) {
            throw new NotFoundException("Could not find template question value with ID " + id);
        } else if (templateQuestionValue.get().getQuestionId() == null) {
            throw new BadArgException("Attempted to delete question with null question ID");
        }

        // Delete the question
        templateQuestionValueRepo.deleteById(id);
        return true;
    }


    @Override
    public TemplateQuestionValue getById(UUID id) {
        final Optional<TemplateQuestionValue> templateQuestionValue = templateQuestionValueRepo.findById(id);
//        if (!getIsPermitted()) {
//            throw new PermissionException("You are not authorized to do this operation");
//        }

        if (templateQuestionValue.isEmpty()) {
            throw new NotFoundException("No feedback question value with ID " + id);
        }

        return templateQuestionValue.get();
    }

    @Override
    public List<TemplateQuestionValue> findByFields(UUID questionId) {
        if (questionId == null) {
            throw new BadArgException("Cannot find template questions for null question ID");
        } else if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return new ArrayList<>(templateQuestionValueRepo.findByQuestionId(Util.nullSafeUUIDToString(questionId)));
    }

//    // only admins or the creator of the template can add questions to it
//    public boolean createIsPermitted(UUID templateCreatorId) {
//        boolean isAdmin = currentUserServices.isAdmin();
//        UUID currentUserId = currentUserServices.getCurrentUser().getId();
//        return currentUserId != null && (isAdmin || currentUserId.equals(templateCreatorId));
//    }
//
//    public boolean updateIsPermitted(UUID templateCreatorId) {
//        return createIsPermitted(templateCreatorId);
//    }
//
//    public boolean deleteIsPermitted(UUID templateCreatorId) {
//        return createIsPermitted(templateCreatorId);
//    }

    public boolean getIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }
}

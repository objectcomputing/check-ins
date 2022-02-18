package com.objectcomputing.checkins.services.feedback_template.template_question.template_questions;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateRepository;
import com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values.TemplateQuestionValue;
import com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values.TemplateQuestionValueServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class TemplateQuestionServicesImpl implements TemplateQuestionServices {

    private final TemplateQuestionRepository templateQuestionRepository;
    private final CurrentUserServices currentUserServices;
    private final FeedbackTemplateRepository feedbackTemplateRepo;
    private final TemplateQuestionValueServices templateQuestionValueServices;

    public TemplateQuestionServicesImpl(TemplateQuestionRepository templateQuestionRepository,
                                        CurrentUserServices currentUserServices,
                                        FeedbackTemplateRepository feedbackTemplateRepo,
                                        TemplateQuestionValueServices templateQuestionValueServices) {
        this.templateQuestionRepository = templateQuestionRepository;
        this.currentUserServices = currentUserServices;
        this.feedbackTemplateRepo = feedbackTemplateRepo;
        this.templateQuestionValueServices = templateQuestionValueServices;
    }

    @Override
    public Pair<TemplateQuestion, List<TemplateQuestionValue>> save(TemplateQuestion templateQuestion, List<TemplateQuestionValue> questionOptions) {
        if (templateQuestion == null) {
            throw new BadArgException("Attempted to save null template question");
        } else if (templateQuestion.getId() != null) {
            throw new BadArgException("Attempted to save question with non-auto-populated ID");
        } else if (templateQuestion.getTemplateId() == null) {
            throw new BadArgException("Attempted to save question with no template ID");
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
            throw new PermissionException("You are not authorized to do this operation");
        }

        TemplateQuestion savedQuestion = templateQuestionRepository.save(templateQuestion);
        List<TemplateQuestionValue> questionValueList=null;
        if (savedQuestion.getId() != null) {
            for (TemplateQuestionValue questionValue: questionOptions) {
                questionValue.setQuestionId(savedQuestion.getId());
                TemplateQuestionValue returnedQuestionValue = templateQuestionValueServices.save(questionValue);
                questionValueList.add(returnedQuestionValue);

            }

        }
        return new MutablePair<>(savedQuestion, questionValueList);

    }

    @Override
    public TemplateQuestion update(TemplateQuestion templateQuestion, List<TemplateQuestionValue> questionOptions) {
        if (templateQuestion == null) {
            throw new BadArgException("Attempted to save null template question");
        } else if (templateQuestion.getId() == null) {
            throw new BadArgException("Attempted to save question with null ID");
        }

        Pair<TemplateQuestion, List<TemplateQuestionValue>> oldTemplateQuestion = getById(templateQuestion.getId());
        templateQuestion.setTemplateId(oldTemplateQuestion.getLeft().getTemplateId());
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
            throw new PermissionException("You are not authorized to do this operation");
        }

        TemplateQuestion updatedQuestion =  templateQuestionRepository.update(templateQuestion);
        if (updatedQuestion != null) {
            for (TemplateQuestionValue questionValue: questionOptions) {
                questionValue.setQuestionId(updatedQuestion.getId());
                templateQuestionValueServices.update(questionValue);
            }

        }
        return updatedQuestion;
    }

    @Override
    public Boolean delete(UUID id) {
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
            throw new PermissionException("You are not authorized to do this operation");
        }

        //Delete all options/values associated with the question
        List<TemplateQuestionValue> templateQuestionValues = templateQuestionValueServices.findByFields(id);
        for (TemplateQuestionValue questionValue: templateQuestionValues) {
            templateQuestionValueServices.delete(questionValue.getId());
        }
        // Delete the question
        templateQuestionRepository.deleteById(id);

        return true;
    }

    @Override
    public Pair<TemplateQuestion, List<TemplateQuestionValue>> getById(UUID id){
        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(id);
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (templateQuestion.isEmpty()) {
            throw new NotFoundException("No feedback question with ID " + id);
        }

        TemplateQuestion receivedQuestion = templateQuestion.get();
        List<TemplateQuestionValue> templateQuestionValues = templateQuestionValueServices.findByFields(receivedQuestion.getId());
        return new MutablePair<TemplateQuestion, List<TemplateQuestionValue>>(receivedQuestion, templateQuestionValues);
    }

    @Override
    public List<Pair<TemplateQuestion, List<TemplateQuestionValue>>> findByFields(UUID templateId) {
        if (templateId == null) {
            throw new BadArgException("Cannot find template questions for null template ID");
        } else if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        List<TemplateQuestion> templateQuestions = templateQuestionRepository.findByTemplateId(Util.nullSafeUUIDToString(templateId));
        List<Pair<TemplateQuestion, List<TemplateQuestionValue>>> returnerList = null;
        for (TemplateQuestion question: templateQuestions ) {
            List<TemplateQuestionValue> templateQuestionValues = templateQuestionValueServices.findByFields(question.getId());
            Pair<TemplateQuestion,List<TemplateQuestionValue>> newPair = new MutablePair<>(question, templateQuestionValues);
            returnerList.add(newPair);
        }

        return returnerList;
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

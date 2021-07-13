package com.objectcomputing.checkins.services.template_question;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class TemplateQuestionServicesImpl implements TemplateQuestionServices {

    private final TemplateQuestionRepository templateQuestionRepository;
    private final CurrentUserServices currentUserServices;
    private final FeedbackTemplateServices feedbackTemplateServices;

    public TemplateQuestionServicesImpl(TemplateQuestionRepository templateQuestionRepository,
                                        CurrentUserServices currentUserServices,
                                        MemberProfileServices memberProfileServices,
                                        FeedbackTemplateServices feedbackTemplateServices) {
        this.templateQuestionRepository = templateQuestionRepository;
        this.currentUserServices = currentUserServices;
        this.feedbackTemplateServices = feedbackTemplateServices;
    }

    @Override
    public TemplateQuestion update(TemplateQuestion templateQuestion) {
        FeedbackTemplate feedbackTemplate;
        TemplateQuestion oldTemplateQuestion = null;

        if (templateQuestion.getId() != null) {
             oldTemplateQuestion = getById(templateQuestion.getId());
                if (oldTemplateQuestion == null) {
                    throw new NotFoundException("Question with that ID not found");
                }

        } else {
            throw new BadArgException("Question ID does not exist");
        }

        templateQuestion.setTemplateId(oldTemplateQuestion.getTemplateId());

        try {
            feedbackTemplate = feedbackTemplateServices.getById(templateQuestion.getTemplateId());
        } catch (NotFoundException e) {
            throw new NotFoundException("Template ID does not exist");
        }
        if (!createIsPermitted(feedbackTemplate.getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        return templateQuestionRepository.update(templateQuestion);

    }

    @Override
    public Boolean delete(UUID id) {
        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(id);
        if (templateQuestion.isEmpty() || templateQuestion.get() == null) {
            throw new NotFoundException("Question with that ID does not exist");
        }
        FeedbackTemplate feedbackTemplate;
        try {
            feedbackTemplate = feedbackTemplateServices.getById(templateQuestion.get().getTemplateId());
        } catch (NotFoundException e) {
            throw new NotFoundException("Template ID does not exist");
        }

        if (!createIsPermitted(feedbackTemplate.getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
      templateQuestionRepository.deleteById(id);
        return true;

    }

    @Override
    public TemplateQuestion save(TemplateQuestion templateQuestion) {
        FeedbackTemplate feedbackTemplate;
        try {
            feedbackTemplate = feedbackTemplateServices.getById(templateQuestion.getTemplateId());
        } catch (NotFoundException e) {
            throw new NotFoundException("Template ID does not exist");
        }

        if (!createIsPermitted(feedbackTemplate.getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (templateQuestion.getId() != null) {
            return templateQuestionRepository.update(templateQuestion);
        }

        return templateQuestionRepository.save(templateQuestion);
    }

    @Override
    public TemplateQuestion getById(UUID id) {
        final Optional<TemplateQuestion> templateQuestion = templateQuestionRepository.findById(id);
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (!templateQuestion.isPresent()) {
            throw new NotFoundException("No feedback question with ID " + id);
        }

        return templateQuestion.get();
    }


    @Override
    public List<TemplateQuestion> findByFields(UUID templateId) {
        List<TemplateQuestion> questionList = new ArrayList<>();
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        else {
            questionList.addAll(templateQuestionRepository.findByTemplateId(Util.nullSafeUUIDToString(templateId)));
        }
        return questionList;
    }

    // only the creator of the template can add questions to it
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

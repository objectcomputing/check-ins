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
                                        MemberProfileServices memberProfileServices,
                                        FeedbackTemplateRepository feedbackTemplateRepo) {
        this.templateQuestionRepository = templateQuestionRepository;
        this.currentUserServices = currentUserServices;
        this.feedbackTemplateRepo = feedbackTemplateRepo;
    }

    @Override
    public TemplateQuestion update(TemplateQuestion templateQuestion) {
        Optional<FeedbackTemplate> feedbackTemplate;
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
            feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());
         if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Template ID does not exist");
         }
        else if (!createIsPermitted(feedbackTemplate.get().getCreatedBy())) {
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
            Optional<FeedbackTemplate> feedbackTemplate;
            feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.get().getTemplateId());
        if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Template ID does not exist");
        }

        if (!createIsPermitted(feedbackTemplate.get().getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
      templateQuestionRepository.deleteById(id);
        return true;

    }

    @Override
    public TemplateQuestion save(TemplateQuestion templateQuestion) {
        Optional <FeedbackTemplate> feedbackTemplate;
            feedbackTemplate = feedbackTemplateRepo.findById(templateQuestion.getTemplateId());
         if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("Template ID does not exist");
        }

        if (!createIsPermitted(feedbackTemplate.get().getCreatedBy())) {
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
    public List<TemplateQuestionResponseDTO> findByFields(UUID templateId) {
        List<TemplateQuestion> questionList = new ArrayList<>();
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        else {
            questionList.addAll(templateQuestionRepository.findByTemplateId(Util.nullSafeUUIDToString(templateId)));
        }

        return questionList.stream().map(this::fromEntity).collect(Collectors.toList());
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

    private TemplateQuestionResponseDTO fromEntity(TemplateQuestion templateQuestion) {
        TemplateQuestionResponseDTO dto = new TemplateQuestionResponseDTO();
        dto.setId(templateQuestion.getId());
        dto.setQuestion(templateQuestion.getQuestion());
        dto.setTemplateId(templateQuestion.getTemplateId());
        dto.setOrderNum(templateQuestion.getOrderNum());
        return dto;
    }
}

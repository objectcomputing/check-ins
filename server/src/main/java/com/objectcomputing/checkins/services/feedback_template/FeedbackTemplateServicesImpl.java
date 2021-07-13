package com.objectcomputing.checkins.services.feedback_template;

import com.mailjet.client.resource.Template;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.feedback_template.template_question.*;
import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class FeedbackTemplateServicesImpl implements FeedbackTemplateServices {

    private final FeedbackTemplateRepository feedbackTemplateRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final TemplateQuestionRepository templateQuestionRepo;
    private final static Logger LOG = LoggerFactory.getLogger(FeedbackTemplateServices.class);

    public FeedbackTemplateServicesImpl(FeedbackTemplateRepository feedbackTemplateRepository,
                                        MemberProfileServices memberProfileServices,
                                        CurrentUserServices currentUserServices,
                                        TemplateQuestionServices templateQuestionServices,
                                        TemplateQuestionRepository templateQuestionRepo) {
        this.feedbackTemplateRepository = feedbackTemplateRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.templateQuestionServices = templateQuestionServices;
        this.templateQuestionRepo = templateQuestionRepo;
    }

    @Override
    public FeedbackTemplateResponseDTO read(UUID id) {
        FeedbackTemplate foundTemplate = feedbackTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such template found"));

        List<TemplateQuestionResponseDTO> questions = templateQuestionRepo
                .findByTemplateId(Util.nullSafeUUIDToString(id))
                .stream()
                .map(question ->
                        fromQuestionEntity(question)).collect(Collectors.toList());


        return fromEntity(foundTemplate, questions);
    }

    @Override
    public FeedbackTemplateResponseDTO save(FeedbackTemplateCreateDTO feedbackTemplate) {

        FeedbackTemplate newTemplateObject = null;
        List<TemplateQuestionResponseDTO> newTemplateQuestions = new ArrayList<>();
        if (feedbackTemplate == null ) {
            throw new BadArgException("Feedback template object is null and cannot be saved");
        }
            try {
                memberProfileServices.getById(feedbackTemplate.getCreatedBy());
            } catch (NotFoundException e) {
                throw new BadArgException("Creator ID is invalid");
            }

            if (!createIsPermitted()) {
                throw new PermissionException("You are not authorized to do this operation");
            }

            if (feedbackTemplate.getActive()) {
                newTemplateObject = feedbackTemplateRepository.save(fromDTO(feedbackTemplate));
                    if (newTemplateObject.getId() != null && feedbackTemplate.getTemplateQuestions() != null) {
                        List<TemplateQuestionCreateDTO> questions = feedbackTemplate.getTemplateQuestions();
                        for (int i = 0; i < feedbackTemplate.getTemplateQuestions().size(); ++i) {
                            TemplateQuestionCreateDTO templateQuestion = questions.get(i);
                            templateQuestion.setTemplateId(newTemplateObject.getId());
                            templateQuestion.setOrderNum(i+1);
                            newTemplateQuestions.add(fromQuestionEntity(templateQuestionRepo.save(fromQuestionDTO(templateQuestion))));
                        }
                    return fromEntity(newTemplateObject, newTemplateQuestions);
                }
                return fromEntity(newTemplateObject);
            } else {
                return fromEntity(fromDTO(feedbackTemplate));
            }
    }




    @Override
    public FeedbackTemplateResponseDTO update(FeedbackTemplateUpdateDTO feedbackTemplate) {
        Optional<FeedbackTemplate> oldFeedbackTemplate;
        FeedbackTemplateResponseDTO updated;
        if (feedbackTemplate.getId() != null) {
            oldFeedbackTemplate = feedbackTemplateRepository.findById(feedbackTemplate.getId());
            if (oldFeedbackTemplate.isEmpty()) {
                throw new NotFoundException("Template does not exist. Cannot update");
            }
            feedbackTemplate.setCreatedBy(oldFeedbackTemplate.get().getCreatedBy());
        } else {
            throw new BadArgException("Feedback template ID is null. Cannot update");
        }

        if (!updateIsPermitted(oldFeedbackTemplate.get().getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        FeedbackTemplate updatedTemplate = feedbackTemplateRepository.update(fromDTO(feedbackTemplate));
        List<TemplateQuestion> existingQuestions = templateQuestionRepo.findByTemplateId(Util.nullSafeUUIDToString(feedbackTemplate.getId()));
        List<TemplateQuestionResponseDTO> newQuestions = new ArrayList<>();

        //Add any new questions to the template
        feedbackTemplate.getTemplateQuestions().stream().forEach((newQuestion) -> {

            Optional<TemplateQuestion> first = existingQuestions.stream().filter((existing) -> existing.getId().equals(newQuestion.getId())).findFirst();
            if(!first.isPresent()) {
                LOG.info("Going to save question :{}", fromQuestionDTO(newQuestion));
                TemplateQuestion returnedQuestion = templateQuestionServices.save(fromQuestionDTO(newQuestion));
                newQuestions.add(fromQuestionEntity(templateQuestionServices.save(fromQuestionDTO(newQuestion))));
            } else {
                LOG.info("Going to update question in place :{}", fromQuestionDTO(newQuestion));
                newQuestions.add(fromQuestionEntity(templateQuestionServices.update(fromQuestionDTO(newQuestion))));
            }
        });

        //Delete any questions that have been removed
        existingQuestions.stream().forEach((existingQuestion) -> {
            if(!feedbackTemplate.getTemplateQuestions().stream().filter((updatedQuestion) -> updatedQuestion.getId().equals(existingQuestion.getId())).findFirst().isPresent()) {
                templateQuestionServices.delete(existingQuestion.getId());
            }
        });
        updated = fromEntity(updatedTemplate, newQuestions);
        return updated;
    }

    @Override
    public Boolean delete(@NotNull UUID id) {
        final Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepository.findById(id);
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (!feedbackTemplate.isPresent()) {
            throw new NotFoundException("No feedback template with id " + id);
        }

            UUID creatorId = feedbackTemplate.get().getCreatedBy();
        if (currentUserId.equals(creatorId) || currentUserServices.isAdmin() ) {
            //Delete both template and any questions attached to it
            feedbackTemplateRepository.deleteById(id);
            List <TemplateQuestion> questionsToDelete = templateQuestionServices.findByFields(id);
            for (TemplateQuestion question : questionsToDelete) {
                templateQuestionServices.delete(question.getId());
            }

            return true;
        } else {
            throw new PermissionException("You are not authorized to do this operation");
        }


    }

    @Override
    public FeedbackTemplate getById(UUID id) {
        final Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepository.findById(id);
        if (!feedbackTemplate.isPresent()) {
            throw new NotFoundException("No feedback template with id " + id);
        }

        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplate.get();
    }


    @Override
    public List<FeedbackTemplate> findByFields(@Nullable UUID createdBy, @Nullable String title, @Nullable Boolean onlyActive) {
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        List<FeedbackTemplate> templateList = new ArrayList<>();
        // Filters only active templates by default
        if (onlyActive != null && onlyActive) {
            templateList.addAll(feedbackTemplateRepository.findByActive(true));
            if (title != null) {
                templateList.retainAll(findByTitleLike(title));
            }
            if (createdBy != null) {
                templateList.retainAll(feedbackTemplateRepository.findByCreatedBy(createdBy));
            }
        } else {
            if (title != null) {
                templateList.addAll(findByTitleLike(title));
                if (createdBy != null) {
                    templateList.retainAll(feedbackTemplateRepository.findByCreatedBy(createdBy));
                }
            } else if (createdBy != null) {
                templateList.addAll(feedbackTemplateRepository.findByCreatedBy(createdBy));
            } else {
                feedbackTemplateRepository.findAll().forEach(templateList::add);
            }
        }

        return templateList;
    }

    protected List<FeedbackTemplate> findByTitleLike(String title) {
        String wildcard = "%" + title + "%";
        return feedbackTemplateRepository.findByTitleLike(wildcard);
    }

    public boolean createIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }

    public boolean updateIsPermitted(UUID createdBy) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        return isAdmin || currentUserId.equals(createdBy);
    }

    public boolean getIsPermitted() {
        return createIsPermitted();
    }

    private FeedbackTemplate fromDTO(FeedbackTemplateCreateDTO dto) {
        return new FeedbackTemplate(dto.getTitle(), dto.getDescription(), dto.getCreatedBy(), dto.getActive());
    }

    private FeedbackTemplate fromDTO(FeedbackTemplateUpdateDTO dto) {
        return new FeedbackTemplate(dto.getId(), dto.getTitle(), dto.getDescription(), dto.getCreatedBy(), dto.getActive());
    }
    private FeedbackTemplateResponseDTO fromEntity(FeedbackTemplate entity) {
        return fromEntity(entity, new ArrayList<>());
    }

    private FeedbackTemplateResponseDTO fromEntity(FeedbackTemplate feedbackTemplate, List<TemplateQuestionResponseDTO> templateQuestions) {
        if (feedbackTemplate == null) {
            return null;
        }
        FeedbackTemplateResponseDTO dto = new FeedbackTemplateResponseDTO();
        dto.setId(feedbackTemplate.getId());
        dto.setTitle(feedbackTemplate.getTitle());
        dto.setDescription(feedbackTemplate.getDescription());
        dto.setCreatedBy(feedbackTemplate.getCreatedBy());
        dto.setActive(feedbackTemplate.getActive());
        dto.setTemplateQuestions(templateQuestions);
        return dto;
    }

    private TemplateQuestionResponseDTO fromQuestionEntity(TemplateQuestion templateQuestion) {
        TemplateQuestionResponseDTO dto = new TemplateQuestionResponseDTO();
        dto.setId(templateQuestion.getId());
        dto.setQuestion(templateQuestion.getQuestion());
        dto.setTemplateId(templateQuestion.getTemplateId());
        dto.setOrderNum(templateQuestion.getOrderNum());
        return dto;
    }

    private TemplateQuestion fromQuestionDTO(TemplateQuestionCreateDTO dto) {
        return new TemplateQuestion(dto.getQuestion(), dto.getTemplateId(), dto.getOrderNum());
    }

    private TemplateQuestion fromQuestionDTO(TemplateQuestionUpdateDTO dto) {
        TemplateQuestion newQuestion = new TemplateQuestion();
        newQuestion.setId(dto.getId());
        newQuestion.setQuestion(dto.getQuestion());
        newQuestion.setOrderNum(dto.getOrderNum());
        newQuestion.setTemplateId(dto.getTemplateId());
        return newQuestion;
    }

}

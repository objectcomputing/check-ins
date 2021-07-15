package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.template_question.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Singleton
public class FeedbackTemplateServicesImpl implements FeedbackTemplateServices {

    private final FeedbackTemplateRepository feedbackTemplateRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final TemplateQuestionRepository templateQuestionRepo;

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
    public FeedbackTemplate save(FeedbackTemplateCreateDTO feedbackTemplate) {

        FeedbackTemplate newTemplateObject;
        List<TemplateQuestionResponseDTO> newTemplateQuestions = new ArrayList<>();
        //Perform initial checks that would preclude further operations
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

            //Only save feedback template and questions if it is marked as "active"--e.g. not an ad-hoc template
            if (feedbackTemplate.getActive()) {
                newTemplateObject = feedbackTemplateRepository.save(fromDTO(feedbackTemplate));
                    if (newTemplateObject.getId() != null && feedbackTemplate.getTemplateQuestions() != null) {

                        //Create list of questions and programmatically create order numbers
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

        //Perform initial checks that would preclude further operations
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

        AtomicInteger counterNum = new AtomicInteger();

        //Add any new questions to the template and update their order numbers programmatically
        if (feedbackTemplate.getTemplateQuestions() != null) {
            feedbackTemplate.getTemplateQuestions().stream().forEach((newQuestion) -> {
                counterNum.set(counterNum.get() + 1);
                int numConverter = counterNum.get();
                Optional<TemplateQuestion> first = existingQuestions.stream().filter((existing) -> existing.getId().equals(newQuestion.getId())).findFirst();
                if(first.isEmpty()) {
                    newQuestion.setOrderNum(numConverter);
                    newQuestion.setTemplateId(feedbackTemplate.getId());
                    TemplateQuestion returnedQuestion = templateQuestionServices.save(fromQuestionDTO(newQuestion));
                    newQuestions.add(fromQuestionEntity(returnedQuestion));
                } else {
                    newQuestion.setOrderNum(numConverter);
                    newQuestion.setTemplateId(feedbackTemplate.getId());
                    TemplateQuestion returnedUpdateQuestion = templateQuestionRepo.update(fromQuestionDTO(newQuestion));
                    newQuestions.add(fromQuestionEntity(returnedUpdateQuestion));

                }
            });

    //Delete any questions that were removed from the template from the separate TemplateQuestion repository
            existingQuestions.stream().forEach((existingQuestion) -> {
                boolean answer = newQuestions.stream().noneMatch((updatedQuestion) ->
                        updatedQuestion.getId().equals(existingQuestion.getId()));
                if (answer) {
                    templateQuestionServices.delete(existingQuestion.getId());
                }

            });

        }


        updated = fromEntity(updatedTemplate, newQuestions);
        return updated;
    }




    @Override
    public Boolean delete(@NotNull UUID id) {
        final FeedbackTemplate feedbackTemplate = getById(id);
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (feedbackTemplate == null ) {
            throw new NotFoundException("No feedback template with id " + id);
        }
        UUID creatorId = feedbackTemplate.getCreatedBy();
        //Delete both template and any questions attached to it
        if (currentUserId.equals(creatorId) || currentUserServices.isAdmin() ) {
            List <TemplateQuestionResponseDTO> questionsToDelete = templateQuestionServices.findByFields(id);
            for (TemplateQuestionResponseDTO question : questionsToDelete) {
                templateQuestionServices.delete(question.getId());
            }
            feedbackTemplateRepository.deleteById(id);
            return true;
        } else {
            throw new PermissionException("You are not authorized to do this operation");
        }


    }

    @Override
    public FeedbackTemplate getById(UUID id) {
        final Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepository.findById(id);
        if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("No feedback template with id " + id);
        }

        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplate.get();
    }


    @Override
    public List<FeedbackTemplateResponseDTO> findByFields(@Nullable UUID createdBy, @Nullable String title) {
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
        List<FeedbackTemplateResponseDTO> convertedResponseList = new ArrayList<>();
        templateList.stream().forEach((templateResponse) -> {
            List <TemplateQuestionResponseDTO> templateQuestions = templateQuestionServices.findByFields(templateResponse.getId());
            convertedResponseList.add(fromEntity(templateResponse, templateQuestions));
        });


        return convertedResponseList;
    }

    protected List<FeedbackTemplate> findByTitleLike(String title) {
        String wildcard = "%" + title + "%";
        return feedbackTemplateRepository.findByTitleLike(wildcard);
    }

    public boolean createIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }

    public boolean updateIsPermitted(UUID createdBy, boolean isActive) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        return isAdmin || (isActive && currentUserId.equals(createdBy));
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

    private FeedbackTemplateResponseDTO fromEntity(FeedbackTemplate entity) {
        if (entity == null) {
            return null;
        }
        FeedbackTemplateResponseDTO dto = new FeedbackTemplateResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedOn(entity.getUpdatedOn());
        dto.setActive(entity.getActive());
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

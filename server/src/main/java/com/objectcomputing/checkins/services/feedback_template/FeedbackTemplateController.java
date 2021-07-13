package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionCreateDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionResponseDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionUpdateDTO;
import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildResponseDTO;
import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/templates")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "feedback_templates")
public class FeedbackTemplateController {
    private final FeedbackTemplateServices feedbackTemplateServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FeedbackTemplateController(FeedbackTemplateServices feedbackTemplateServices,
                                      EventLoopGroup eventLoopGroup,
                                      ExecutorService executorService) {
        this.feedbackTemplateServices = feedbackTemplateServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a feedback template
     *
     * @param requestBody {@link FeedbackTemplateCreateDTO} New feedback templat4e to create
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Post()
    public Single<HttpResponse<FeedbackTemplateResponseDTO>> save(@Body @Valid @NotNull FeedbackTemplateCreateDTO requestBody) {
        return Single.fromCallable(() -> feedbackTemplateServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedbackTemplate))
                        .headers(headers -> headers.location(URI.create("/feedback_templates/" + savedFeedbackTemplate.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Update a feedback template
     *
     * @param requestBody {@link FeedbackTemplateUpdateDTO} The updated feedback template
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Put()
    public Single<HttpResponse<FeedbackTemplateResponseDTO>> update(@Body @Valid @NotNull FeedbackTemplateUpdateDTO requestBody) {
        return Single.fromCallable(() -> feedbackTemplateServices.update(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_template/" + savedFeedbackTemplate.getId())))
                        .body(fromEntity(savedFeedbackTemplate)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Delete a feedback template
     *
     * @param id {@link UUID} ID of the feedback template being deleted
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Delete("/{id}")
    public HttpResponse<?> delete(@NotNull UUID id) {
        feedbackTemplateServices.delete(id);
        return HttpResponse
                .ok();
    }

    /**
     * Get feedback template by ID
     *
     * @param id {@link UUID} ID of the requested feedback template
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FeedbackTemplateResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> feedbackTemplateServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .ok(fromEntity(feedbackTemplate))
                        .headers(headers -> headers.location(URI.create("/feedback_template/" + feedbackTemplate.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback templates by title or by the creator id, filter by active status
     *
     * @param title {@link String} Title of feedback template
     * @param createdBy {@link UUID} UUID of creator
     * @param onlyActive {@link Boolean} whether the template has been soft deleted or not
     * @return {@link List<FeedbackTemplateResponseDTO>} List of feedback templates that match the input parameters
     */
    @Get("/{?createdBy,title,onlyActive}")
    public Single<HttpResponse<List<FeedbackTemplateResponseDTO>>> findByValues(@Nullable UUID createdBy, @Nullable String title, @Nullable Boolean onlyActive) {
        return Single.fromCallable(() -> feedbackTemplateServices.findByFields(createdBy, title, onlyActive))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackTemplates -> {
                    List<FeedbackTemplateResponseDTO> dtoList = feedbackTemplates.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackTemplateResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
    }

    private FeedbackTemplate fromDTO(FeedbackTemplateCreateDTO dto) {
        return new FeedbackTemplate(dto.getTitle(), dto.getDescription(), dto.getCreatedBy(), dto.getActive());
    }

    private FeedbackTemplate fromDTO(FeedbackTemplateUpdateDTO dto) {
        return new FeedbackTemplate(dto.getId(), dto.getTitle(), dto.getDescription(), dto.getActive());
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

    private TemplateQuestionResponseDTO fromEntity(TemplateQuestion templateQuestion) {
        TemplateQuestionResponseDTO dto = new TemplateQuestionResponseDTO();
        dto.setId(templateQuestion.getId());
        dto.setQuestion(templateQuestion.getQuestion());
        dto.setTemplateId(templateQuestion.getTemplateId());
        dto.setOrderNum(templateQuestion.getOrderNum());
        return dto;
    }

    private TemplateQuestion fromDTO(TemplateQuestionCreateDTO dto) {
        return new TemplateQuestion(dto.getQuestion(), dto.getTemplateId(), dto.getOrderNum());
    }

    private TemplateQuestion fromDTO(TemplateQuestionUpdateDTO dto) {
        TemplateQuestion newQuestion = new TemplateQuestion();
        newQuestion.setId(dto.getId());
        newQuestion.setQuestion(dto.getQuestion());
        newQuestion.setOrderNum(dto.getOrderNum());
        return newQuestion;
    }
}

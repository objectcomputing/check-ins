package com.objectcomputing.checkins.services.feedback_template.template_question;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/services/feedback/template_questions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "template_questions")
public class TemplateQuestionController {

    private final TemplateQuestionServices templateQuestionServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public TemplateQuestionController(TemplateQuestionServices templateQuestionServices,
                                      EventLoopGroup eventLoopGroup,
                                      ExecutorService executorService) {
        this.templateQuestionServices = templateQuestionServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a feedback question
     *
     * @param requestBody {@link TemplateQuestionCreateDTO} The feedback question to create
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Post()
    public Single<HttpResponse<TemplateQuestionResponseDTO>> save(@Body @Valid @NotNull TemplateQuestionCreateDTO requestBody) {
        return Single.fromCallable(() -> templateQuestionServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackQuestion -> (HttpResponse<TemplateQuestionResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedbackQuestion))
                        .headers(headers -> headers.location(URI.create("/template_questions/" + savedFeedbackQuestion.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Update a feedback template question
     *
     * @param requestBody {@link TemplateQuestionUpdateDTO} The updated template question
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Put()
    public Single<HttpResponse<TemplateQuestionResponseDTO>> update(@Body @Valid @NotNull TemplateQuestionUpdateDTO requestBody) {
        return Single.fromCallable(() -> templateQuestionServices.update(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackTemplateQ -> (HttpResponse<TemplateQuestionResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/template_questions/" + savedFeedbackTemplateQ.getId())))
                        .body(fromEntity(savedFeedbackTemplateQ)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Delete a feedback template question
     *
     * @param id {@link UUID} ID of the feedback template question being deleted
     * @return {Boolean}
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteTemplateQuestion(@NotNull UUID id) {
        templateQuestionServices.delete(id);
        return HttpResponse
                .ok();
    }

    /**
     * Get feedback question by ID
     *
     * @param id The {@link UUID} of the feedback question
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<TemplateQuestionResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> templateQuestionServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackQuestion -> (HttpResponse<TemplateQuestionResponseDTO>) HttpResponse
                        .ok(fromEntity(feedbackQuestion))
                        .headers(headers -> headers.location(URI.create("/template_questions/" + feedbackQuestion.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get all feedback questions that are part of a specific template
     *
     * @param templateId The {@link UUID} of the template
     * @return list of {@link TemplateQuestionResponseDTO}
     */
    @Get("/{?templateId}")
    public Single<HttpResponse<List<TemplateQuestionResponseDTO>>> findByValues(@Nullable UUID templateId) {
        return Single.fromCallable(() -> templateQuestionServices.findByFields(templateId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(templateQuestions -> {
                    List<TemplateQuestionResponseDTO> dtoList = templateQuestions.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<TemplateQuestionResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Converts a {@link TemplateQuestionCreateDTO} into a {@link TemplateQuestion}
     * @param dto {@link TemplateQuestionCreateDTO}
     * @return {@link TemplateQuestion}
     */
    private TemplateQuestion fromDTO(TemplateQuestionCreateDTO dto) {
        return new TemplateQuestion(dto.getQuestion(), dto.getTemplateId(), dto.getQuestionNumber());
    }

    /**
     * Converts a {@link TemplateQuestionUpdateDTO} into a {@link TemplateQuestion}
     * @param dto {@link TemplateQuestionUpdateDTO}
     * @return {@link TemplateQuestion}
     */
    private TemplateQuestion fromDTO(TemplateQuestionUpdateDTO dto) {
        return new TemplateQuestion(dto.getId(), dto.getQuestion(), dto.getQuestionNumber());
    }

    /**
     * Converts a {@link TemplateQuestion} into a {@link TemplateQuestionResponseDTO}
     * @param templateQuestion {@link TemplateQuestion}
     * @return {@link TemplateQuestionResponseDTO}
     */
    private TemplateQuestionResponseDTO fromEntity(TemplateQuestion templateQuestion) {
        TemplateQuestionResponseDTO dto = new TemplateQuestionResponseDTO();
        dto.setId(templateQuestion.getId());
        dto.setQuestion(templateQuestion.getQuestion());
        dto.setTemplateId(templateQuestion.getTemplateId());
        dto.setQuestionNumber(templateQuestion.getQuestionNumber());
        return dto;
    }

}

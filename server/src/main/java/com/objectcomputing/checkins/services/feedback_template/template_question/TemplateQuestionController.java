package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/feedback/template_questions")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "template_questions")
@Validated
public class TemplateQuestionController {

    private final TemplateQuestionServices templateQuestionServices;

    public TemplateQuestionController(TemplateQuestionServices templateQuestionServices) {
        this.templateQuestionServices = templateQuestionServices;
    }

    /**
     * Create a feedback question
     *
     * @param requestBody {@link TemplateQuestionCreateDTO} The feedback question to create
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Post()
    public Mono<HttpResponse<TemplateQuestionResponseDTO>> save(@Body @Valid @NotNull TemplateQuestionCreateDTO requestBody) {
        return Mono.fromCallable(() -> templateQuestionServices.save(fromDTO(requestBody)))
                .map(savedFeedbackQuestion -> HttpResponse.created(fromEntity(savedFeedbackQuestion))
                        .headers(headers -> headers.location(URI.create("/template_questions/" + savedFeedbackQuestion.getId()))));
    }

    /**
     * Update a feedback template question
     *
     * @param requestBody {@link TemplateQuestionUpdateDTO} The updated template question
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Put()
    public Mono<HttpResponse<TemplateQuestionResponseDTO>> update(@Body @Valid @NotNull TemplateQuestionUpdateDTO requestBody) {
        return Mono.fromCallable(() -> templateQuestionServices.update(fromDTO(requestBody)))
                .map(savedFeedbackTemplateQ -> HttpResponse.ok(fromEntity(savedFeedbackTemplateQ))
                        .headers(headers -> headers.location(URI.create("/template_questions/" + savedFeedbackTemplateQ.getId()))));
    }

    /**
     * Delete a feedback template question
     *
     * @param id {@link UUID} ID of the feedback template question being deleted
     * @return {Boolean}
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteTemplateQuestion(@NotNull UUID id) {
        return Mono.fromRunnable(() -> templateQuestionServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Get feedback question by ID
     *
     * @param id The {@link UUID} of the feedback question
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Get("/{id}")
    public Mono<HttpResponse<TemplateQuestionResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> templateQuestionServices.getById(id))
                .map(feedbackQuestion -> HttpResponse.ok(fromEntity(feedbackQuestion))
                        .headers(headers -> headers.location(URI.create("/template_questions/" + feedbackQuestion.getId()))));
    }

    /**
     * Get all feedback questions that are part of a specific template
     *
     * @param templateId The {@link UUID} of the template
     * @return list of {@link TemplateQuestionResponseDTO}
     */
    @Get("/{?templateId}")
    public Mono<HttpResponse<List<TemplateQuestionResponseDTO>>> findByValues(@Nullable UUID templateId) {
        return Mono.fromCallable(() -> templateQuestionServices.findByFields(templateId))
                .map(entities -> entities.stream().map(this::fromEntity).collect(Collectors.toList()))
                .map(HttpResponse::ok);
    }

    /**
     * Converts a {@link TemplateQuestionCreateDTO} into a {@link TemplateQuestion}
     * @param dto {@link TemplateQuestionCreateDTO}
     * @return {@link TemplateQuestion}
     */
    private TemplateQuestion fromDTO(TemplateQuestionCreateDTO dto) {
        return new TemplateQuestion(dto.getQuestion(), dto.getTemplateId(), dto.getQuestionNumber(), dto.getInputType());
    }

    /**
     * Converts a {@link TemplateQuestionUpdateDTO} into a {@link TemplateQuestion}
     * @param dto {@link TemplateQuestionUpdateDTO}
     * @return {@link TemplateQuestion}
     */
    private TemplateQuestion fromDTO(TemplateQuestionUpdateDTO dto) {
        return new TemplateQuestion(dto.getId(), dto.getQuestion(), dto.getQuestionNumber(), dto.getInputType());
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
        dto.setInputType(templateQuestion.getInputType());
        return dto;
    }

}

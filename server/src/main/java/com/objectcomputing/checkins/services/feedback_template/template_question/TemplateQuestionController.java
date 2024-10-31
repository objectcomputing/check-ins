package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/services/feedback/template_questions")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
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
    @Post
    public HttpResponse<TemplateQuestionResponseDTO> save(@Body @Valid @NotNull TemplateQuestionCreateDTO requestBody) {
        TemplateQuestion savedFeedbackQuestion = templateQuestionServices.save(fromDTO(requestBody));
        return HttpResponse.created(fromEntity(savedFeedbackQuestion))
                .headers(headers -> headers.location(URI.create("/template_questions/" + savedFeedbackQuestion.getId())));
    }

    /**
     * Update a feedback template question
     *
     * @param requestBody {@link TemplateQuestionUpdateDTO} The updated template question
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Put
    public HttpResponse<TemplateQuestionResponseDTO> update(@Body @Valid @NotNull TemplateQuestionUpdateDTO requestBody) {
        TemplateQuestion savedFeedbackTemplateQ = templateQuestionServices.update(fromDTO(requestBody));
        return HttpResponse.ok(fromEntity(savedFeedbackTemplateQ))
                .headers(headers -> headers.location(URI.create("/template_questions/" + savedFeedbackTemplateQ.getId())));
    }

    /**
     * Delete a feedback template question
     *
     * @param id {@link UUID} ID of the feedback template question being deleted
     * @return {Boolean}
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteTemplateQuestion(@NotNull UUID id) {
        templateQuestionServices.delete(id);
    }

    /**
     * Get feedback question by ID
     *
     * @param id The {@link UUID} of the feedback question
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Get("/{id}")
    public HttpResponse<TemplateQuestionResponseDTO> getById(UUID id) {
        TemplateQuestion feedbackQuestion = templateQuestionServices.getById(id);
        return feedbackQuestion == null ? HttpResponse.notFound() : HttpResponse.ok(fromEntity(feedbackQuestion))
                .headers(headers -> headers.location(URI.create("/template_questions/" + feedbackQuestion.getId())));
    }

    /**
     * Get all feedback questions that are part of a specific template
     *
     * @param templateId The {@link UUID} of the template
     * @return list of {@link TemplateQuestionResponseDTO}
     */
    @Get("/{?templateId}")
    public List<TemplateQuestionResponseDTO> findByValues(@Nullable UUID templateId) {
        return templateQuestionServices.findByFields(templateId)
                .stream()
                .map(this::fromEntity).toList();
    }

    /**
     * Converts a {@link TemplateQuestionCreateDTO} into a {@link TemplateQuestion}
     *
     * @param dto {@link TemplateQuestionCreateDTO}
     * @return {@link TemplateQuestion}
     */
    private TemplateQuestion fromDTO(TemplateQuestionCreateDTO dto) {
        return new TemplateQuestion(dto.getQuestion(), dto.getTemplateId(), dto.getQuestionNumber(), dto.getInputType());
    }

    /**
     * Converts a {@link TemplateQuestionUpdateDTO} into a {@link TemplateQuestion}
     *
     * @param dto {@link TemplateQuestionUpdateDTO}
     * @return {@link TemplateQuestion}
     */
    private TemplateQuestion fromDTO(TemplateQuestionUpdateDTO dto) {
        return new TemplateQuestion(dto.getId(), dto.getQuestion(), dto.getQuestionNumber(), dto.getInputType());
    }

    /**
     * Converts a {@link TemplateQuestion} into a {@link TemplateQuestionResponseDTO}
     *
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

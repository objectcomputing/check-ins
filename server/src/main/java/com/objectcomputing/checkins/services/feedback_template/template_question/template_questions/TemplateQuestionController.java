package com.objectcomputing.checkins.services.feedback_template.template_question.template_questions;

import com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values.TemplateQuestionValue;
import com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values.TemplateQuestionValueCreateDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values.TemplateQuestionValueResponseDTO;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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
    public Single<HttpResponse<TemplateQuestionResponseDTO>> save(@Body @Valid @NotNull Pair<TemplateQuestionCreateDTO, List<TemplateQuestionValueCreateDTO>> requestBody) {
        return Single.fromCallable(() -> templateQuestionServices.save(fromDTOCreate(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackQuestion -> (HttpResponse<Pair<TemplateQuestionResponseDTO, List<TemplateQuestionValueResponseDTO>>>) HttpResponse
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

    private Pair<TemplateQuestion, List<TemplateQuestionValue>> fromDTOCreate(Pair<TemplateQuestionCreateDTO, List<TemplateQuestionValueCreateDTO>> dto) {
        TemplateQuestion question = fromDTO(dto.getKey());
        List<TemplateQuestionValue> returnedList = null;
        for (TemplateQuestionValueCreateDTO questionValue: dto.getValue()) {
            TemplateQuestionValue newQuestionValue = new TemplateQuestionValue(questionValue.getOptionText(), questionValue.getOptionNumber());
            returnedList.add(newQuestionValue);

        }
        return new MutablePair<TemplateQuestion, List<TemplateQuestionValue>>(question, returnedList);

    }

    private Pair<TemplateQuestion, List<TemplateQuestionValue>> fromDTOResponse(Pair<TemplateQuestionResponseDTO, List<TemplateQuestionValueResponseDTO>> dto) {
        TemplateQuestion question = fromDTO(dto.getKey());
        List<TemplateQuestionValue> returnedList = null;
        for (TemplateQuestionValueResponseDTO questionValue: dto.getValue()) {
            TemplateQuestionValue newQuestionValue = new TemplateQuestionValue(questionValue.getId(), questionValue.getOptionText(),questionValue.getQuestionId(), questionValue.getOptionNumber());
            returnedList.add(newQuestionValue);

        }
        return new MutablePair<TemplateQuestion, List<TemplateQuestionValue>>(question, returnedList);

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
     * Converts a {@link TemplateQuestionResponseDTO} into a {@link TemplateQuestion}
     * @param dto {@link TemplateQuestionResponseDTO}
     * @return {@link TemplateQuestion}
     */
    private TemplateQuestion fromDTO(TemplateQuestionResponseDTO dto) {
        return new TemplateQuestion(dto.getId(),dto.getQuestion(), dto.getTemplateId(), dto.getQuestionNumber());
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

    private Pair<TemplateQuestionResponseDTO, List<TemplateQuestionValueResponseDTO>> fromEntity(Pair<TemplateQuestion, List<TemplateQuestionValue>> values) {
        TemplateQuestionResponseDTO dto = fromEntity(values.getKey());
        for (TemplateQuestionValueResponseDTO questionValue: dto.getValue()) {
            TemplateQuestionValue newQuestionValue = new TemplateQuestionValue(questionValue.getId(), questionValue.getOptionText(),questionValue.getQuestionId(), questionValue.getOptionNumber());
            returnedList.add(newQuestionValue);

        }
        return new MutablePair<TemplateQuestion, List<TemplateQuestionValue>>(question, returnedList);
    }

}

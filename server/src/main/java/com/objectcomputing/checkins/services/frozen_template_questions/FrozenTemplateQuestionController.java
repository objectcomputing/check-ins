package com.objectcomputing.checkins.services.frozen_template_questions;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


@Controller("/services/feedback/frozen_template_questions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "frozen template question")
public class FrozenTemplateQuestionController {
    private final FrozenTemplateQuestionServices frozenTemplateQServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FrozenTemplateQuestionController(FrozenTemplateQuestionServices frozenTemplateQServices,
                                            EventLoopGroup eventLoopGroup,
                                            @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.frozenTemplateQServices = frozenTemplateQServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }


    /**
     * Attaches a question to a feedback request
     *
     * @param requestBody {@link FrozenTemplateQuestionCreateDTO} New feedback request to create
     * @return {@link FrozenTemplateQuestionResponseDTO}
     */
    @Post()
    public Single<HttpResponse<FrozenTemplateQuestionResponseDTO>> save(@Body @Valid @NotNull FrozenTemplateQuestionCreateDTO requestBody) {
        return Single.fromCallable(() -> frozenTemplateQServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedQuestion -> (HttpResponse<FrozenTemplateQuestionResponseDTO>) HttpResponse
                        .created(fromEntity(savedQuestion))
                        .headers(headers -> headers.location(URI.create("/frozen_template_questions/" + savedQuestion.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback request question/answer pair by ID
     *
     * @param id {@link UUID} ID of the request
     * @return {@link FrozenTemplateQuestionResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FrozenTemplateQuestionResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> frozenTemplateQServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackQ -> (HttpResponse<FrozenTemplateQuestionResponseDTO>) HttpResponse
                        .ok(fromEntity(savedFeedbackQ))
                        .headers(headers -> headers.location(URI.create("/frozen_template_questions/" + savedFeedbackQ.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback request question and answer pair by request id
     *
     * @param templateId {@link UUID} ID of frozen template
     * @return {@link List <FeedbackRequestQuestionResponseDTO>} List of feedback requests question/answers that are attached to a certain feedback request
     */
    @Get("/{?templateId}")
    public Single<HttpResponse<List<FrozenTemplateQuestionResponseDTO>>> findByValues(@Nullable UUID templateId) {
        return Single.fromCallable(() -> frozenTemplateQServices.findByValues(templateId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackReqQs -> {
                    List<FrozenTemplateQuestionResponseDTO> dtoList = feedbackReqQs.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FrozenTemplateQuestionResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
    }


    private FrozenTemplateQuestionResponseDTO fromEntity(FrozenTemplateQuestion savedQuestion) {
        FrozenTemplateQuestionResponseDTO dto = new FrozenTemplateQuestionResponseDTO();
        dto.setId(savedQuestion.getId());
        dto.setFrozenTemplateId(savedQuestion.getFrozenTemplateId());
        dto.setQuestionContent(savedQuestion.getQuestionContent());
        dto.setQuestionNumber(savedQuestion.getQuestionNumber());
        return dto;
    }

    private FrozenTemplateQuestion fromDTO(FrozenTemplateQuestionCreateDTO requestBody) {
        return new FrozenTemplateQuestion(requestBody.getFrozenTemplateId(),
                requestBody.getQuestionContent(),
                requestBody.getQuestionNumber());
    }

}

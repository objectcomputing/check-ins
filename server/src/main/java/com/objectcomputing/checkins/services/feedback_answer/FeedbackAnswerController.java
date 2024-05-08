package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/feedback/answers")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class FeedbackAnswerController {

    private final FeedbackAnswerServices feedbackAnswerServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FeedbackAnswerController(FeedbackAnswerServices feedbackAnswerServices,
                                    EventLoopGroup eventLoopGroup,
                                    @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.feedbackAnswerServices = feedbackAnswerServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a feedback answer
     *
     * @param requestBody {@link FeedbackAnswerCreateDTO} New feedback answer to create
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Post()
    public Mono<HttpResponse<FeedbackAnswerResponseDTO>> save(@Body @Valid @NotNull FeedbackAnswerCreateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackAnswerServices.save(fromDTO(requestBody)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedAnswer -> (HttpResponse<FeedbackAnswerResponseDTO>) HttpResponse
                        .created(fromEntity(savedAnswer))
                        .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId()))))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Update a feedback answer
     *
     * @param requestBody {@link FeedbackAnswerUpdateDTO} The updated feedback answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Put()
    public Mono<HttpResponse<FeedbackAnswerResponseDTO>> update(@Body @Valid @NotNull FeedbackAnswerUpdateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackAnswerServices.update(fromDTO(requestBody)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedAnswer -> (HttpResponse<FeedbackAnswerResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId())))
                        .body(fromEntity(savedAnswer)))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Get a feedback answer by ID
     *
     * @param id {@link UUID} ID of the feedback answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @RequiredPermission(Permission.CAN_VIEW_FEEDBACK_ANSWER)
    @Get("/{id}")
    public Mono<HttpResponse<FeedbackAnswerResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> feedbackAnswerServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedAnswer -> (HttpResponse<FeedbackAnswerResponseDTO>) HttpResponse
                        .ok(fromEntity(savedAnswer))
                        .headers(headers -> headers.location(URI.create("/feedback_answer/" +  savedAnswer.getId()))))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Search for all feedback requests that match the intersection of the provided values
     * Any values that are null are not applied to the intersection
     *
     * @param questionId The attached {@link UUID} of the related question
     * @param requestId The attached {@link UUID} of the request that corresponds with the answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @RequiredPermission(Permission.CAN_VIEW_FEEDBACK_ANSWER)
    @Get("/{?questionId,requestId}")
    public Mono<HttpResponse<List<FeedbackAnswerResponseDTO>>> findByValues(@Nullable UUID questionId, @Nullable UUID requestId) {
        return Mono.fromCallable(() -> feedbackAnswerServices.findByValues(questionId, requestId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(feedbackAnswers -> {
                    List<FeedbackAnswerResponseDTO> dtoList = feedbackAnswers.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackAnswerResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.fromExecutor(executorService));
    }

    private FeedbackAnswer fromDTO(FeedbackAnswerCreateDTO dto) {
        return new FeedbackAnswer(dto.getAnswer(), dto.getQuestionId(), dto.getRequestId(), dto.getSentiment());
    }

    private FeedbackAnswer fromDTO(FeedbackAnswerUpdateDTO dto) {
        return new FeedbackAnswer(dto.getId(), dto.getAnswer(), dto.getSentiment());
    }

    private FeedbackAnswerResponseDTO fromEntity(FeedbackAnswer feedbackAnswer) {
        FeedbackAnswerResponseDTO dto = new FeedbackAnswerResponseDTO();
        dto.setId(feedbackAnswer.getId());
        dto.setAnswer(feedbackAnswer.getAnswer());
        dto.setQuestionId(feedbackAnswer.getQuestionId());
        dto.setRequestId(feedbackAnswer.getRequestId());
        dto.setSentiment(feedbackAnswer.getSentiment());
        return dto;
    }
}

package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

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
    public Single<HttpResponse<FeedbackAnswerResponseDTO>> save(@Body @Valid @NotNull FeedbackAnswerCreateDTO requestBody) {
        return Single.fromCallable(() -> feedbackAnswerServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedAnswer -> (HttpResponse<FeedbackAnswerResponseDTO>) HttpResponse
                        .created(fromEntity(savedAnswer))
                        .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Update a feedback answer
     *
     * @param requestBody {@link FeedbackAnswerUpdateDTO} The updated feedback answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Put()
    public Single<HttpResponse<FeedbackAnswerResponseDTO>> update(@Body @Valid @NotNull FeedbackAnswerUpdateDTO requestBody) {
        return Single.fromCallable(() -> feedbackAnswerServices.update(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedAnswer -> (HttpResponse<FeedbackAnswerResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId())))
                        .body(fromEntity(savedAnswer)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get a feedback answer by ID
     *
     * @param id {@link UUID} ID of the feedback answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FeedbackAnswerResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> feedbackAnswerServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedAnswer -> (HttpResponse<FeedbackAnswerResponseDTO>) HttpResponse
                        .ok(fromEntity(savedAnswer))
                        .headers(headers -> headers.location(URI.create("/feedback_answer/" +  savedAnswer.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    private FeedbackAnswer fromDTO(FeedbackAnswerCreateDTO dto) {
        return new FeedbackAnswer(dto.getAnswer(), dto.getQuestionId(), dto.getSentiment());
    }

    private FeedbackAnswer fromDTO(FeedbackAnswerUpdateDTO dto) {
        return new FeedbackAnswer(dto.getId(), dto.getAnswer(), dto.getSentiment());
    }

    private FeedbackAnswerResponseDTO fromEntity(FeedbackAnswer feedbackAnswer) {
        FeedbackAnswerResponseDTO dto = new FeedbackAnswerResponseDTO();
        dto.setId(feedbackAnswer.getId());
        dto.setAnswer(feedbackAnswer.getAnswer());
        dto.setQuestionId(feedbackAnswer.getQuestionId());
        dto.setSentiment(feedbackAnswer.getSentiment());
        return dto;
    }
}

package com.objectcomputing.checkins.services.feedback_request_questions;
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


@Controller("/services/feedback/request_questions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "feedback request question")
public class FeedbackRequestQuestionController {
    private final FeedbackRequestQuestionServices feedbackReqQServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FeedbackRequestQuestionController(FeedbackRequestQuestionServices feedbackReqQServices,
                                     EventLoopGroup eventLoopGroup,
                                     @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.feedbackReqQServices = feedbackReqQServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }


    /**
     * Attaches a question to a feedback request
     *
     * @param requestBody {@link FeedbackRequestQuestionCreateDTO} New feedback request to create
     * @return {@link FeedbackRequestQuestionResponseDTO}
     */
    @Post()
    public Single<HttpResponse<FeedbackRequestQuestionResponseDTO>> save(@Body @Valid @NotNull FeedbackRequestQuestionCreateDTO requestBody) {
        return Single.fromCallable(() -> feedbackReqQServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedQuestion -> (HttpResponse<FeedbackRequestQuestionResponseDTO>) HttpResponse
                        .created(fromEntity(savedQuestion))
                        .headers(headers -> headers.location(URI.create("/feedback_request_questions/" + savedQuestion.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Update a feedback request question with an answer
     *
     * @param requestBody {@link FeedbackRequestQuestionUpdateDTO} The updated feedback request
     * @return {@link FeedbackRequestQuestionResponseDTO}
     */
    @Put()
    public Single<HttpResponse<FeedbackRequestQuestionResponseDTO>> update(@Body @Valid @NotNull FeedbackRequestQuestionUpdateDTO requestBody) {
        return Single.fromCallable(() -> feedbackReqQServices.update(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackQ -> (HttpResponse<FeedbackRequestQuestionResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_request_questions/" + savedFeedbackQ.getId())))
                        .body(fromEntity(savedFeedbackQ)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Delete a feedback question and possibly answer pair by UUID--admin only
     *
     * @param id {@link UUID} of the feedback request question/answer pair to be deleted
     * @return {@link HttpResponse}
     */
    @Delete("/{id}")
    public Single<HttpResponse> delete(@NotNull UUID id) {
        return Single.fromCallable(() -> feedbackReqQServices.delete(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(successFlag -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback request question/answer pair by ID
     *
     * @param id {@link UUID} ID of the request
     * @return {@link FeedbackRequestQuestionResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FeedbackRequestQuestionResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> feedbackReqQServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackQ -> (HttpResponse<FeedbackRequestQuestionResponseDTO>) HttpResponse
                        .ok(fromEntity(savedFeedbackQ))
                        .headers(headers -> headers.location(URI.create("/feedback_request_questions" + savedFeedbackQ.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback request question and answer pair by request id
     *
     * @param requestId {@link UUID} ID of feedback request
     * @return {@link List <FeedbackRequestQuestionResponseDTO>} List of feedback requests question/answers that are attached to a certain feedback request
     */
    @Get("/{?requestId}")
    public Single<HttpResponse<List<FeedbackRequestQuestionResponseDTO>>> findByValues(@Nullable UUID requestId) {
        return Single.fromCallable(() -> feedbackReqQServices.findByValues(requestId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackReqQs -> {
                    List<FeedbackRequestQuestionResponseDTO> dtoList = feedbackReqQs.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackRequestQuestionResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
    }


    private FeedbackRequestQuestionResponseDTO fromEntity(FeedbackRequestQuestion savedQuestion) {
        FeedbackRequestQuestionResponseDTO dto = new FeedbackRequestQuestionResponseDTO();
        dto.setId(savedQuestion.getId());
        dto.setRequestId(savedQuestion.getRequestId());
        dto.setQuestionContent(savedQuestion.getQuestionContent());
        dto.setAnswerContent(savedQuestion.getAnswerContent());
        dto.setOrderNum(savedQuestion.getOrderNum());
        return dto;
    }

    private FeedbackRequestQuestion fromDTO(FeedbackRequestQuestionCreateDTO requestBody) {
        return new FeedbackRequestQuestion(requestBody.getRequestId(), requestBody.getQuestionContent(), requestBody.getOrderNum());
    }

    private FeedbackRequestQuestion fromDTO(FeedbackRequestQuestionResponseDTO requestBody) {
        return new FeedbackRequestQuestion(requestBody.getId(), requestBody.getRequestId(), requestBody.getQuestionContent(), requestBody.getAnswerContent(), requestBody.getOrderNum());
    }
    private FeedbackRequestQuestion fromDTO(FeedbackRequestQuestionUpdateDTO requestBody) {
        FeedbackRequestQuestion question = new FeedbackRequestQuestion();
        question.setId(requestBody.getId());
        question.setAnswerContent(requestBody.getAnswerContent());
        return question;

    }


}

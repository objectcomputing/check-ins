package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/services/feedback/answers")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class FeedbackAnswerController {

    private final FeedbackAnswerServices feedbackAnswerServices;

    public FeedbackAnswerController(FeedbackAnswerServices feedbackAnswerServices) {
        this.feedbackAnswerServices = feedbackAnswerServices;
    }

    /**
     * Create a feedback answer
     *
     * @param requestBody {@link FeedbackAnswerCreateDTO} New feedback answer to create
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Post
    public HttpResponse<FeedbackAnswerResponseDTO> save(@Body @Valid @NotNull FeedbackAnswerCreateDTO requestBody) {
        FeedbackAnswer savedAnswer = feedbackAnswerServices.save(fromDTO(requestBody));
        return HttpResponse.created(fromEntity(savedAnswer))
                .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId())));
    }

    /**
     * Update a feedback answer
     *
     * @param requestBody {@link FeedbackAnswerUpdateDTO} The updated feedback answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Put
    public HttpResponse<FeedbackAnswerResponseDTO> update(@Body @Valid @NotNull FeedbackAnswerUpdateDTO requestBody) {
        FeedbackAnswer savedAnswer = feedbackAnswerServices.update(fromDTO(requestBody));
        return HttpResponse.ok(fromEntity(savedAnswer))
                .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId())));
    }

    /**
     * Get a feedback answer by ID
     *
     * @param id {@link UUID} ID of the feedback answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Get("/{id}")
    public HttpResponse<FeedbackAnswerResponseDTO> getById(UUID id) {
        FeedbackAnswer savedAnswer = feedbackAnswerServices.getById(id);
        return HttpResponse.ok(fromEntity(savedAnswer))
                .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId())));
    }

    /**
     * Search for all feedback requests that match the intersection of the provided values
     * Any values that are null are not applied to the intersection
     *
     * @param questionId The attached {@link UUID} of the related question
     * @param requestId  The attached {@link UUID} of the request that corresponds with the answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Get("/{?questionId,requestId}")
    public List<FeedbackAnswerResponseDTO> findByValues(@Nullable UUID questionId, @Nullable UUID requestId) {
        return feedbackAnswerServices.findByValues(questionId, requestId)
                .stream()
                .map(this::fromEntity)
                .toList();
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

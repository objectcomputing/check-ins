package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/services/feedback/answers/external/recipients")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
public class FeedbackAnswerExternalRecipientController {

    private final FeedbackAnswerServices feedbackAnswerServices;
    private final FeedbackRequestServices feedbackRequestServices;

    public FeedbackAnswerExternalRecipientController(FeedbackAnswerServices feedbackAnswerServices, FeedbackRequestServices feedbackRequestServices) {
        this.feedbackAnswerServices = feedbackAnswerServices;
        this.feedbackRequestServices = feedbackRequestServices;
    }

    /**
     * Create a feedback answer
     *
     * @param requestBody {@link FeedbackAnswerCreateDTO} New feedback answer to create
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Post
    public HttpResponse<FeedbackAnswerResponseDTO> save(@Body @Valid @NotNull FeedbackAnswerCreateDTO requestBody) {
        FeedbackRequest feedbackRequest = this.feedbackRequestServices.getById(requestBody.getRequestId());
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }
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
        FeedbackAnswer feedbackAnswerExistingRecord = this.feedbackAnswerServices.getById(requestBody.getId());
        FeedbackRequest feedbackRequest = this.feedbackRequestServices.getById(feedbackAnswerExistingRecord.getRequestId());
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }
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
        FeedbackRequest feedbackRequest = this.feedbackRequestServices.getById(savedAnswer.getRequestId());
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }
        return HttpResponse.ok(fromEntity(savedAnswer))
                .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId())));
    }

    /**
     * Search for all feedback requests that match the intersection of the provided values
     * Any values that are null are not applied to the intersection
     *
     * @param questionId The attached {@link UUID} of the related question
     * @param requestId  The attached {@link UUID} of the request that corresponds with the answer
     * @param externalRecipientId  The attached {@link UUID} of the external-recipient that corresponds with the answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Get("/{?questionId,requestId,externalRecipientId}")
    public List<FeedbackAnswerResponseDTO> findByValues(@Nullable UUID questionId, @Nullable UUID requestId, @Nullable UUID externalRecipientId) {
        return feedbackAnswerServices.findByValues(questionId, requestId, externalRecipientId)
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

package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@Controller("/services/feedback/requests")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "feedback request")
public class FeedbackRequestController {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestController.class);

    private final FeedbackRequestServices feedbackReqServices;

    public FeedbackRequestController(FeedbackRequestServices feedbackReqServices) {
        this.feedbackReqServices = feedbackReqServices;
    }

    /**
     * Create a feedback request
     *
     * @param requestBody {@link FeedbackRequestCreateDTO} New feedback request to create
     * @return {@link FeedbackRequestResponseDTO}
     */
    @RequiredPermission(Permission.CAN_CREATE_FEEDBACK_REQUEST)
    @Post
    public Mono<HttpResponse<FeedbackRequestResponseDTO>> save(@Body @Valid @NotNull FeedbackRequestCreateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackReqServices.save(fromDTO(requestBody)))
                .map(savedFeedbackRequest -> HttpResponse.created(fromEntity(savedFeedbackRequest))
                        .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedbackRequest.getId()))));
    }

    /**
     * Update a feedback request
     *
     * @param requestBody {@link FeedbackRequestUpdateDTO} The updated feedback request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Put
    public Mono<HttpResponse<FeedbackRequestResponseDTO>> update(@Body @Valid @NotNull FeedbackRequestUpdateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackReqServices.update(requestBody))
                .map(savedFeedback -> HttpResponse.ok(fromEntity(savedFeedback))
                        .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedback.getId()))));
    }

    /**
     * Delete a feedback request by UUID
     *
     * @param id {@link UUID} of the feedback request to be deleted
     * @return {@link HttpResponse}
     */
    @RequiredPermission(Permission.CAN_DELETE_FEEDBACK_REQUEST)
    @Delete("/{id}")
    public Mono<HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromRunnable(() -> feedbackReqServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Get feedback requst by ID
     *
     * @param id {@link UUID} ID of the request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @RequiredPermission(Permission.CAN_VIEW_FEEDBACK_REQUEST)
    @Get("/{id}")
    public Mono<HttpResponse<FeedbackRequestResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> feedbackReqServices.getById(id))
                .map(savedFeedbackRequest -> HttpResponse.ok(fromEntity(savedFeedbackRequest))
                        .headers(headers -> headers.location(URI.create("/feedback_request" + savedFeedbackRequest.getId()))));
    }

    /**
     * Search for all feedback requests that match the intersection of the provided values
     * Any values that are null are not applied to the intersection
     *
     * @param creatorId The {@link UUID} of the creator of the request
     * @param requesteeId The {@link UUID} of the requestee
     * @param recipientId The {@link UUID} of the recipient
     * @param oldestDate The date that filters out any requests that were made before that date
     * @return list of {@link FeedbackRequestResponseDTO}
     */
    @RequiredPermission(Permission.CAN_VIEW_FEEDBACK_REQUEST)
    @Get("/{?creatorId,requesteeId,recipientId,oldestDate,reviewPeriodId,templateId,requesteeIds}")
    public Mono<HttpResponse<List<FeedbackRequestResponseDTO>>> findByValues(@Nullable UUID creatorId, @Nullable UUID requesteeId, @Nullable UUID recipientId, @Nullable @Format("yyyy-MM-dd") LocalDate oldestDate, @Nullable UUID reviewPeriodId, @Nullable UUID templateId, @Nullable List<UUID> requesteeIds) {
        return Mono.fromCallable(() -> feedbackReqServices.findByValues(creatorId, requesteeId, recipientId, oldestDate, reviewPeriodId, templateId, requesteeIds))
                .map(entities -> entities.stream().map(this::fromEntity).collect(Collectors.toList()))
                .map(HttpResponse::ok);
    }

    private FeedbackRequestResponseDTO fromEntity(FeedbackRequest feedbackRequest) {
        FeedbackRequestResponseDTO dto = new FeedbackRequestResponseDTO();
        dto.setId(feedbackRequest.getId());
        dto.setCreatorId(feedbackRequest.getCreatorId());
        dto.setRequesteeId(feedbackRequest.getRequesteeId());
        dto.setRecipientId(feedbackRequest.getRecipientId());
        dto.setTemplateId(feedbackRequest.getTemplateId());
        dto.setSendDate(feedbackRequest.getSendDate());
        dto.setDueDate(feedbackRequest.getDueDate());
        dto.setStatus(feedbackRequest.getStatus());
        dto.setSubmitDate(feedbackRequest.getSubmitDate());
        dto.setReviewPeriodId(feedbackRequest.getReviewPeriodId());

        return dto;
    }

    private FeedbackRequest fromDTO(FeedbackRequestCreateDTO dto) {
        return new FeedbackRequest(
                dto.getCreatorId(),
                dto.getRequesteeId(),
                dto.getRecipientId(),
                dto.getTemplateId(),
                dto.getSendDate(),
                dto.getDueDate(),
                dto.getStatus(),
                dto.getSubmitDate(),
                dto.getReviewPeriodId());
    }

}

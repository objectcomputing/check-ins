package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.feedback_request.DTO.DenyFeedbackRequestDTO;
import com.objectcomputing.checkins.services.feedback_request.DTO.DenierDTO;
import com.objectcomputing.checkins.services.feedback_request.DTO.CreatorDTO;
import com.objectcomputing.checkins.services.notification.NotificationService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@Controller("/services/feedback/requests")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "feedback request")
public class FeedbackRequestController {

    private final FeedbackRequestServices feedbackReqServices;
    private final NotificationService notificationService;

    @Inject
    public FeedbackRequestController(FeedbackRequestServices feedbackReqServices, NotificationService notificationService) {
        this.feedbackReqServices = feedbackReqServices;
        this.notificationService = notificationService;
    }

    /**
     * Create a feedback request
     *
     * @param requestBody {@link FeedbackRequestCreateDTO} New feedback request to create
     * @return {@link FeedbackRequestResponseDTO}
     */
    @RequiredPermission(Permission.CAN_CREATE_FEEDBACK_REQUEST)
    @Post
    public HttpResponse<FeedbackRequestResponseDTO> save(@Body @Valid @NotNull FeedbackRequestCreateDTO requestBody) {
        FeedbackRequest savedFeedbackRequest = feedbackReqServices.save(fromDTO(requestBody));
        return HttpResponse.created(fromEntity(savedFeedbackRequest))
                .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedbackRequest.getId())));
    }

    /**
     * Update a feedback request
     *
     * @param requestBody {@link FeedbackRequestUpdateDTO} The updated feedback request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Put
    public HttpResponse<FeedbackRequestResponseDTO> update(@Body @Valid @NotNull FeedbackRequestUpdateDTO requestBody) {
        FeedbackRequest savedFeedback = feedbackReqServices.update(requestBody);
        return HttpResponse.ok(fromEntity(savedFeedback))
                .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedback.getId())));
    }

    /**
     * Delete a feedback request by UUID
     *
     * @param id {@link UUID} of the feedback request to be deleted
     * @return {@link HttpResponse}
     */
    @Delete("/{id}")
    @RequiredPermission(Permission.CAN_DELETE_FEEDBACK_REQUEST)
    @Status(HttpStatus.OK)
    public void delete(@NotNull UUID id) {
        feedbackReqServices.delete(id);
    }

    /**
     * Get feedback request by ID
     *
     * @param id {@link UUID} ID of the request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Get("/{id}")
    @RequiredPermission(Permission.CAN_VIEW_FEEDBACK_REQUEST)
    public HttpResponse<FeedbackRequestResponseDTO> getById(UUID id) {
        FeedbackRequest savedFeedbackRequest = feedbackReqServices.getById(id);
        return savedFeedbackRequest == null ? HttpResponse.notFound() : HttpResponse.ok(fromEntity(savedFeedbackRequest))
                .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedbackRequest.getId())));
    }

    /**
     * Search for all feedback requests that match the intersection of the provided values
     * Any values that are null are not applied to the intersection
     *
     * @param creatorId   The {@link UUID} of the creator of the request
     * @param requesteeId The {@link UUID} of the requestee
     * @param recipientId The {@link UUID} of the recipient
     * @param oldestDate  The date that filters out any requests that were made before that date
     * @return list of {@link FeedbackRequestResponseDTO}
     */
    @RequiredPermission(Permission.CAN_VIEW_FEEDBACK_REQUEST)
    @Get("/{?creatorId,requesteeId,recipientId,oldestDate,reviewPeriodId,templateId,requesteeIds}")
    public List<FeedbackRequestResponseDTO> findByValues(
            @Nullable UUID creatorId, 
            @Nullable UUID requesteeId, 
            @Nullable UUID recipientId, 
            @Nullable @Format("yyyy-MM-dd") LocalDate oldestDate, 
            @Nullable UUID reviewPeriodId, 
            @Nullable UUID templateId, 
            @Nullable List<UUID> requesteeIds) {
        return feedbackReqServices.findByValues(creatorId, requesteeId, recipientId, oldestDate, reviewPeriodId, templateId, requesteeIds)
                .stream()
                .map(this::fromEntity)
                .toList();
    }

    /**
     * Deny a feedback request
     *
     * @param id   {@link UUID} ID of the feedback request to deny
     * @param body Request body containing reason, denier, and creator information
     * @return {@link FeedbackRequestResponseDTO} with updated denial status
     */
    @Post("/{id}/deny")
@RequiredPermission(Permission.CAN_DENY_FEEDBACK_REQUEST)
public HttpResponse<FeedbackRequestResponseDTO> denyFeedbackRequest(
    @PathVariable("id") @NotNull UUID id, 
    @Body @Valid DenyFeedbackRequestDTO body
) {
    FeedbackRequest feedbackRequest = feedbackReqServices.getById(id);
    if (feedbackRequest == null) {
        return HttpResponse.notFound();
    }

    String reason = body.getReason();
    DenierDTO denier = body.getDenier();
    CreatorDTO creator = body.getCreator();

    if (!feedbackRequest.isDenied() && reason != null && !reason.trim().isEmpty()) {
        FeedbackRequestUpdateDTO dto = new FeedbackRequestUpdateDTO();
        dto.setId(feedbackRequest.getId());
        dto.setDueDate(feedbackRequest.getDueDate());
        dto.setStatus(feedbackRequest.getStatus());
        dto.setSubmitDate(feedbackRequest.getSubmitDate());
        dto.setRecipientId(feedbackRequest.getRecipientId());
        dto.setDenied(true);
        dto.setReason(reason);

        FeedbackRequest updatedFeedbackRequest = feedbackReqServices.update(dto);

        UUID creatorId = creator.getId();
        String denierName = denier.getName();
        notificationService.sendNotification(
            creatorId,
            String.format("Your feedback request was denied by %s. Reason: %s", denierName, reason)
        );

        return HttpResponse.ok(fromEntity(updatedFeedbackRequest));
    }

    return HttpResponse.ok(fromEntity(feedbackRequest));
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
        dto.setDenied(feedbackRequest.isDenied());
        dto.setReason(feedbackRequest.getReason());
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
                dto.getReviewPeriodId(),
                dto.isDenied(),
                dto.getReason()
        );
    }
}

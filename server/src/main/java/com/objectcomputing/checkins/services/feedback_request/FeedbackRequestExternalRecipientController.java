package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.feedback_external_recipient.FeedbackExternalRecipientServices;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@Controller("/services/feedback/requests/external/recipients")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
@Tag(name = "feedback external recipient")
public class FeedbackRequestExternalRecipientController {

    private final FeedbackRequestServices feedbackReqServices;
    private final FeedbackExternalRecipientServices feedbackExternalRecipientServices;

    public FeedbackRequestExternalRecipientController(FeedbackRequestServices feedbackRequestServices, FeedbackExternalRecipientServices feedbackExternalRecipientServices) {
        this.feedbackReqServices = feedbackRequestServices;
        this.feedbackExternalRecipientServices = feedbackExternalRecipientServices;
    }

    @Get("/{?creatorId,requesteeId,recipientId,oldestDate,reviewPeriodId,templateId,externalRecipientId,requesteeIds}")
    public List<FeedbackRequestResponseDTO> findByValues(@Nullable UUID creatorId, @Nullable UUID requesteeId, @Nullable UUID recipientId, @Nullable @Format("yyyy-MM-dd") LocalDate oldestDate, @Nullable UUID reviewPeriodId, @Nullable UUID templateId, @Nullable UUID externalRecipientId, @Nullable List<UUID> requesteeIds) {
        if (externalRecipientId == null) {
            throw new BadArgException("Missing required parameter: externalRecipientId");
        }
        return feedbackReqServices.findByValues(creatorId, requesteeId, recipientId, oldestDate, reviewPeriodId, templateId, externalRecipientId, requesteeIds)
                .stream()
                .map(this::feedbackRequestFromEntity)
                .toList();
    }

    /**
     * Update a feedback request
     *
     * @param requestBody {@link FeedbackRequestUpdateDTO} The updated feedback request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Put
    public HttpResponse<FeedbackRequestResponseDTO> update(@Body @Valid @NotNull FeedbackRequestUpdateDTO requestBody) {
        if (requestBody.getExternalRecipientId() == null) {
            throw new BadArgException("Missing required parameter: externalRecipientId");
        }
        FeedbackRequest savedFeedback = feedbackReqServices.update(requestBody);
        return HttpResponse.ok(feedbackRequestFromEntity(savedFeedback))
                .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedback.getId())));
    }

    /**
     * Get feedback request by ID
     *
     * @param id {@link UUID} ID of the request
     * @return {@link FeedbackRequestResponseDTO}
     */
    //@Secured(SecurityRule.IS_ANONYMOUS)
    @Get("/{id}")
    public HttpResponse<FeedbackRequestResponseDTO> getById(UUID id) {
        FeedbackRequest feedbackRequest = feedbackReqServices.getById(id);
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("Missing required parameter: externalRecipientId");
        }
        return feedbackRequest == null ? HttpResponse.notFound() : HttpResponse.ok(feedbackRequestFromEntity(feedbackRequest))
                .headers(headers -> headers.location(URI.create("/feedback_request" + feedbackRequest.getId())));
    }

    private FeedbackRequestResponseDTO feedbackRequestFromEntity(FeedbackRequest feedbackRequest) {
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
        dto.setExternalRecipientId(feedbackRequest.getExternalRecipientId());

        return dto;
    }

}

package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@Introspected
public class FeedbackRequestResponseDTO {

    @NotBlank
    @Schema(description = "unique id of the feedback request", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "id of the person who was requested to give feedback", required = true)
    private UUID recipientId;

    @NotBlank
    @Schema(description = "id of the feedback request creator", required = true)
    private UUID creatorId;

    @NotBlank
    @Schema(description = "id of the person who is getting feedback requested on them", required = true)
    private UUID requesteeId;

    @NotBlank
    @Schema(description = "id of the template the feedback request references", required = true)
    private UUID templateId;

    @NotBlank
    @Schema(description = "date request was sent")
    private LocalDate sendDate;

    @Nullable
    @Schema(description = "date request is due, if applicable")
    private LocalDate dueDate;

    @NotBlank
    @Schema(description = "completion status of request", required = true)
    private String status;

    @Nullable
    @Schema(description = "date the recipient submitted feedback for the request")
    private LocalDate submitDate;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    public UUID getRequesteeId() {
        return requesteeId;
    }

    public void setRequesteeId(UUID requesteeId) {
        this.requesteeId = requesteeId;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public LocalDate getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDate sendDate) {
        this.sendDate = sendDate;
    }

    @Nullable
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(@Nullable LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Nullable
    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(@Nullable LocalDate submitDate) {
        this.submitDate = submitDate;
    }

}

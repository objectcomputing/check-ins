package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Introspected
public class FeedbackRequestCreateDTO {

    @NotNull
    @Schema(description = "id of the feedback request creator", required = true)
    private UUID creatorId;

    @NotNull
    @Schema(description = "id of the person who is getting feedback requested on them", required = true)
    private UUID requesteeId;

    @NotNull
    @Schema(description = "id of the person who was requested to give feedback", required = true)
    private UUID recipientId;

    @NotNull
    @Schema(description = "id of the template the feedback request references", required = true)
    private UUID templateId;

    @NotNull
    @Schema(description = "date request was sent")
    private LocalDate sendDate;

    @Nullable
    @Schema(description = "date request is due (may be nullable)")
    private LocalDate dueDate;

    @NotNull
    @Schema(description = "completion status of request", required = true)
    private String status;

    @Nullable
    @Schema(description = "date the recipient submitted feedback for the request")
    private LocalDate submitDate;


    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public UUID getRequesteeId() {
        return requesteeId;
    }

    public void setRequesteeId(UUID requesteeId) {
        this.requesteeId = requesteeId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
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


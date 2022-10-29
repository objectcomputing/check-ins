package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "feedback_requests")
public class FeedbackRequest {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback request", required = true)
    private UUID id;

    @Column(name = "creator_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the feedback request creator", required = true)
    private UUID creatorId;

    @Column(name = "requestee_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the person who is getting feedback requested on them", required = true)
    private UUID requesteeId;

    @Column(name = "recipient_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the person who was requested to give feedback", required = true)
    private UUID recipientId;

    @Column(name = "template_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the template the feedback request references", required = true)
    private UUID templateId;

    @Column(name="send_date")
    @NotNull
    @Schema(description = "date request was sent", required = true)
    private LocalDate sendDate;

    @Column(name="due_date")
    @Nullable
    @Schema(description = "date request is due, if applicable")
    private LocalDate dueDate;

    @Column(name = "status")
    @NotNull
    @Schema(description = "completion status of request", required = true)
    private String status;

    @Column(name = "submit_date")
    @Nullable
    @Schema(description = "date the recipient submitted feedback for the request")
    private LocalDate submitDate;

    @Column(name = "review_period_id")
    @TypeDef(type = DataType.STRING)
    @Nullable
    @Schema(description = "the id of the review period in that this request was created for")
    private UUID reviewPeriodId;

    public FeedbackRequest(UUID creatorId,
                           UUID requesteeId,
                           UUID recipientId,
                           UUID templateId,
                           LocalDate sendDate,
                           @Nullable LocalDate dueDate,
                           String status,
                           @Nullable LocalDate submitDate,
                           @Nullable UUID reviewPeriodId) {
        this.id = null;
        this.creatorId = creatorId;
        this.requesteeId = requesteeId;
        this.recipientId = recipientId;
        this.templateId = templateId;
        this.sendDate = sendDate;
        this.dueDate = dueDate;
        this.status = status;
        this.submitDate = submitDate;
        this.reviewPeriodId = reviewPeriodId;
    }

    public FeedbackRequest() {}

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

    @Nullable
    public UUID getReviewPeriodId() {
        return reviewPeriodId;
    }

    public void setReviewPeriodId(UUID reviewPeriodId) {
        this.reviewPeriodId = reviewPeriodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackRequest that = (FeedbackRequest) o;
        return Objects.equals(id, that.id)
                && Objects.equals(creatorId, that.creatorId)
                && Objects.equals(requesteeId, that.requesteeId)
                && Objects.equals(recipientId, that.recipientId)
                && Objects.equals(templateId, that.templateId)
                && Objects.equals(sendDate, that.sendDate)
                && Objects.equals(dueDate, that.dueDate)
                && Objects.equals(status, that.status)
                && Objects.equals(submitDate, that.submitDate)
                && Objects.equals(reviewPeriodId, that.reviewPeriodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creatorId, recipientId, requesteeId, sendDate, templateId, dueDate, status, submitDate, reviewPeriodId);
    }

    @Override
    public String toString() {
        return "FeedbackRequest{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", recipientId=" + recipientId +
                ", requesteeId=" + requesteeId +
                ", templateId='" + templateId +
                ", sendDate=" + sendDate +
                ", dueDate=" + dueDate +
                ", status='" + status +
                ", submitDate='" + submitDate +
                ", reviewPeriodId='" + reviewPeriodId +
                '}';
    }
}

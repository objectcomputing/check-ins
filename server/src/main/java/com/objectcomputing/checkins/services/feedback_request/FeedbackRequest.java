package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.converter.LocalDateConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "feedback_requests")
public class FeedbackRequest {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback request")
    private UUID id;

    @Column(name = "creator_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the feedback request creator")
    private UUID creatorId;

    @Column(name = "requestee_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the person who is getting feedback requested on them")
    private UUID requesteeId;

    @Column(name = "recipient_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the person who was requested to give feedback")
    private UUID recipientId;

    @Column(name = "template_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the template the feedback request references")
    private UUID templateId;

    @Column(name="send_date")
    @NotNull
    @Schema(description = "date request was sent")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate sendDate;

    @Column(name="due_date")
    @Nullable
    @Schema(description = "date request is due, if applicable")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate dueDate;

    @Column(name = "status")
    @NotNull
    @Schema(description = "completion status of request")
    private String status;

    @Column(name = "submit_date")
    @Nullable
    @Schema(description = "date the recipient submitted feedback for the request")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate submitDate;

    @Column(name = "review_period_id")
    @TypeDef(type = DataType.STRING)
    @Nullable
    @Schema(description = "the id of the review period in that this request was created for")
    private UUID reviewPeriodId;

    @Column(name = "denied")
    @Nullable
    @Schema(description = "Whether the feedback request has been denied")
    private boolean denied = false;

    @Column(name = "reason")
    @Nullable
    @Schema(description = "Denial reason")
    private String reason;



    public FeedbackRequest(UUID creatorId,
                           UUID requesteeId,
                           UUID recipientId,
                           UUID templateId,
                           LocalDate sendDate,
                           @Nullable LocalDate dueDate,
                           String status,
                           @Nullable LocalDate submitDate,
                           @Nullable UUID reviewPeriodId,
                           boolean denied,
                           @Nullable String reason) {
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
        this.denied = denied;
        this.reason = reason;
    }

    public FeedbackRequest() {}

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
                && Objects.equals(reviewPeriodId, that.reviewPeriodId)
                && Objects.equals(denied, that.denied)
                && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creatorId, recipientId, requesteeId, sendDate, templateId, dueDate, status, submitDate, reviewPeriodId, denied, reason);
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
                ", denied='" + denied +
                ", reason='" + reason +
                '}';
    }
}

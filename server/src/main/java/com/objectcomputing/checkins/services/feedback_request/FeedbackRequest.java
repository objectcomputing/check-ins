package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "feedback_requests")
public class FeedbackRequest {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback request", required = true)
    private UUID id;

    @Column(name = "creatorId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the feedback request creator", required = true)
    private UUID creatorId;

    @Column(name = "requesteeId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the person who is getting feedback requested on them", required = true)
    private UUID requesteeId;

    @Column(name = "templateId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the template attached to request", required = true)
    private UUID templateId;

    @Column(name="sendDate")
    @Schema(description = "date request was sent")
    private LocalDate sendDate;

    @Column(name="dueDate")
    @Nullable
    @Schema(description = "date request is due (may be nullable)")
    private LocalDate dueDate;

    @Column(name = "status")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "Completion status of request", required = true)
    private String status;

    public FeedbackRequest(
                           @NotNull UUID creatorId,
                           @NotNull UUID requesteeId,
                           @NotNull UUID templateId,
                           @Nullable LocalDate sendDate,
                           @Nullable LocalDate dueDate,
                           @NotNull String status) {
        this.id = null;
        this.creatorId = creatorId;
        this.requesteeId = requesteeId;
        this.templateId = templateId;
        this.sendDate = sendDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public FeedbackRequest(@Nullable UUID id,
                           @NotNull UUID creatorId,
                           @NotNull UUID requesteeId,
                           @NotNull UUID templateId,
                           @Nullable LocalDate sendDate,
                           @Nullable LocalDate dueDate,
                           @NotNull String status
                           ) {
        this.id = id;
        this.creatorId = creatorId;
        this.requesteeId = requesteeId;
        this.templateId = templateId;
        this.sendDate = sendDate;
        this.dueDate = dueDate;
        this.status = status;

    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackRequest that = (FeedbackRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(creatorId, that.creatorId) && Objects.equals(requesteeId, that.requesteeId) && Objects.equals(templateId, that.templateId) && Objects.equals(sendDate, that.sendDate) && Objects.equals(dueDate, that.dueDate) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creatorId, requesteeId, templateId, sendDate, dueDate, status);
    }

    @Override
    public String toString() {
        return "FeedbackRequest{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", requesteeId=" + requesteeId +
                ", templateId=" + templateId +
                ", sendDate=" + sendDate +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                '}';
    }
}

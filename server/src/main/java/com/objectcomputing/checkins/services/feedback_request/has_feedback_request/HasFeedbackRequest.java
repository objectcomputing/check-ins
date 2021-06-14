package com.objectcomputing.checkins.services.feedback_request.has_feedback_request;

import javax.persistence.Table;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "has_feedback_request")
public class HasFeedbackRequest {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the has feedback request", required = true)
    private UUID id;

    @Column(name = "userId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of person the request was sent to", required = true)
    private UUID userId;

    @Column(name = "requestId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the request the has_request is attached to", required = true)
    private UUID requestId;

    @Column(name = "status")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "Completion status of request", required = true)
    private String status;

    @Column(name = "submitDate")
    @Schema(description = "date feedback was submitted from user with user_id")
    private LocalDate submitDate;

    @Column(name = "sentiment")
    @Nullable
    @TypeDef(type=DataType.FLOAT)
    @Schema(description = "Sentiment of feedback response", required = true)
    private Float sentiment;

    public HasFeedbackRequest(@NotNull UUID userId, @NotNull UUID requestId, @NotNull String status, @NotNull LocalDate submitDate, @Nullable Float sentiment  ) {
        this.id = null;
        this.userId =  userId;
        this.requestId = requestId;
        this.status = status;
        this.submitDate = submitDate;
        this.sentiment = sentiment;

    }

    public HasFeedbackRequest(@NotNull UUID id, @NotNull UUID userId, @NotNull UUID requestId, @NotNull String status, @NotNull LocalDate submitDate, @Nullable Float sentiment  ) {
        this.id = id;
        this.userId =  userId;
        this.requestId = requestId;
        this.status = status;
        this.submitDate = submitDate;
        this.sentiment = sentiment;

    }

    @Override
    public String toString() {
        return "HasFeedbackRequest{" +
                "id=" + id +
                ", userId=" + userId +
                ", requestId=" + requestId +
                ", status='" + status + '\'' +
                ", submitDate=" + submitDate +
                ", sentiment=" + sentiment +
                '}';
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
    }

    @Nullable
    public Float getSentiment() {
        return sentiment;
    }

    public void setSentiment(@Nullable Float sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HasFeedbackRequest that = (HasFeedbackRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(requestId, that.requestId) && Objects.equals(status, that.status) && Objects.equals(submitDate, that.submitDate) && Objects.equals(sentiment, that.sentiment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, requestId, status, submitDate, sentiment);
    }






}

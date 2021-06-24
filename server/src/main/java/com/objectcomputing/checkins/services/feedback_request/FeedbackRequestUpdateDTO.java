package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Introspected
public class FeedbackRequestUpdateDTO {

    @NotNull
    @Schema(description = "unique id of the feedback request", required = true)
    private UUID id;

    @Nullable
    @Schema(description = "date request is due, if applicable")
    private LocalDate dueDate;

    @NotNull
    @Schema(description = "Completion status of request", required = true)
    private String status;

    @Nullable
    @Schema(description = "date the recipient submitted feedback for the request")
    private LocalDate submitDate;

    @Nullable
    @Schema(description = "sentiment of the recipient's feedback")
    private Double sentiment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
    public Double getSentiment() {
        return sentiment;
    }

    public void setSentiment(@Nullable Double sentiment) {
        this.sentiment = sentiment;
    }
}

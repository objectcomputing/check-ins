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
    @Schema(description = "date request is due (may be nullable)")
    private LocalDate dueDate;

    @NotNull
    @Schema(description = "Completion status of request", required = true)
    private String status;

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
}

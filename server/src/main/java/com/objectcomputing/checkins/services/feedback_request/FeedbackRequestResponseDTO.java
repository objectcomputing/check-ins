package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class FeedbackRequestResponseDTO {

    @NotNull
    @Schema(description = "unique id of the feedback request")
    private UUID id;

    @NotNull
    @Schema(description = "id of the person who was requested to give feedback")
    private UUID recipientId;

    @NotNull
    @Schema(description = "id of the feedback request creator")
    private UUID creatorId;

    @NotNull
    @Schema(description = "id of the person who is getting feedback requested on them")
    private UUID requesteeId;

    @NotNull
    @Schema(description = "id of the template the feedback request references")
    private UUID templateId;

    @NotNull
    @Schema(description = "date request was sent")
    private LocalDate sendDate;

    @Nullable
    @Schema(description = "date request is due, if applicable")
    private LocalDate dueDate;

    @NotNull
    @Schema(description = "completion status of request")
    private String status;

    @Nullable
    @Schema(description = "date the recipient submitted feedback for the request")
    private LocalDate submitDate;

    @Nullable
    @Schema(description = "the id of the review period in that this request was created for")
    private UUID reviewPeriodId;

    @Schema(description = "Whether the feedback request has been denied")
    private boolean denied = false;

    @Nullable
    @Schema(description = "Reason for the request being denied")
    private String reason;

    public FeedbackRequestResponseDTO() {
        this.denied = false;
    }

}

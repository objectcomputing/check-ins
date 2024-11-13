package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import com.objectcomputing.checkins.services.feedback_request.DTO.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class FeedbackRequestUpdateDTO {

    @NotNull
    @Schema(description = "unique id of the feedback request")
    private UUID id;

    @Nullable
    @Schema(description = "date request is due, if applicable")
    private LocalDate dueDate;

    @NotNull
    @Schema(description = "Completion status of request")
    private String status;

    @Nullable
    @Schema(description = "date the recipient submitted feedback for the request")
    private LocalDate submitDate;

    @Nullable
    @Schema(description = "the recipient of the request, used to reassign")
    private UUID recipientId;

    @Schema(description = "Whether the feedback request has been denied")
    private boolean denied = false;

    @Nullable
    @Schema(description = "Reason for the request being denied")
    private String reason;

}

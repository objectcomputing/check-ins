package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class FeedbackAnswerResponseDTO {

    @NotNull
    @Schema(description = "unique id of the feedback answer")
    private UUID id;

    @Nullable
    @Schema(description = "the content of the answer")
    private String answer;

    @NotNull
    @Schema(description = "id of the feedback question the answer is linked to")
    private UUID questionId;

    @NotNull
    @Schema(description = "id of the request this question is linked to ")
    private UUID requestId;

    @Nullable
    @Schema(description = "the sentiment of the answer")
    private Double sentiment;
}

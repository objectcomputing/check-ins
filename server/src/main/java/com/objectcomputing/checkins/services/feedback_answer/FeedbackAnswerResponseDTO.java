package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class FeedbackAnswerResponseDTO {

    @NotNull
    @Schema(description = "unique id of the feedback answer", required = true)
    private UUID id;

    @Nullable
    @Schema(description = "the content of the answer", required = true)
    private String answer;

    @NotBlank
    @Schema(description = "id of the feedback question the answer is linked to", required = true)
    private UUID questionId;

    @NotBlank
    @Schema(description = "id of the request this question is linked to ", required = true)
    private UUID requestId;

    @Nullable
    @Schema(description = "the sentiment of the answer")
    private Double sentiment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    @Nullable
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(@Nullable String answer) {
        this.answer = answer;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }


    @Nullable
    public Double getSentiment() {
        return sentiment;
    }

    public void setSentiment(@Nullable Double sentiment) {
        this.sentiment = sentiment;
    }
}

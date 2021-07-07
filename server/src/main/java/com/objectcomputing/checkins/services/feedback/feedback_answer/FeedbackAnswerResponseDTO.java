package com.objectcomputing.checkins.services.feedback.feedback_answer;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class FeedbackAnswerResponseDTO {

    @NotNull
    @Schema(description = "id of the answer entry", required = true)
    private UUID entryId;

    @NotBlank
    @Schema(description = "content of the feedback", required = true)
    private String answer;

    @NotNull
    @Schema(description = "id of member profile who answered question", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "id of question the answer is attached to", required = true)
    private UUID questionId;

    @NotNull
    @Schema(description = "id of the feedback request the answer is attached to", required = true)
    private UUID requestId;

    @Nullable
    @Schema(description = "The machine-analyzed sentiment of the feedback answer", required = false)
    private Float sentiment;


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

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

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public Float getSentiment() {
        return sentiment;
    }

    public void setSentiment(Float sentiment) {
        this.sentiment = sentiment;
    }
}

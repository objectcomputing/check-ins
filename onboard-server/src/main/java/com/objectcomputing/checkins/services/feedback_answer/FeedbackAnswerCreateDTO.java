package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Introspected
public class FeedbackAnswerCreateDTO {

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    @Nullable
    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(@Nullable UUID requestId) {
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

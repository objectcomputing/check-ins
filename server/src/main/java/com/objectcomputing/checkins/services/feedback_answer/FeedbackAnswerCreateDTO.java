package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Introspected
public class FeedbackAnswerCreateDTO {

    @NotNull
    @Schema(description = "id of the feedback question the answer is linked to", required = true)
    private String questionId;

    @NotBlank
    @Schema(description = "the content of the answer", required = true)
    private String answer;

    @Nullable
    @Schema(description = "the sentiment of the answer")
    private String sentiment;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Nullable
    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(@Nullable String sentiment) {
        this.sentiment = sentiment;
    }
}

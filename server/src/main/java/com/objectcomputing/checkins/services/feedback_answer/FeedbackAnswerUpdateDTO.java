package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class FeedbackAnswerUpdateDTO {

    @NotNull
    @Schema(description = "unique id of the feedback answer", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "the content of the answer", required = true)
    private String answer;

    @Nullable
    @Schema(description = "the sentiment of the answer")
    private Double sentiment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Nullable
    public Double getSentiment() {
        return sentiment;
    }

    public void setSentiment(@Nullable Double sentiment) {
        this.sentiment = sentiment;
    }
}

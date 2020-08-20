package com.objectcomputing.checkins.services.questions;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class QuestionUpdateDTO {
    @Schema(description = "id of the question this entry is associated with")
    @NotNull
    private UUID questionId;

    @NotBlank
    @Schema(description = "text of the question being asked", required = true)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }
}

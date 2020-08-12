package com.objectcomputing.checkins.services.questions;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

public class QuestionResponseDTO {
    @Schema(description = "id of the member this entry is associated with")
    private UUID questionId;

    @NotBlank
    @Schema(description = "text of the question being asked", required = true)
    private String text;

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

package com.objectcomputing.checkins.services.feedback_request_questions;


import com.sun.istack.Nullable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class FeedbackRequestQuestionUpdateDTO{

    @NotNull
    @Schema(description = "id of the question answer entry", required = true)
    private UUID id;

    @Nullable
    @Schema(description = "The answer to the question", required = false)
    private String answerContent;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }
}

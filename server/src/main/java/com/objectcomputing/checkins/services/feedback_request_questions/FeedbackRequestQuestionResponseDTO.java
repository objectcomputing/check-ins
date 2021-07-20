package com.objectcomputing.checkins.services.feedback_request_questions;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class FeedbackRequestQuestionResponseDTO {

    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the request question answer entry", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "The question asked to the recipient", required = true)
    private String question;

    @NotNull
    @Schema(description = "id of the feedback request the question is attached to", required = true)
    private UUID requestId;

    @NotNull
    @Schema(description = "Order number of the question relative to others in its set", required = true)
    private Integer questionNumber;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }
}

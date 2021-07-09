package com.objectcomputing.checkins.services.feedback_request_questions;

import com.sun.istack.Nullable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class FeedbackRequestQuestionCreateDTO {

    @NotNull
    @Schema(description = "id of the feedback request the question answer pair is attached to", required = true)
    private UUID requestId;

    @NotBlank
    @Schema(description = "The question asked to the recipient", required = true)
    private String questionContent;

    @Nullable
    @Schema(description = "The answer to the question", required = false)
    private String answerContent;

    @NotNull
    @Schema(description = "Order number of the question relative to others in its set", required = true)
    private Integer orderNum;

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}

package com.objectcomputing.checkins.services.feedback_request_questions;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class FeedbackRequestQuestionResponseDTO {

    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the request question answer entry", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "id of the feedback request the question answer pair is attached to", required = true)
    private UUID requestId;

    @NotBlank
    @Schema(description = "The question asked to the recipient", required = true)
    private String questionContent;

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

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}

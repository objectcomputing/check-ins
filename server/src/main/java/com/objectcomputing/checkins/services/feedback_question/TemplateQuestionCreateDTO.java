package com.objectcomputing.checkins.services.feedback_question;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class TemplateQuestionCreateDTO {

    @NotNull
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String question;

    @NotNull
    @Schema(description = "id of the template this question is a part of", required = true)
    private UUID templateId;

    @NotNull
    @Schema(description = "order of question in template", required = true)
    private Integer orderNum;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }


}

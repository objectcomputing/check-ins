package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
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

    @Nullable
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

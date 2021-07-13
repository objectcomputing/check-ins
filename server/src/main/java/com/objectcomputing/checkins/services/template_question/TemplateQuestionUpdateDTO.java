package com.objectcomputing.checkins.services.template_question;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class TemplateQuestionUpdateDTO {
    @NotNull
        @Schema(description = "id of the template question", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String question;

    @Nullable
    @Schema(description = "order of question in template", required = true)
    private Integer orderNum;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}

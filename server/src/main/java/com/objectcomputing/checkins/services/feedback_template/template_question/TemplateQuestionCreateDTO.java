package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Introspected
public class TemplateQuestionCreateDTO {

    @NotBlank
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String question;

    @NotNull
    @Schema(description = "id of the template this question is a part of", required = true)
    private UUID templateId;

    @NotNull
    @Schema(description = "order of question in template", required = true)
    private Integer questionNumber;

    @NotBlank
    @Schema(description = "the type of input used to answer the question", required = true)
    public String inputType;

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

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }
}

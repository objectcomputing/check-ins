package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Introspected
public class TemplateQuestionResponseDTO {

    @Schema(description = "unique id of the feedback question", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String question;

    @NotBlank
    @Schema(description = "id of the template this question is a part of", required = true)
    private UUID templateId;

    @NotBlank
    @Schema(description = "order of question in template", required = true)
    private Integer questionNumber;

    @NotBlank
    @Schema(description = "the type of input used to answer the question", required = true)
    public String inputType;

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

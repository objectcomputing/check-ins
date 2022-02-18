package com.objectcomputing.checkins.services.feedback_template.template_question.template_questions;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Introspected
public class TemplateQuestionUpdateDTO {

    @NotBlank
    @Schema(description = "id of the template question", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String question;

    @NotBlank
    @Schema(description = "order of question in template", required = true)
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

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }
}

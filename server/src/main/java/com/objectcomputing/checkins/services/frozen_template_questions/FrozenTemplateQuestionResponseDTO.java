package com.objectcomputing.checkins.services.frozen_template_questions;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class FrozenTemplateQuestionResponseDTO {

    @NotNull
    @Schema(description = "unique id of the request question answer entry", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "id of the versioned template (and by extension, feedback request) that question is attached to ", required = true)
    private UUID frozenTemplateId;


    @NotBlank
    @Schema(description = "The question asked to the recipient", required = true)
    private String question;

    @NotNull
    @Schema(description = "Order number of the question relative to others in its set", required = true)
    private Integer questionNumber;

    public UUID getFrozenTemplateId() {
        return frozenTemplateId;
    }

    public void setFrozenTemplateId(UUID frozenTemplateId) {
        this.frozenTemplateId = frozenTemplateId;
    }

    public String getQuestionContent() {
        return question;
    }

    public void setQuestionContent(String question) {
        this.question = question;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

}
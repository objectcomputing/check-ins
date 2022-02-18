package com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

public class TemplateQuestionValueResponseDTO {

    @Schema(description = "unique id of the feedback question", required = true)
    private UUID id;

    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String optionText;

    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the template question this option belongs to", required = true)
    private UUID questionId;

    @NotBlank
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "order of option when displayed as part of the question", required = true)
    private Integer optionNumber;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public Integer getOptionNumber() {
        return optionNumber;
    }

    public void setOptionNumber(Integer optionNumber) {
        this.optionNumber = optionNumber;
    }
}

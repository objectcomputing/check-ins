package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class TemplateQuestionUpdateDTO {

    @NotNull
    @Schema(description = "id of the template question")
    private UUID id;

    @NotBlank
    @Schema(description = "text of the question to receive feedback on")
    private String question;

    @NotNull
    @Schema(description = "order of question in template")
    private Integer questionNumber;

    @NotBlank
    @Schema(description = "the type of input used to answer the question")
    public String inputType;

}

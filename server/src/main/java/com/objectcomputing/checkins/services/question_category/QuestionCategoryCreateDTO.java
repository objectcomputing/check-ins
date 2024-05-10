package com.objectcomputing.checkins.services.question_category;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Introspected
public class QuestionCategoryCreateDTO {
    @NotBlank
    @Schema(description = "name of the category for the question")
    private String name;

}

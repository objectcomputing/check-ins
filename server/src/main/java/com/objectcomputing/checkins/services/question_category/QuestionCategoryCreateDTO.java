package com.objectcomputing.checkins.services.question_category;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Introspected
public class QuestionCategoryCreateDTO {
    @NotBlank
    @Schema(description = "name of the category for the question")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

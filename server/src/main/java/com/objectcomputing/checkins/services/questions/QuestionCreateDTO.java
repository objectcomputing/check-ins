package com.objectcomputing.checkins.services.questions;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Introspected
public class QuestionCreateDTO {
    @NotBlank
    @Schema(description = "text of the question being asked", required = true)
    private String text;

    @Schema(description = "category id of the question being asked")
    private UUID categoryId;

    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public UUID getCategoryId() { return categoryId; }

    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
}

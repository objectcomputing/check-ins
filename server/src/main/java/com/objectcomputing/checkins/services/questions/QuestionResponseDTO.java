package com.objectcomputing.checkins.services.questions;

import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Introspected
public class QuestionResponseDTO {
    @Schema(description = "id of the question this entry is associated with")
    private UUID id;

    @NotBlank
    @Schema(description = "text of the question being asked", required = true)
    private String text;

    @Nullable
    @Schema(description="id of the category of the question")
    private UUID categoryId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UUID getCategoryId() { return categoryId;
    }

    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId;
    }
}

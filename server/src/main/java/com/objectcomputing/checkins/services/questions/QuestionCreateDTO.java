package com.objectcomputing.checkins.services.questions;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotBlank;

@Introspected
public class QuestionCreateDTO {
    @NotBlank
    @Schema(description = "text of the question being asked", required = true)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(@Nonnull String text) {
        this.text = text;
    }
}

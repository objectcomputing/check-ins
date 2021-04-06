package com.objectcomputing.checkins.services.questions;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.NonNull;
import javax.validation.constraints.NotBlank;

@Introspected
public class QuestionCreateDTO {
    @NotBlank
    @Schema(description = "text of the question being asked", required = true)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }
}

package com.objectcomputing.checkins.services.questions;

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
public class QuestionUpdateDTO {
    @Schema(description = "id of the question this entry is associated with")
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(description = "text of the question being asked")
    private String text;

    @Schema(description = "id of the category of the question being asked")
    private UUID categoryId;

}

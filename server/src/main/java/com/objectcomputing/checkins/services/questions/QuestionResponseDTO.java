package com.objectcomputing.checkins.services.questions;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class QuestionResponseDTO {
    @Schema(description = "id of the question this entry is associated with")
    private UUID id;

    @NotBlank
    @Schema(description = "text of the question being asked")
    private String text;
}

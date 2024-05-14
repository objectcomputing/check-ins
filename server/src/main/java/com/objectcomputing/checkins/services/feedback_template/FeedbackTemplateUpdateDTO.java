package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class FeedbackTemplateUpdateDTO {

    @NotNull
    @Schema(description = "id of the feedback template")
    private UUID id;

    @NotNull
    @Schema(description = "whether or not the template is allowed to be used for a feedback request")
    private Boolean active;
}

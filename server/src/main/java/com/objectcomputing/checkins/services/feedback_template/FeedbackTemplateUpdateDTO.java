package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Introspected
public class FeedbackTemplateUpdateDTO {

    @NotNull
    @Schema(description = "id of the feedback template", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "whether or not the template is allowed to be used for a feedback request", required = true)
    private Boolean active;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

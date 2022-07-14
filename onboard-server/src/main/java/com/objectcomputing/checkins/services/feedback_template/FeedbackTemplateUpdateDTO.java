package com.objectcomputing.checkins.services.feedback_template;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.UUID;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class FeedbackTemplateUpdateDTO {

    @NotBlank
    @Schema(description = "id of the feedback template", required = true)
    private UUID id;

    @NotBlank
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

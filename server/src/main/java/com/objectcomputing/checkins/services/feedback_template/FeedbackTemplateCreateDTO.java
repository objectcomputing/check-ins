package com.objectcomputing.checkins.services.feedback_template;


import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.UUID;

@Introspected
public class FeedbackTemplateCreateDTO {

    @NotBlank
    @Schema(description = "title of the feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "description of the feedback template", required = false)
    private String description;

    @NotNull
    @Schema(description = "ID of person who created the feedback template", required = true)
    private UUID createdBy;

    @NotNull
    @Schema(description = "whether the template can still be used", required = true)
    private Boolean active;


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public Boolean getActive() {
        return active;
    }
}

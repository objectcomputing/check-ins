package com.objectcomputing.checkins.services.feedback_template;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import java.util.UUID;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class FeedbackTemplateUpdateDTO {

    @NotBlank
    @Schema(description = "id of the feedback template", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "the updated title of the feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "the updated description of the feedback template")
    private String description;

    @NotBlank
    @Schema(description = "UUID of person who last updated the feedback template")
    private UUID updaterId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public UUID getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(UUID updaterId) {
        this.updaterId = updaterId;
    }
}

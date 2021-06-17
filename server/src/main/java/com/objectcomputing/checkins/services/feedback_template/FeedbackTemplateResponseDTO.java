package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;


@Introspected
public class FeedbackTemplateResponseDTO {

    @NotNull
    @Schema(description = "id of the feedback template", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "title of the feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "description of the feedback template")
    private String description;

    @NotNull
    @Schema(description = "ID of person who created the feedback template", required = true)
    private UUID createdBy;

    @NotNull
    @Schema(description = "whether or not the template is visible only to the person who made it", required=true)
    private Boolean isPrivate;

    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getId() {
        return id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }
    public Boolean getIsPrivate() { return isPrivate; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
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

}

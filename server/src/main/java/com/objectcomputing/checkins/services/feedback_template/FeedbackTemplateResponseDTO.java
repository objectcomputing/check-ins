package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;


@Introspected
public class FeedbackTemplateResponseDTO {

    @NotBlank
    @Schema(description = "id of the feedback template", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "title of the feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "description of the feedback template")
    private String description;

    @Nullable
    @Schema(description = "ID of person who created the feedback template", required = true)
    private UUID creatorId;

    @NotBlank
    @Schema(description = "date the template was created", required = true)
    private LocalDate dateCreated;

    @NotBlank
    @Schema(description = "UUID of person who last updated the feedback template")
    private UUID updaterId;

    @Nullable
    @Schema(description = "date the template was last updated")
    private LocalDate dateUpdated;

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

    @Nullable
    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(@Nullable UUID creatorId) {
        this.creatorId = creatorId;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public UUID getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(UUID updaterId) {
        this.updaterId = updaterId;
    }

    @Nullable
    public LocalDate getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(@Nullable LocalDate dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}

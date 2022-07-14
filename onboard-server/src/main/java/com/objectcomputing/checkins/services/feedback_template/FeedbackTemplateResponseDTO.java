package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

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

    @NotBlank
    @Schema(description = "ID of person who created the feedback template", required = true)
    private UUID creatorId;

    @NotBlank
    @Schema(description = "date the template was created", required = true)
    private LocalDate dateCreated;

    @NotBlank
    @Schema(description = "whether or not the template is allowed to be used for a feedback request", required = true)
    private Boolean active;

    @NotBlank
    @Schema(description = "whether the template is accessible to everyone or just the creator", required = true)
    private Boolean isPublic;

    @NotBlank
    @Schema(description = "whether the template is an ad-hoc template", required = true)
    private Boolean isAdHoc;

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

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsAdHoc() {
        return isAdHoc;
    }

    public void setIsAdHoc(Boolean isAdHoc) {
        this.isAdHoc = isAdHoc;
    }

}

package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Introspected
public class FeedbackTemplateCreateDTO {

    @NotBlank
    @Schema(description = "title of the feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "description of the feedback template")
    private String description;

    @NotNull
    @Schema(description = "UUID of person who created the feedback template", required = true)
    private UUID creatorId;

    @NotNull
    @Schema(description = "whether or not the template is allowed to be used for a feedback request", required = true)
    private Boolean active;

    @NotNull
    @Schema(description = "whether the template is accessible to everyone or just the creator", required = true)
    private Boolean isPublic;

    @NotNull
    @Schema(description = "whether the template is an ad-hoc template", required = true)
    private Boolean isAdHoc;

    @NotNull
    @Schema(description = "whether the template is used for performance reviews", required = true)
    private Boolean isReview;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public Boolean getIsAdHoc() {
        return isAdHoc;
    }

    public void setIsAdHoc(Boolean isAdHoc) {
        this.isAdHoc = isAdHoc;
    }

    public Boolean getIsReview() {
        return isReview;
    }

    public void setIsReview(Boolean isReview) {
        this.isReview = isReview;
    }

}

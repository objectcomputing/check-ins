package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "feedback_templates")
public class FeedbackTemplate {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback template ", required = true)
    private UUID id;

    @Column(name = "title")
    @NotBlank
    @Schema(description = "title of feedback template", required = true)
    private String title;

    @Column(name = "description")
    @Nullable
    @Schema(description = "description of feedback template")
    private String description;

    @Column(name = "creator_id")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of person who created the feedback template", required = true)
    private UUID creatorId;

    @Column(name = "date_created")
    @DateCreated
    @Schema(description = "date the template was created", required = true)
    private LocalDate dateCreated;

    @Column(name = "active")
    @NotBlank
    @Schema(description = "whether or not the template is allowed to be used for a feedback request", required = true)
    private Boolean active;

    @Column(name = "is_public")
    @NotBlank
    @Schema(description = "whether the template is accessible to everyone or just the creator", required = true)
    private Boolean isPublic;

    @Column(name = "is_ad_hoc")
    @NotBlank
    @Schema(description = "whether the template is an ad-hoc template", required = true)
    private Boolean isAdHoc;

    @Column(name = "is_review")
    @NotBlank
    @Schema(description = "indicates whether the template is utilized for performance reviews", required = true)
    private Boolean isReview;

    /**
     * Constructs a new {@link FeedbackTemplate} to save
     *
     * @param title       The title of the template
     * @param description An optional description of the template
     * @param creatorId   The {@link UUID} of the user who created the template
     * @param isPublic    Whether the template is public or private
     * @param isAdHoc     Whether the template is an ad-hoc template
     */
    public FeedbackTemplate(String title, @Nullable String description, UUID creatorId, Boolean isPublic, Boolean isAdHoc, Boolean isReview) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
        this.active = true;
        this.isPublic = isPublic;
        this.isAdHoc = isAdHoc;
        this.isReview = isReview;
    }

    /**
     * Constructs a {@link FeedbackTemplate} to update
     *
     * @param id     The existing {@link UUID} of the template
     * @param active Whether or not the template is allowed to be used for a feedback request
     */
    public FeedbackTemplate(UUID id, Boolean active) {
        this.id = id;
        this.active = active;
        this.isReview = false;
    }

    public FeedbackTemplate() {
    }

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

    public Boolean getIsReview() { return isReview; }

    public void setIsReview(Boolean review) { isReview = review; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackTemplate that = (FeedbackTemplate) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(creatorId, that.creatorId) &&
                Objects.equals(dateCreated, that.dateCreated) &&
                Objects.equals(active, that.active) &&
                Objects.equals(isPublic, that.isPublic) &&
                Objects.equals(isAdHoc, that.isAdHoc) &&
                Objects.equals(isReview, that.isReview);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, creatorId, dateCreated, active, isPublic, isAdHoc, isReview);
    }

    @Override
    public String toString() {
        return "FeedbackTemplate{" +
                "id=" + id +
                ", title='" + title +
                ", description='" + description +
                ", creatorId=" + creatorId +
                ", dateCreated=" + dateCreated +
                ", active=" + active +
                ", isPublic=" + isPublic +
                ", isAdHoc=" + isAdHoc +
                ", isReview=" + isReview +
                '}';
    }
}

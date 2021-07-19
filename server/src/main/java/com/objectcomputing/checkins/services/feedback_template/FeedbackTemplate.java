package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
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
    @TypeDef(type = DataType.STRING)
    @Schema(description = "title of feedback template", required = true)
    private String title;

    @Column(name = "description")
    @Nullable
    @TypeDef(type = DataType.STRING)
    @Schema(description = "description of feedback template")
    private String description;

    @Column(name = "creator_id")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of person who created the feedback template", required = true)
    private UUID creatorId;

    @Column(name = "date_created")
    @DateCreated
    @NotBlank
    @TypeDef(type = DataType.DATE)
    @Schema(description = "date the template was created", required = true)
    private LocalDate dateCreated;

    @Column(name = "updater_id")
    @Nullable
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of person who last updated the feedback template")
    private UUID updaterId;

    @Column(name = "date_updated")
    @DateUpdated
    @Nullable
    @TypeDef(type = DataType.DATE)
    @Schema(description = "date the template was last updated")
    private LocalDate dateUpdated;

    /**
     * Constructs a new {@link FeedbackTemplate} to save
     *
     * @param title The title of the template
     * @param description An optional description of the template
     * @param creatorId The {@link UUID} of the user who created the template
     */
    public FeedbackTemplate(@NotBlank String title, @Nullable String description, @NotBlank UUID creatorId) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
        this.updaterId = null;
    }

    /**
     * Constructs a {@link FeedbackTemplate} to update
     *
     * @param id The existing {@link UUID} of the template
     * @param title The updated title of the template
     * @param description The optional updated description of the template
     * @param updaterId The {@link UUID} of the user who most recently updated the template
     */
    public FeedbackTemplate(@NotBlank UUID id, @NotBlank String title, @Nullable String description, @Nullable UUID updaterId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.updaterId = updaterId;
    }

    public FeedbackTemplate () {}

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

    @Nullable
    public UUID getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(@Nullable UUID updaterId) {
        this.updaterId = updaterId;
    }

    @Nullable
    public LocalDate getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(@Nullable LocalDate dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

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
                Objects.equals(updaterId, that.updaterId) &&
                Objects.equals(dateUpdated, that.dateUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, creatorId, dateCreated, updaterId, dateUpdated);
    }

    @Override
    public String toString() {
        return "FeedbackTemplate{" +
                "id=" + id +
                ", title='" + title +
                ", description='" + description +
                ", creatorId=" + creatorId +
                ", dateCreated=" + dateCreated +
                ", updaterId=" + updaterId +
                ", dateUpdated=" + dateUpdated +
                '}';
    }
}

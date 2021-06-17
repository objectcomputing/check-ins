package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @Schema(description = "description of feedback template", required = false)
    private String description;

    @Column(name = "createdBy")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of person who created the feedback template", required = true)
    private UUID createdBy;

    @Column(name = "isPrivate")
    @NotNull
    @TypeDef(type = DataType.BOOLEAN)
    @Schema(description = "whether this feedback template is visible only to its creator", required = true)
    private Boolean isPrivate;

    public FeedbackTemplate(@NotNull String title, @Nullable String description, @NotNull UUID createdBy, @NotNull Boolean isPrivate) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.isPrivate = isPrivate;
    }

    public FeedbackTemplate(@Nullable UUID id, @NotNull String title, @Nullable String description, @NotNull UUID createdBy, @NotNull Boolean isPrivate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.isPrivate = isPrivate;
    }

    public FeedbackTemplate(@Nullable UUID id,
                    @NotNull String title,
                    @Nullable String description,
                    @NotNull Boolean isPrivate
    ) {
        this.id = id;
        this.title = title;
        this.isPrivate = isPrivate;
        this.description = description;
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

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackTemplate that = (FeedbackTemplate) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(createdBy, that.createdBy) && Objects.equals(isPrivate, that.isPrivate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, createdBy, isPrivate);
    }

    @Override
    public String
    toString() {
        return "FeedbackTemplate{" +
                "id=" + id +
                ", title='" + title +
                ", description='" + description +
                ", createdBy=" + createdBy +
                ", isPrivate=" + isPrivate +
                '}';
    }
}

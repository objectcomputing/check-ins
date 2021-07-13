package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import io.micronaut.context.annotation.Type;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.Where;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
//@Where("@.active = true")
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

    @Column(name = "active")
    @NotBlank
    @TypeDef(type = DataType.BOOLEAN)
    @Schema(description = "whether the template can still be used", required = true)
    private Boolean active;

    @Schema(description = "list of template questions attached to template", required = false)
    List<TemplateQuestion> templateQuestions = new ArrayList<>();


    public FeedbackTemplate(@NotNull String title, @Nullable String description, @NotNull UUID createdBy) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.active = true;
    }

    public FeedbackTemplate(@NotNull String title, @Nullable String description, @NotNull UUID createdBy, @NotNull Boolean active) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.active = active;
    }

    public FeedbackTemplate(@NotNull UUID id,
                    @NotNull String title,
                    @Nullable String description,
                    @NotNull Boolean active
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.active = active;
    }

    public FeedbackTemplate() {}

    public List<TemplateQuestion> getTemplateQuestions() {
        return templateQuestions;
    }

    public void setTemplateQuestions(List<TemplateQuestion> templateQuestions) {
        this.templateQuestions = templateQuestions;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackTemplate that = (FeedbackTemplate) o;
        return Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(createdBy, that.createdBy)
                && Objects.equals(active, that.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, createdBy, active);
    }

    @Override
    public String
    toString() {
        return "FeedbackTemplate{" +
                "id=" + id +
                ", title='" + title +
                ", description='" + description +
                ", createdBy=" + createdBy +
                ", active=" + active+
                '}';
    }
}

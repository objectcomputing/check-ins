package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.converter.LocalDateConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "feedback_templates")
public class FeedbackTemplate {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback template")
    private UUID id;

    @Column(name = "title")
    @NotBlank
    @Schema(description = "title of feedback template")
    private String title;

    @Column(name = "description")
    @Nullable
    @Schema(description = "description of feedback template")
    private String description;

    @Column(name = "creator_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of person who created the feedback template")
    private UUID creatorId;

    @Column(name = "date_created")
    @DateCreated
    @Schema(description = "date the template was created")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate dateCreated;

    @Column(name = "active")
    @NotNull
    @Schema(description = "whether or not the template is allowed to be used for a feedback request")
    private Boolean active;

    @Column(name = "is_public")
    @NotNull
    @Schema(description = "whether the template is accessible to everyone or just the creator")
    private Boolean isPublic;

    @Column(name = "is_ad_hoc")
    @NotNull
    @Schema(description = "whether the template is an ad-hoc template")
    private Boolean isAdHoc;

    @Column(name = "is_review")
    @NotNull
    @Schema(description = "indicates whether the template is utilized for performance reviews")
    private Boolean isReview;

    @Column(name = "is_for_external_recipient")
    @Nullable
    @Schema(description = "indicates whether the template is utilized for external recipients")
    private Boolean isForExternalRecipient;

    /**
     * Constructs a new {@link FeedbackTemplate} to save
     *
     * @param title       The title of the template
     * @param description An optional description of the template
     * @param creatorId   The {@link UUID} of the user who created the template
     * @param isPublic    Whether the template is public or private
     * @param isAdHoc     Whether the template is an ad-hoc template
     * @param isReview    Whether the template is used for performance reviews
     */
    public FeedbackTemplate(String title, @Nullable String description, UUID creatorId, Boolean isPublic, Boolean isAdHoc, Boolean isReview, @Nullable Boolean isForExternalRecipient) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
        this.active = true;
        this.isPublic = isPublic;
        this.isAdHoc = isAdHoc;
        this.isReview = isReview;
        this.isForExternalRecipient = isForExternalRecipient;
    }

    public FeedbackTemplate(String title, @Nullable String description, UUID creatorId, Boolean isPublic, Boolean isAdHoc, Boolean isReview) {
        this(title, description, creatorId, isPublic, isAdHoc, isReview, null);
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
                Objects.equals(isReview, that.isReview)
                && customEquals(isForExternalRecipient, that.isForExternalRecipient)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, creatorId, dateCreated, active, isPublic, isAdHoc, isReview, isForExternalRecipient);
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
                ", isForExternalRecipient=" + isForExternalRecipient +
                '}';
    }

    public static boolean customEquals(Boolean a, Boolean b) {
        if (Boolean.FALSE.equals(a) && b == null) return true;
        if (a == null && Boolean.FALSE.equals(b)) return true;
        return Objects.equals(a, b);
    }

}

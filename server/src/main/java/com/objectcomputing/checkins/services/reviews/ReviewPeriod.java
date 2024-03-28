package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "review_periods")
public class ReviewPeriod {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The id of the review period", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    @Schema(description = "The name of the review period", required = true)
    private String name;

    @NotNull
    @Column(name = "open")
    @Schema(description = "Whether or not the review period is open")
    private Boolean open = true;

    @Column(name = "review_template_id")
    @TypeDef(type = DataType.STRING)
    @Nullable
    @Schema(description = "the id of the review template to be used for this review period")
    private UUID reviewTemplateId;

    @Column(name = "self_review_template_id")
    @TypeDef(type = DataType.STRING)
    @Nullable
    @Schema(description = "the id of the self-review template to be used for this review period")
    private UUID selfReviewTemplateId;

    public ReviewPeriod() {
    }

    public ReviewPeriod(String name) {
        this(name, true, null, null);
    }

    public ReviewPeriod(UUID id, String name, Boolean open) {
        this(name, open, null, null);
        this.id = id;
    }

    public ReviewPeriod(String name, Boolean open, @Nullable UUID reviewTemplateId, @Nullable UUID selfReviewTemplateId) {
        this.name = name;
        this.open = open;
        this.reviewTemplateId = reviewTemplateId;
        this.selfReviewTemplateId = selfReviewTemplateId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isOpen() { return open; }

    public void setOpen(Boolean open) { this.open = open; }

    @Nullable
    public UUID getReviewTemplateId() { return reviewTemplateId; }

    public void setReviewTemplateId(@Nullable UUID reviewTemplateId) { this.reviewTemplateId = reviewTemplateId; }

    @Nullable
    public UUID getSelfReviewTemplateId() { return selfReviewTemplateId; }

    public void setSelfReviewTemplateId(@Nullable UUID selfReviewTemplateId) { this.selfReviewTemplateId = selfReviewTemplateId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewPeriod that = (ReviewPeriod) o;
        return open == that.open && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(reviewTemplateId, that.reviewTemplateId) && Objects.equals(selfReviewTemplateId, that.selfReviewTemplateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, open, reviewTemplateId, selfReviewTemplateId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReviewPeriod{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", open=").append(open);
        sb.append(", reviewTemplateId=").append(reviewTemplateId);
        sb.append(", selfReviewTemplateId=").append(selfReviewTemplateId);
        sb.append('}');
        return sb.toString();
    }
}

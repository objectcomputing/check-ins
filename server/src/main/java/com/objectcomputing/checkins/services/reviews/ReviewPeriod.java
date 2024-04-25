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
import java.time.LocalDateTime;
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
    @Column(name = "status")
    @Schema(description = "The current status of the review period")
    private String status;

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

    @Column(name = "launch_date")
    private LocalDateTime launchDate;

    @Column(name = "self_review_close_date")
    private LocalDateTime selfReviewCloseDate;

    @Column(name = "close_date")
    private LocalDateTime closeDate;

    public ReviewPeriod() {
    }

    public ReviewPeriod(String name) {
        this(name, ReviewStatus.OPEN.name(), null, null, null, null, null);
    }

    public ReviewPeriod(UUID id, String name, String status) {
        this(name, status, null, null, null, null, null);
        this.id = id;
    }

    public ReviewPeriod(String name, String status, @Nullable UUID reviewTemplateId, @Nullable UUID selfReviewTemplateId,
                        LocalDateTime launchDate, LocalDateTime selfReviewCloseDate, LocalDateTime closeDate) {
        this.name = name;
        this.status = status;
        this.reviewTemplateId = reviewTemplateId;
        this.selfReviewTemplateId = selfReviewTemplateId;
        this.launchDate = launchDate;
        this.selfReviewCloseDate = selfReviewCloseDate;
        this.closeDate = closeDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Nullable
    public UUID getReviewTemplateId() { return reviewTemplateId; }

    public void setReviewTemplateId(@Nullable UUID reviewTemplateId) { this.reviewTemplateId = reviewTemplateId; }

    @Nullable
    public UUID getSelfReviewTemplateId() { return selfReviewTemplateId; }

    public void setSelfReviewTemplateId(@Nullable UUID selfReviewTemplateId) { this.selfReviewTemplateId = selfReviewTemplateId; }

    public LocalDateTime getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDateTime launchDate) {
        this.launchDate = launchDate;
    }

    public LocalDateTime getSelfReviewCloseDate() {
        return selfReviewCloseDate;
    }

    public void setSelfReviewCloseDate(LocalDateTime selfReviewCloseDate) {
        this.selfReviewCloseDate = selfReviewCloseDate;
    }

    public LocalDateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDateTime closeDate) {
        this.closeDate = closeDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, reviewTemplateId, selfReviewTemplateId, launchDate, selfReviewCloseDate, closeDate);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReviewPeriod{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", status=").append(status);
        sb.append(", reviewTemplateId=").append(reviewTemplateId);
        sb.append(", selfReviewTemplateId=").append(selfReviewTemplateId);
        sb.append(", launchDate=").append(launchDate);
        sb.append(", selfReviewCloseDate=").append(selfReviewCloseDate);
        sb.append(", closeDate=").append(closeDate);

        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewPeriod that = (ReviewPeriod) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(status, that.status) &&
                Objects.equals(reviewTemplateId, that.reviewTemplateId) && Objects.equals(selfReviewTemplateId, that.selfReviewTemplateId) &&
                Objects.equals(launchDate, that.launchDate) && Objects.equals(selfReviewCloseDate, that.selfReviewCloseDate) &&
                Objects.equals(closeDate, that.closeDate);
    }
}

package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Setter
@Getter
@Introspected
@NoArgsConstructor
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
    @Enumerated(EnumType.STRING)
    @Column(name = "review_status")
    @Schema(description = "The current status of the review period")
    private ReviewStatus reviewStatus;

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

    public ReviewPeriod(String name, ReviewStatus reviewStatus, @Nullable UUID reviewTemplateId, @Nullable UUID selfReviewTemplateId,
                        LocalDateTime launchDate, LocalDateTime selfReviewCloseDate, LocalDateTime closeDate) {
        this.name = name;
        this.reviewStatus = reviewStatus;
        this.reviewTemplateId = reviewTemplateId;
        this.selfReviewTemplateId = selfReviewTemplateId;
        this.launchDate = launchDate;
        this.selfReviewCloseDate = selfReviewCloseDate;
        this.closeDate = closeDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, reviewStatus, reviewTemplateId, selfReviewTemplateId, launchDate, selfReviewCloseDate, closeDate);
    }

    @Override
    public String toString() {
        return "ReviewPeriod{" + "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + reviewStatus +
                ", reviewTemplateId=" + reviewTemplateId +
                ", selfReviewTemplateId=" + selfReviewTemplateId +
                ", launchDate=" + launchDate +
                ", selfReviewCloseDate=" + selfReviewCloseDate +
                ", closeDate=" + closeDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewPeriod that = (ReviewPeriod) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(reviewStatus, that.reviewStatus) &&
                Objects.equals(reviewTemplateId, that.reviewTemplateId) && Objects.equals(selfReviewTemplateId, that.selfReviewTemplateId) &&
                Objects.equals(launchDate, that.launchDate) && Objects.equals(selfReviewCloseDate, that.selfReviewCloseDate) &&
                Objects.equals(closeDate, that.closeDate);
    }
}

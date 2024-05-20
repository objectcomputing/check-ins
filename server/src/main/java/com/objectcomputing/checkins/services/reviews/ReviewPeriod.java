package com.objectcomputing.checkins.services.reviews;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Schema(description = "The id of the review period")
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    @Schema(description = "The name of the review period")
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

    @Nullable
    @Column(name = "launch_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime launchDate;

    @Nullable
    @Column(name = "self_review_close_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime selfReviewCloseDate;

    @Nullable
    @Column(name = "close_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime closeDate;

    public ReviewPeriod(String name, ReviewStatus reviewStatus, @Nullable UUID reviewTemplateId,
                        @Nullable UUID selfReviewTemplateId, @Nullable LocalDateTime launchDate,
                        @Nullable LocalDateTime selfReviewCloseDate, @Nullable LocalDateTime closeDate) {
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

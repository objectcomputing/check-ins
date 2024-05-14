package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Setter
@Getter
@Introspected
@NoArgsConstructor
@Table(name = "review_assignments")
public class ReviewAssignment {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The id of the review assignment")
    private UUID id;

    public ReviewAssignment(UUID revieweeId,  @Nullable UUID reviewerId, UUID reviewPeriodId, @Nullable Boolean approved) {
        this.revieweeId = revieweeId;
        this.reviewerId = reviewerId;
        this.reviewPeriodId = reviewPeriodId;
        this.approved = approved;
    }

    @NotNull
    @Column(name = "reviewee_id")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The ID of the employee being reviewed")
    private UUID revieweeId;

    @Nullable
    @Column(name = "reviewer_id")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The ID of the employee conducting the review")
    private UUID reviewerId;

    @NotNull
    @Column(name = "review_period_id")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The ID of the review period that the assignment is related to")
    private UUID reviewPeriodId;

    @Nullable
    @Column(name = "approved")
    @Schema(description = "The status of the review assignment")
    private Boolean approved;

    @Override
    public int hashCode() {
        return Objects.hash(id, revieweeId, reviewerId, reviewPeriodId, approved);
    }

    @Override
    public String toString() {
        return "ReviewAssignment{" +
            "id=" + id +
            ", revieweeId=" + revieweeId +
            ", reviewerId=" + reviewerId +
            ", reviewPeriodId=" + reviewPeriodId +
            ", approved=" + approved +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewAssignment that = (ReviewAssignment) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(revieweeId, that.revieweeId) &&
            Objects.equals(reviewerId, that.reviewerId) &&
            Objects.equals(reviewPeriodId, that.reviewPeriodId) &&
            Objects.equals(approved, that.approved);
    }

}

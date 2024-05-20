package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Introspected
public class ReviewAssignmentDTO {

    @NotNull
    @Schema(description = "The ID of the employee being reviewed")
    private UUID revieweeId;

    @Nullable
    @Schema(description = "The ID of the employee conducting the review")
    private UUID reviewerId;

    @NotNull
    @Schema(description = "The ID of the review period that the assignment is related to")
    private UUID reviewPeriodId;

    @Nullable
    @Schema(description = "The status of the review assignment")
    private Boolean approved = false;

    public ReviewAssignment convertToEntity(){
        return new ReviewAssignment(this.revieweeId, this.reviewerId, this.reviewPeriodId, this.approved);
    }

}

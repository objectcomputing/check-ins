package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Introspected
public class ReviewPeriodCreateDTO {
    @NotBlank
    @Schema(required = true, description = "name of the review period")
    private String name;

    @NotNull
    @Schema(implementation = ReviewStatus.class, required = true, description = "the status of the review")
    private ReviewStatus reviewStatus;

    @Nullable
    private UUID reviewTemplateId;

    @Nullable
    private UUID selfReviewTemplateId;

    @Nullable
    private LocalDateTime launchDate;

    @Nullable
    private LocalDateTime selfReviewCloseDate;

    @Nullable
    private LocalDateTime closeDate;

    public ReviewPeriod convertToEntity(){
        return new ReviewPeriod(this.name, this.reviewStatus, this.reviewTemplateId,
                this.selfReviewTemplateId, this.launchDate, this.selfReviewCloseDate, this.closeDate);
    }
}

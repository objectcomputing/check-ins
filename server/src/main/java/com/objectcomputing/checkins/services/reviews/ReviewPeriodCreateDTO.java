package com.objectcomputing.checkins.services.reviews;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Introspected
public class ReviewPeriodCreateDTO {
    @NotBlank
    @Schema(description = "name of the review period")
    private String name;

    @NotNull
    @Schema(implementation = ReviewStatus.class, description = "the status of the review")
    private ReviewStatus reviewStatus;

    @Nullable
    private UUID reviewTemplateId;

    @Nullable
    private UUID selfReviewTemplateId;

    @Nullable
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime launchDate;

    @Nullable
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime selfReviewCloseDate;

    @Nullable
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime closeDate;

    public ReviewPeriod convertToEntity(){
        return new ReviewPeriod(this.name, this.reviewStatus, this.reviewTemplateId,
                this.selfReviewTemplateId, this.launchDate, this.selfReviewCloseDate, this.closeDate);
    }
}

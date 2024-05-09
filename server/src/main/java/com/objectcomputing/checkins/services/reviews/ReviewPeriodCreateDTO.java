package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Introspected
public class ReviewPeriodCreateDTO {
    @NotBlank
    @Schema(required = true, description = "name of the review period")
    private String name;

    @NotNull
    @Schema(required = true, description = "whether the review is open")
    private Boolean open;

    private UUID reviewTemplateId;

    private UUID selfReviewTemplateId;

    private LocalDateTime launchDate;

    private LocalDateTime selfReviewCloseDate;

    private LocalDateTime closeDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public UUID getReviewTemplateId() {
        return reviewTemplateId;
    }

    public void setReviewTemplateId(UUID reviewTemplateId) { this.reviewTemplateId = reviewTemplateId; }

    public UUID getSelfReviewTemplateId() { return selfReviewTemplateId; }

    public void setSelfReviewTemplateId(UUID selfReviewTemplateId) { this.selfReviewTemplateId = selfReviewTemplateId; }

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
}

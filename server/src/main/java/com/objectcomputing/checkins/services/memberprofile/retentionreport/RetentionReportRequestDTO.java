package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Introspected
public class RetentionReportRequestDTO {

    @NotNull
    @Schema(description = "The start date for the retention report")
    private LocalDate startDate;

    @NotNull
    @Schema(description = "The end date for the retention report")
    private LocalDate endDate;

    @Nullable
    @Schema(description = "The frequency for the retention report")
    private String frequency;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Nullable
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(@Nullable String frequency) {
        this.frequency = frequency;
    }
}

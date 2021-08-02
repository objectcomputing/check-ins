package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Introspected
public class RetentionReportDTO {
    @NotNull
    @Schema(description = "The start date for the retention report")
    private LocalDate startDate;

    @NotNull
    @Schema(description = "The end date for the retention report")
    private LocalDate endDate;

    @Nullable
    @Schema(description = "The frequency for the retention report")
    private FrequencyType frequency;

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
    public FrequencyType getFrequency() {
        return frequency;
    }

    public void setFrequency(@Nullable FrequencyType frequency) {
        this.frequency = frequency;
    }
}

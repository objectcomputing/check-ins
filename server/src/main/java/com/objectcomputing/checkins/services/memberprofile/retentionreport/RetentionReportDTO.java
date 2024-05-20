package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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

}

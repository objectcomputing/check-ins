package com.objectcomputing.checkins.services.reports;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class ReportDataDTO {
    @NotNull
    private List<UUID> memberIds;

    @NotNull
    private UUID reviewPeriodId;
}

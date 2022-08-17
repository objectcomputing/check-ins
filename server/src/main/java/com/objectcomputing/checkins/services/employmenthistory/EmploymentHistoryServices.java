package com.objectcomputing.checkins.services.employmenthistory;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Nullable;

public interface EmploymentHistoryServices {
    EmploymentHistory getById(UUID id);

    EmploymentHistory saveHistory(EmploymentHistory employmentHistory);

    Set<EmploymentHistory> findByValues(
            @Nullable UUID id,
            @Nullable String company,
            @Nullable String companyAddress,
            @Nullable String jobTitle,
            @Nullable LocalDate startDate,
            @Nullable LocalDate endDate,
            @Nullable String reason);

    Object deleteHistory(@NotNull UUID id);
}

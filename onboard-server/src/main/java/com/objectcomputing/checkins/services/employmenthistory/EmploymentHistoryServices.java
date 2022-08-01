package com.objectcomputing.checkins.services.employmenthistory;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import io.micronaut.core.annotation.Nullable;

public interface EmploymentHistoryServices {
    EmploymentHistory getById(UUID id);

    Set<EmploymentHistory> findByValues(
            @Nullable UUID id,
            @Nullable String company,
            @Nullable String companyAddress,
            @Nullable String jobTitle,
            @Nullable LocalDate startDate,
            @Nullable LocalDate endDate,
            @Nullable String reason);
}

package com.objectcomputing.checkins.services.education;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import io.micronaut.core.annotation.Nullable;

public interface EducationServices {
    Education getById(UUID id);

    Set<Education> findByValues(
            @Nullable UUID id,
            @Nullable String highestDegree,
            @Nullable String institution,
            @Nullable String location,
            @Nullable String degree,
            @Nullable String major,
            @Nullable LocalDate completionDate,
            @Nullable String additionalInfo);
}

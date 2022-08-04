package com.objectcomputing.checkins.services.WorkPreference;

import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WorkPreferenceServices {
    WorkPreference getById(UUID id);

    Set<WorkPreference> findByValues (String desiredPosition, LocalDate desiredStartDate, Boolean currentlyEmployed,
                                      @Nullable String referredBy, @Nullable String referrerEmail);

    WorkPreference savePreferences(WorkPreference workPreference);

    Boolean deletePreferences(UUID id);

    WorkPreference findByPosition(@NotNull String desiredPosition);

    WorkPreference findByReferral(@Nullable String referredBy);

    List<WorkPreference> findAll();

}

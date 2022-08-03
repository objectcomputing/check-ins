package com.objectcomputing.checkins.services.WorkPreference;

import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface WorkPreferencesServices {

    Set<WorkPreference> findByValues (String desiredPosition, LocalDate desiredStartDate, Boolean currentlyEmployed,
                                      @Nullable String referredBy, @Nullable String referrerEmail);

    WorkPreference findByPosition(@NotNull String desiredPosition);

    WorkPreference findByReferral(@Nullable String referredBy);

    List<WorkPreference> findAll();

}

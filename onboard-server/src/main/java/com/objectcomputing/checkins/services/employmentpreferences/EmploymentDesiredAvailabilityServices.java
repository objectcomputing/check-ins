package com.objectcomputing.checkins.services.employmentpreferences;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EmploymentDesiredAvailabilityServices {
    EmploymentDesiredAvailability getById(UUID id);

    Set<EmploymentDesiredAvailability> findByValues (String desiredPosition, LocalDate desiredStartDate, Boolean currentlyEmployed);

    EmploymentDesiredAvailability savePreferences(EmploymentDesiredAvailability workPreference);

    Boolean deletePreferences(UUID id);

    EmploymentDesiredAvailability findByPosition(@NotNull String desiredPosition);

    List<EmploymentDesiredAvailability> findAll();
}

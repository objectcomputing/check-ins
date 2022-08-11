package com.objectcomputing.checkins.services.employmentpreferences;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EmploymentDesiredAvailabilityServices {
    EmploymentDesiredAvailability getById(UUID id);

    Set<EmploymentDesiredAvailability> findByValues (UUID id, String desiredPosition, LocalDate desiredStartDate,
                                                     String desiredSalary, Boolean currentlyEmployed, Boolean contactCurrentEmployer,
                                                     Boolean previousEmploymentOCI, Boolean noncompeteAgreement, LocalDate noncompeteExpirationDate);

    EmploymentDesiredAvailability savePreferences(EmploymentDesiredAvailability employmentDesiredAvailabilityPreferences);

    EmploymentDesiredAvailability saveDesiredAvailability(EmploymentDesiredAvailability employmentDesiredAvailability);

    Boolean deletePreferences(UUID id);

    EmploymentDesiredAvailability findByPosition(@NotNull String desiredPosition);

    List<EmploymentDesiredAvailability> findAll();
}

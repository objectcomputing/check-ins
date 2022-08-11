package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.employmentpreferences.EmploymentDesiredAvailability;

import java.time.LocalDate;

public interface EmploymentPreferencesFixture extends RepositoryFixture {
    LocalDate date = LocalDate.of(2020, 1, 8);

    default EmploymentDesiredAvailability createADefaultEmploymentPreferences() {
        return getEmploymentDesiredAvailabilityRepository().save(new EmploymentDesiredAvailability("Boss", date, "100",
                true, true, true, true, date));
    }

    default EmploymentDesiredAvailability createSecondDefaultEmploymentPreferences() {
        return getEmploymentDesiredAvailabilityRepository().save(new EmploymentDesiredAvailability("Worker", date, "10",
                false, false, false, false, date));
    }
}

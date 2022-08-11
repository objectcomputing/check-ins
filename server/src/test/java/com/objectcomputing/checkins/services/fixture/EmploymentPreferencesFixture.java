package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.employmentpreferences.EmploymentDesiredAvailability;

import java.time.LocalDate;

public interface EmploymentPreferencesFixture extends RepositoryFixture {
    LocalDate date = LocalDate.of(2020, 1, 8);

    default EmploymentDesiredAvailability createADefaultEmploymentPreferences() {
        return getEmploymentDesiredA().save(new EmploymentDesiredAvailability("Boss", date, "100",
                true, true, true, true, date));
    }
}

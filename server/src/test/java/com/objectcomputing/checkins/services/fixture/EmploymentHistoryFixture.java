package com.objectcomputing.checkins.services.fixture;

import java.time.LocalDate;

import com.objectcomputing.checkins.services.employmenthistory.EmploymentHistory;

public interface EmploymentHistoryFixture extends RepositoryFixture {
    default EmploymentHistory createEmploymentHistory() {
        return getEmploymentHistoryRepository().save(new EmploymentHistory("OCI", "12140 Woodcrest Executive Dr", 
                                                "Intern", LocalDate.of(2022, 6, 9), LocalDate.now(), "Internship ended"));
    }
}

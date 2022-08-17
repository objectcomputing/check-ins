package com.objectcomputing.checkins.services.fixture;

import java.time.LocalDate;

import com.objectcomputing.checkins.services.education.Education;

public interface EducationFixture extends RepositoryFixture {
    default Education createEducation() {
        return getEducationRepository().save(new Education("Masters", "UMSL", "St. Louis", 
                                                    "Masters", LocalDate.now(), "Comp Sci", "Hello :)"));
    }
}

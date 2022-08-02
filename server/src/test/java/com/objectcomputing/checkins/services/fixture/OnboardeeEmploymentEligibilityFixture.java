package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboardee_employment_eligibility.OnboardeeEmploymentEligibility;

import java.time.LocalDate;

public interface OnboardeeEmploymentEligibilityFixture extends RepositoryFixture{

    default OnboardeeEmploymentEligibility createADefaultOnboardeeEmploymentEligibility() {
        return getOnboardeeEmploymentEligibilityRepository().save(new OnboardeeEmploymentEligibility(true,true,"F-1",LocalDate.now(),false,""));
    }

}

package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;
import com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility.OnboardeeEmploymentEligibility;

import java.time.LocalDate;

public interface OnboardeeEmploymentEligibilityFixture extends RepositoryFixture {

    default OnboardeeEmploymentEligibility createADefaultOnboardeeEmploymentEligibility(BackgroundInformation backgroundInformation) {

        return getOnboardeeEmploymentEligibilityRepository().save(new OnboardeeEmploymentEligibility(true, true, "F-1", LocalDate.now(), false, "nope"));
    }

    default OnboardeeEmploymentEligibility createADefaultOnboardeeEmploymentEligibility2(BackgroundInformation backgroundInformation) {
        return getOnboardeeEmploymentEligibilityRepository().save(new OnboardeeEmploymentEligibility(true,true,"F-1",LocalDate.now(),true,"Yes felony"));
    }
}

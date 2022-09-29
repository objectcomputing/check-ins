package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;
import com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility.OnboardeeEmploymentEligibility;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface OnboardeeEmploymentEligibilityFixture extends RepositoryFixture {

    default OnboardeeEmploymentEligibility createADefaultOnboardeeEmploymentEligibility(BackgroundInformation backgroundInformation) {
        NewHireAccountEntity newHireAccountEntity = new NewHireAccountEntity();
        return getOnboardeeEmploymentEligibilityRepository().save(new OnboardeeEmploymentEligibility(newHireAccountEntity,true, true, "F-1", LocalDate.now(), false, "nope"));
    }

    default OnboardeeEmploymentEligibility createADefaultOnboardeeEmploymentEligibility2(BackgroundInformation backgroundInformation) {
        NewHireAccountEntity newHireAccountEntity = new NewHireAccountEntity();
        return getOnboardeeEmploymentEligibilityRepository().save(new OnboardeeEmploymentEligibility(newHireAccountEntity,true,true,"F-1",LocalDate.now(),true,"Yes felony"));
    }
}

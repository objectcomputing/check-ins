package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboardeeprofile.Onboarding_Profile;

import java.time.LocalDate;

public interface OnboardingFixture extends RepositoryFixture {

    default Onboarding_Profile createADefaultOnboardeeProfile() {
        return getOnboardingProfileRepository().save(new Onboarding_Profile("Bill", null, "Charles",
                100935009, LocalDate.now(),"5000 Main Street" , "4351 Wyoming St.",
                "9193330000", "8322933423"));
        }
    default Onboarding_Profile createSecondOnboardeeProfile() {
        return getOnboardingProfileRepository().save(new Onboarding_Profile("Bill", null, "Charles",
                500737100, LocalDate.now(),"49 Sherwood Harbor Rd" , "3456 Crittenden St.",
                "7043330000", "3142933423"));
        }
    }
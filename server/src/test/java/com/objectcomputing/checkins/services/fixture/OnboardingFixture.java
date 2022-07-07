package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboardeeprofile.Onboarding_Profile;

import java.time.LocalDate;

public interface OnboardingFixture extends RepositoryFixture {

    default Onboarding_Profile createADefaultOnboardeeProfile() {
        return getOnboardingProfileRepository().save(new Onboarding_Profile("Bill", null, "Charles",
                100935009, LocalDate.now(),"5000 Main Street" , "New York, New York",
                "3142933423", "3142933423"));

        }
    }
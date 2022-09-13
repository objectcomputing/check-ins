package com.objectcomputing.checkins.services.fixture;
import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;
import com.objectcomputing.checkins.services.onboard.onboardeeprofile.OnboardingProfile;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface OnboardingFixture extends RepositoryFixture {
    default OnboardingProfile createADefaultOnboardeeProfile(BackgroundInformation backgroundInformation) {
        return getOnboardingProfileRepository().save(new OnboardingProfile(null,"Bill", null , "Charles",
                "100935009", LocalDate.now(),"5000 Main Street" , "4351 Wyoming St.",
                "9193330000", "8322933423", "bill.Charles@yahoo.com"));
        }
    default OnboardingProfile createSecondOnboardeeProfile(BackgroundInformation backgroundInformation) {
        return getOnboardingProfileRepository().save(new OnboardingProfile(null,"Diane", null, "Fredrickson",
                "500737100", LocalDate.now(),"49 Sherwood Harbor Rd" , "3456 Crittenden St.",
                "7043330000", "3142933423", ""));
        }
    }
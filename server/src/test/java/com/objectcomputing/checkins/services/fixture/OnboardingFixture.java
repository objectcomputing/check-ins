package com.objectcomputing.checkins.services.fixture;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfile;
import java.time.LocalDate;

public interface OnboardingFixture extends RepositoryFixture {
    default OnboardingProfile createADefaultOnboardeeProfile() {
        return getOnboardingProfileRepository().save(new OnboardingProfile("Bill", null, "Charles",
                100935009, LocalDate.now(),"5000 Main Street" , "4351 Wyoming St.",
                "9193330000", "8322933423", "bill.Charles@yahoo.com"));
        }
    default OnboardingProfile createSecondOnboardeeProfile() {
        return getOnboardingProfileRepository().save(new OnboardingProfile("Diane", null, "Fredrickson",
                500737100, LocalDate.now(),"49 Sherwood Harbor Rd" , "3456 Crittenden St.",
                "7043330000", "3142933423", ""));
        }
    }
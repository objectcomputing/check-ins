package com.objectcomputing.checkins.services.onboard.onboardeeprofile;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public interface OnboardingProfileServices {
    OnboardingProfile getById(UUID id);

    OnboardingProfile saveProfile(OnboardingProfileCreateDTO onboardingProfileCreateDTO);

    OnboardingProfile updateProfile(OnboardingProfileDTO onboardingProfileDTO);

    Boolean deleteProfile(UUID id);

    OnboardingProfile findByName(@NotNull String firstName, @NotNull String lastName);
}

package com.objectcomputing.checkins.services.onboardeeprofile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OnboardingProfileServices {
    Onboarding_profile getById(UUID id);

    Set<Onboarding_profile> findByValues (String firstName, String lastName, Integer socialSecurityNumber,
                                          UUID id);

    Onboarding_profile saveProfile(Onboarding_profile onboardingProfile);

    Boolean deleteProfile(UUID id);

    Onboarding_profile findByName(@NotNull String firstName, @NotNull String lastName);

    List<Onboarding_profile> findAll();

}

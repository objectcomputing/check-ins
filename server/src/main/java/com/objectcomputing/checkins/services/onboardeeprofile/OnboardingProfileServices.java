package com.objectcomputing.checkins.services.onboardeeprofile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OnboardingProfileServices {
    Onboarding_Profile getById(UUID id);

    Set<Onboarding_Profile> findByValues (UUID id, String firstName, String lastName, Integer socialSecurityNumber,
                                          LocalDate birthDate, Integer phoneNumber);

    Onboarding_Profile saveProfile(Onboarding_Profile onboardingProfile);

    Boolean deleteProfile(UUID id);

    Onboarding_Profile findByName(@NotNull String firstName, @NotNull String lastName);

    List<Onboarding_Profile> findAll();

}

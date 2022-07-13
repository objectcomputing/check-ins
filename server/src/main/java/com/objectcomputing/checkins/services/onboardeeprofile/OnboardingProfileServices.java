package com.objectcomputing.checkins.services.onboardeeprofile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OnboardingProfileServices {
    OnboardingProfile getById(UUID id);

    Set<OnboardingProfile> findByValues (UUID id, String firstName, String lastName, String socialSecurityNumber,
                                         LocalDate birthDate, String phoneNumber, String personalEmail);

    OnboardingProfile saveProfile(OnboardingProfile onboardingProfile);

    Boolean deleteProfile(UUID id);

    OnboardingProfile findByName(@NotNull String firstName, @NotNull String lastName);

    List<OnboardingProfile> findAll();

}

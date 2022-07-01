package com.objectcomputing.checkins.services.onboardeeprofile;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class OnboardingProfileServicesImpl implements OnboardingProfileServices  {
    private final OnboardingProfileRepository onboardingProfileRepository;

    public OnboardingProfileServicesImpl(OnboardingProfileRepository onboardingProfileRepository) {
        this.onboardingProfileRepository = onboardingProfileRepository;
    }

    @Override
    public Onboarding_profile getByID(@NotNull UUID id) {
        Optional<Onboarding_profile> onboardingProfile = onboardingProfileRepository.findById(id);
        if (onboardingProfile.isEmpty()) {
            throw new NotFoundException("No new employee profile for id" + id);
        }
        return onboardingProfile.get();
    }

    @Override
    public Set<Onboarding_profile> findByValues (
            @Nullable UUID id,
            @Nullable String firstName,
            @Nullable String middleName,
            @Nullable String lastName,
            @Nullable Integer socialSecurityNumber,
            @Nullable Date birthDate,
            @Nullable String currentAddress,
            @Nullable String previousAddress,
            @Nullable Integer phoneNumber,
            @Nullable Integer secondPhoneNumber) {
        HashSet<Onboarding_profile> onboarding_profiles = new HashSet<>(onboardingProfileRepository.search((firstName, null, lastName, null, middleName,
                nullSafeUUIDToString(id),socialSecurityNumber, birthDate, currentAddress, previousAddress, phoneNumber, secondPhoneNumber));

        return onboarding_profiles;
    }


}

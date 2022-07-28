package com.objectcomputing.checkins.services.onboardeeprofile;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@Singleton
public class OnboardingProfileServicesImpl implements OnboardingProfileServices  {

    private final OnboardingProfileRepository onboardingProfileRepository;
    public OnboardingProfileServicesImpl(OnboardingProfileRepository onboardingProfileRepository) {
        this.onboardingProfileRepository = onboardingProfileRepository;
    }

    @Override
    public OnboardingProfile getById(@NotNull UUID id) {
        Optional<OnboardingProfile> onboardingProfile = onboardingProfileRepository.findById(id);
        if (onboardingProfile.isEmpty()) {
            throw new NotFoundException("No new employee profile for id " + id);
        }
        return onboardingProfile.get();
    }
    @Override
    public Set<OnboardingProfile> findByValues (
            @Nullable UUID id,
            @Nullable String firstName,
            @Nullable String lastName,
            @Nullable String socialSecurityNumber,
            @Nullable LocalDate birthDate,
            @Nullable String phoneNumber,
            @Nullable String personalEmail) {
        HashSet<OnboardingProfile> onboarding_profiles = new HashSet<>(onboardingProfileRepository.search( (nullSafeUUIDToString(id)), firstName, null, lastName,
                socialSecurityNumber,  birthDate,null, null, phoneNumber, null, personalEmail));

        return onboarding_profiles;
    }

    @Override
    public OnboardingProfile saveProfile(OnboardingProfile onboarding_profile) {
        OnboardingProfile employeeSocialSecurityNumber = onboardingProfileRepository.findBySocial(onboarding_profile.getSocialSecurityNumber()).orElse(null);
        if (employeeSocialSecurityNumber != null && employeeSocialSecurityNumber.getId() != null && !Objects.equals(onboarding_profile.getId(), employeeSocialSecurityNumber.getId())) {
            throw new AlreadyExistsException(String.format("Onboardee SSN already exists in database",
                    onboarding_profile.getSocialSecurityNumber()));
        }
        if (onboarding_profile.getId() == null) {
            return onboardingProfileRepository.save(onboarding_profile);
        }
        return onboardingProfileRepository.update(onboarding_profile);
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id) {
        onboardingProfileRepository.deleteById(id);
        return true;
    }

    @Override
    public OnboardingProfile findByName(String firstName, String lastName) {
        List<OnboardingProfile> searchResult = onboardingProfileRepository.search(null, firstName, null, lastName,
                null, null, null, null, null, null, null);
        if (searchResult.size() != 1) {
            throw new BadArgException("Expected exactly 1 result. Found " + searchResult.size());
        }
        return searchResult.get(0);
    }

    @Override
    public List<OnboardingProfile> findAll() {
        return onboardingProfileRepository.findAll();
    }
}

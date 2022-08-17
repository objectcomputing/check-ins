package com.objectcomputing.checkins.services.onboard.onboardeeprofile;

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
    public OnboardingProfile saveProfile(String accountEmail, OnboardingProfile onboarding_profile) {
        Mono<NewHireAccountEntity> accountEntity = newHireAccountRepository.findByEmailAddress(accountEmail);
        LOG.info(
                accountEntity.toString()
        );
//        LOG.info(
//                NewHireAccountRepository.findByEmailAddress(onboarding_profile.getPersonalEmail())
//        );
//        if (newHireAccountEntity.getId() != null) {
            if (onboarding_profile.getId() == null) {
                return onboardingProfileRepository.save(onboarding_profile);
            }
            return onboardingProfileRepository.update(onboarding_profile);
//        } else {
//            throw new AlreadyExistsException(String.format("New Hire account does not exist in database",
//                    newHireAccountEntity.getEmailAddress()));
//        }
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

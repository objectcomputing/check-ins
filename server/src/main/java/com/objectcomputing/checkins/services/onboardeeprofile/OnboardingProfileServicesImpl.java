package com.objectcomputing.checkins.services.onboardeeprofile;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;

import javax.validation.constraints.NotNull;
import java.util.*;


@Singleton
public class OnboardingProfileServicesImpl implements OnboardingProfileServices  {
    private final OnboardingProfileRepository onboardingProfileRepository;

    public OnboardingProfileServicesImpl(OnboardingProfileRepository onboardingProfileRepository) {
        this.onboardingProfileRepository = onboardingProfileRepository;
    }

//    @Override
    public Onboarding_profile getByID(@NotNull UUID id) {
        Optional<Onboarding_profile> onboardingProfile = onboardingProfileRepository.findById(id);
        if (onboardingProfile.isEmpty()) {
            throw new NotFoundException("No new employee profile for id" + id);
        }
        return onboardingProfile.get();
    }

//    @Override
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

    @Override
    public Onboarding_profile saveProfile(Onboarding_profile onboarding_profile) {
        Onboarding_profile employeeSocialSecurityNumber = onboardingProfileRepository.findBySocial(onboarding_profile.socialSecurityNumber()).orElse(null);

        if (employeeSocialSecurityNumber != null && employeeSocialSecurityNumber.getId() != null && !Objects.equals(onboarding_profile.getId(), employeeSocialSecurityNumber.getId())) {
            throw new AlreadyExistsException(String.format("Employee SSN already exists in database",
                    onboarding_profile.getSocialSecurityNumber()));
        }

        if (onboarding_profile.getId() == null) {
            return onboardingProfileRepository.save(onboarding_profile);
        }

        return onboardingProfileRepository.update(onboarding_profile);
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id) {
        memberProfileRepository.deleteById(id);
        return true;
    }





}

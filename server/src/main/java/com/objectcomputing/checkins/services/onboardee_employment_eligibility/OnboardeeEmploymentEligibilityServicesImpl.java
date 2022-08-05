package com.objectcomputing.checkins.services.onboardee_employment_eligibility;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfile;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileRepository;
import jakarta.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class OnboardeeEmploymentEligibilityServicesImpl implements OnboardeeEmploymentEligibilityServices {
    private final OnboardeeEmploymentEligibilityRepository onboardeeEmploymentEligibilityRepository;

    public OnboardeeEmploymentEligibilityServicesImpl(OnboardeeEmploymentEligibilityRepository onboardeeEmploymentEligibilityRepository) {
        this.onboardeeEmploymentEligibilityRepository = onboardeeEmploymentEligibilityRepository;
    }

    @Override
    public OnboardeeEmploymentEligibility getById(@NotNull UUID id) {
        Optional<OnboardeeEmploymentEligibility> onboardeeEmploymentEligibility = onboardeeEmploymentEligibilityRepository.findById(id);
        if (onboardeeEmploymentEligibility.isEmpty()) {
            throw new NotFoundException("No new employee employment eligibility information for id " + id);
        }
        return onboardeeEmploymentEligibility.get();
    }

    @Override
    public Set<OnboardeeEmploymentEligibility> findByValues(
            @Nullable UUID id,
            @Nullable Boolean ageLegal,
            @Nullable Boolean usCitizen,
            @Nullable String visaStatus,
            @Nullable LocalDate expirationDate,
            @Nullable Boolean felonyStatus,
            @Nullable String felonyExplanation) {
        HashSet<OnboardeeEmploymentEligibility> onboardee_employment_eligibility = new HashSet<>(onboardeeEmploymentEligibilityRepository.search((nullSafeUUIDToString(id)), ageLegal,
                usCitizen, null, null, felonyStatus, null));

        return onboardee_employment_eligibility;
    }
    //implement other methods as well
    @Override
    public OnboardeeEmploymentEligibility saveProfile (OnboardeeEmploymentEligibility onboardeeEmploymentEligibility){
        if (onboardeeEmploymentEligibility.getId() != null){
            throw new AlreadyExistsException(String.format("Onboardee already exists in database"));
        }
        if (onboardeeEmploymentEligibility.getId() == null) {
            return onboardeeEmploymentEligibilityRepository.save(onboardeeEmploymentEligibility);
        }
        return onboardeeEmploymentEligibilityRepository.update(onboardeeEmploymentEligibility);
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id) {
        onboardeeEmploymentEligibilityRepository.deleteById(id);
        return true;
    }

    @Override
    public List<OnboardeeEmploymentEligibility> findAll() {
        return onboardeeEmploymentEligibilityRepository.findAll();
    }
}

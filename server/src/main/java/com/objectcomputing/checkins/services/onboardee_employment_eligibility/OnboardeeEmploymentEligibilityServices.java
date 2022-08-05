package com.objectcomputing.checkins.services.onboardee_employment_eligibility;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OnboardeeEmploymentEligibilityServices {
    OnboardeeEmploymentEligibility getById(UUID id);
    Set<OnboardeeEmploymentEligibility> findByValues (UUID id, Boolean ageLegal, Boolean usCitizen, String visaStatus,
                                         LocalDate expirationDate, Boolean felonyStatus, String felonyExplanation);

    OnboardeeEmploymentEligibility saveProfile(OnboardeeEmploymentEligibility onboardeeEmploymentEligibility);

    Boolean deleteProfile(UUID id);

    List<OnboardeeEmploymentEligibility> findAll();
}
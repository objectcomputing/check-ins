package com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OnboardeeEmploymentEligibilityServices {
    OnboardeeEmploymentEligibility getById(UUID id);
    Set<OnboardeeEmploymentEligibility> findByValues (UUID id, Boolean ageLegal, Boolean usCitizen, String visaStatus,
                                         LocalDate expirationDate, Boolean felonyStatus, String felonyExplanation, UUID backgroundId);

    OnboardeeEmploymentEligibility saveProfile(OnboardeeEmploymentEligibility onboardeeEmploymentEligibility);

    Boolean deleteProfile(UUID id);

    List<OnboardeeEmploymentEligibility> findAll();
}
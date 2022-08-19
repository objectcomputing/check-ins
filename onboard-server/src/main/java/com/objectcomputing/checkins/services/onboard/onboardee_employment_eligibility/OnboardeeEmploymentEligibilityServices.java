package com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility;

import java.util.List;
import java.util.UUID;

public interface OnboardeeEmploymentEligibilityServices {
    OnboardeeEmploymentEligibility getById(UUID id);

    OnboardeeEmploymentEligibility saveProfile(OnboardeeEmploymentEligibilityCreateDTO onboardeeEmploymentEligibilityCreateDTO);
    OnboardeeEmploymentEligibility updateProfile(OnboardeeEmploymentEligibilityDTO onboardeeEmploymentEligibilityDTO);

    Boolean deleteProfile(UUID id);

    List<OnboardeeEmploymentEligibility> findAll();
}
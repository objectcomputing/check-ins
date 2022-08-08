package com.objectcomputing.checkins.services.onboardee_employment_eligibility;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnboardeeEmploymentEligibilityTestUtil {
    public static OnboardeeEmploymentEligibilityCreateDTO mkCreateOnboardeeEmploymentEligibilityDTO() {
        OnboardeeEmploymentEligibilityCreateDTO dto = new OnboardeeEmploymentEligibilityCreateDTO();
        dto.setAgeLegal(true);
        dto.setUsCitizen(true);
        dto.setVisaStatus("F-1");
        dto.setExpirationDate(LocalDate.now());
        dto.setFelonyStatus(false);
        dto.setFelonyExplanation("Say No to Felony");
        return dto;
    }

    public static OnboardeeEmploymentEligibilityResponseDTO mkUpdateOnboardeeEmploymentEligibilityResponseDTO() {
        OnboardeeEmploymentEligibilityResponseDTO dto = new OnboardeeEmploymentEligibilityResponseDTO();
        dto.setAgeLegal(true);
        dto.setUsCitizen(true);
        dto.setVisaStatus("F-1");
        dto.setExpirationDate(LocalDate.now());
        dto.setFelonyStatus(false);
        dto.setFelonyExplanation("Say No to Felony");
        return dto;
    }

    public static OnboardeeEmploymentEligibility mkOnboardee_Employment_Eligibility(String seed) {
        return new OnboardeeEmploymentEligibility(true, true, "F-1" + seed, LocalDate.now(), false, "Say No to Felony" + seed);
    }

    public static OnboardeeEmploymentEligibility mkOnboardee_Employment_Eligibility() {
        return mkOnboardee_Employment_Eligibility("");
    }

    public static void assetProfilesEqual(OnboardeeEmploymentEligibility entity, OnboardeeEmploymentEligibilityResponseDTO dto) {
        assertEquals(entity.getAgeLegal(), dto.getAgeLegal());
        assertEquals(entity.getUsCitizen(), dto.getUsCitizen());
        assertEquals(entity.getVisaStatus(), dto.getVisaStatus());
        assertEquals(entity.getExpirationDate(), dto.getExpirationDate());
        assertEquals(entity.getFelonyStatus(), dto.getFelonyStatus());
        assertEquals(entity.getFelonyExplanation(), dto.getFelonyExplanation());
    }

    public static void assetProfilesEqual(OnboardeeEmploymentEligibilityCreateDTO entity, OnboardeeEmploymentEligibilityResponseDTO dto) {
        assertEquals(entity.getAgeLegal(), dto.getAgeLegal());
        assertEquals(entity.getUsCitizen(), dto.getUsCitizen());
        assertEquals(entity.getVisaStatus(), dto.getVisaStatus());
        assertEquals(entity.getExpirationDate(), dto.getExpirationDate());
        assertEquals(entity.getFelonyStatus(), dto.getFelonyStatus());
        assertEquals(entity.getFelonyExplanation(), dto.getFelonyExplanation());
    }

    public static OnboardeeEmploymentEligibilityResponseDTO toDto(OnboardeeEmploymentEligibility entity) {
        OnboardeeEmploymentEligibilityResponseDTO dto = new OnboardeeEmploymentEligibilityResponseDTO();
        dto.setId(entity.getId());
        dto.setAgeLegal(entity.getAgeLegal());
        dto.setUsCitizen(entity.getUsCitizen());
        dto.setVisaStatus(entity.getVisaStatus());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setFelonyStatus(entity.getFelonyStatus());
        dto.setFelonyExplanation(entity.getFelonyExplanation());
        return dto;
    }
}

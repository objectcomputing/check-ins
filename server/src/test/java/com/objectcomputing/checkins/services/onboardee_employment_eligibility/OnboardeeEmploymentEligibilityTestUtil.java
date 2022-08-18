package com.objectcomputing.checkins.services.onboardee_employment_eligibility;

import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;
import com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility.OnboardeeEmploymentEligibility;
import com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility.OnboardeeEmploymentEligibilityCreateDTO;
import com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility.OnboardeeEmploymentEligibilityDTO;

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

    public static OnboardeeEmploymentEligibilityDTO mkUpdateOnboardeeEmploymentEligibilityResponseDTO(BackgroundInformation backgroundInformation) {
        OnboardeeEmploymentEligibilityDTO dto = new OnboardeeEmploymentEligibilityDTO();
        dto.setAgeLegal(true);
        dto.setUsCitizen(true);
        dto.setVisaStatus("F-1");
        dto.setExpirationDate(LocalDate.now());
        dto.setFelonyStatus(false);
        dto.setFelonyExplanation("Say No to Felony");
        return dto;
    }

    public static OnboardeeEmploymentEligibility mkOnboardee_Employment_Eligibility(String seed, BackgroundInformation backgroundInformation) {
        return new OnboardeeEmploymentEligibility(true, true, "F-1" + seed, LocalDate.now(), false, "Say No to Felony" + seed);
    }

    public static void assetProfilesEqual(OnboardeeEmploymentEligibility entity, OnboardeeEmploymentEligibilityDTO dto) {
        assertEquals(entity.getAgeLegal(), dto.getAgeLegal());
        assertEquals(entity.getUsCitizen(), dto.getUsCitizen());
        assertEquals(entity.getVisaStatus(), dto.getVisaStatus());
        assertEquals(entity.getExpirationDate(), dto.getExpirationDate());
        assertEquals(entity.getFelonyStatus(), dto.getFelonyStatus());
        assertEquals(entity.getFelonyExplanation(), dto.getFelonyExplanation());
    }

    public static void assetProfilesEqual(OnboardeeEmploymentEligibilityCreateDTO entity, OnboardeeEmploymentEligibilityDTO dto) {
        assertEquals(entity.getAgeLegal(), dto.getAgeLegal());
        assertEquals(entity.getUsCitizen(), dto.getUsCitizen());
        assertEquals(entity.getVisaStatus(), dto.getVisaStatus());
        assertEquals(entity.getExpirationDate(), dto.getExpirationDate());
        assertEquals(entity.getFelonyStatus(), dto.getFelonyStatus());
        assertEquals(entity.getFelonyExplanation(), dto.getFelonyExplanation());
    }

    public static OnboardeeEmploymentEligibilityDTO toDto(OnboardeeEmploymentEligibility entity) {
        OnboardeeEmploymentEligibilityDTO dto = new OnboardeeEmploymentEligibilityDTO();
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

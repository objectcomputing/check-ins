package com.objectcomputing.checkins.services.onboardee_about;

import com.objectcomputing.checkins.services.onboard.onboardee_about.OnboardeeAbout;
import com.objectcomputing.checkins.services.onboard.onboardee_about.OnboardeeAboutDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnboardeeAboutTestUtil {
    public static OnboardeeAboutDTO mkUpdateOnboardeeAbout() {
        OnboardeeAboutDTO dto = new OnboardeeAboutDTO();
        dto.setTshirtSize("M");
        dto.setGoogleTraining("Gmail");
        dto.setIntroduction("Hello :)");
        dto.setVaccineStatus(true);
        dto.setVaccineTwoWeeks(true);
        dto.setOtherTraining("No");
        dto.setAdditionalSkills("Maybe");
        dto.setCertifications("Yes");
        return dto;
    }
    
    public static void assertAboutEquals(OnboardeeAbout entity, OnboardeeAboutDTO dto) {
        assertEquals(entity.getTshirtSize(), dto.getTshirtSize());
        assertEquals(entity.getGoogleTraining(), dto.getGoogleTraining());
        assertEquals(entity.getIntroduction(), dto.getIntroduction());
        assertEquals(entity.getVaccineStatus(), dto.getVaccineStatus());
        assertEquals(entity.getVaccineTwoWeeks(), dto.getVaccineTwoWeeks());
        assertEquals(entity.getOtherTraining(), dto.getOtherTraining());
        assertEquals(entity.getAdditionalSkills(), dto.getAdditionalSkills());
        assertEquals(entity.getCertifications(), dto.getCertifications());
    }

    public static OnboardeeAboutDTO toDto(OnboardeeAbout entity) {
        OnboardeeAboutDTO dto = new OnboardeeAboutDTO();
        dto.setId(entity.getId());
        dto.setTshirtSize(entity.getTshirtSize());
        dto.setGoogleTraining(entity.getGoogleTraining());
        dto.setIntroduction(entity.getIntroduction());
        dto.setVaccineStatus(entity.getVaccineStatus());
        dto.setVaccineTwoWeeks(entity.getVaccineTwoWeeks());
        dto.setOtherTraining(entity.getOtherTraining());
        dto.setAdditionalSkills(entity.getAdditionalSkills());
        dto.setCertifications(entity.getCertifications());
        return dto;
    }
}

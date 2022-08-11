package com.objectcomputing.checkins.services.onboardingprofile;

import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileCreateDTO;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileDTO;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfile;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class OnboardingProfileTestUtil {

    public static OnboardingProfileCreateDTO mkCreateOnboardeeProfileDTO() {
        OnboardingProfileCreateDTO dto = new OnboardingProfileCreateDTO();
        dto.setFirstName("TestFirstName");
        dto.setLastName("TestLastName");
        dto.setSocialSecurityNumber("TestSocialSecurityNumber");
        dto.setBirthDate(LocalDate.of(2000,12,25));
        dto.setCurrentAddress("TestAddress");
        dto.setPreviousAddress("TestPreviousAddress");
        dto.setPhoneNumber("TestPhoneNumber");
        dto.setPhoneNumber("TestSecondPhoneNumber");
        dto.setPersonalEmail("TestPersonalEmail");
        return dto;
    }
    //ResponseDTO here is used as an UpdateDTO
    public static OnboardingProfileDTO mkUpdateOnboardeeProfileDTO() {
        OnboardingProfileDTO dto = new OnboardingProfileDTO();
        dto.setFirstName("TestFirstName");
        dto.setMiddleName("TestMiddleName");
        dto.setLastName("TestLastName");
        dto.setSocialSecurityNumber("TestSocialSecurityNumber");
        dto.setBirthDate(LocalDate.of(2000,12,25));
        dto.setCurrentAddress("TestAddress");
        dto.setPreviousAddress("TestPreviousAddress");
        dto.setPhoneNumber("TestPhoneNumber");
        dto.setPhoneNumber("TestSecondPhoneNumber");
        dto.setPersonalEmail("TestPersonalEmail");
        return dto;
    }

    public static OnboardingProfile mkOnboarding_Profile(String seed) {
        return new OnboardingProfile("TestFirstName" + seed,
                "TestMiddleName" + seed,
                "TestLastName" + seed,
                "TestSocialSecurityNumber" + seed,
                LocalDate.of(2000, 12, 25),
                "TestAddress" + seed,
                "TestPreviousAddress" + seed,
                "TestPhoneNumber" + seed,
                "TestSecondPhoneNumber" + seed,
                "TestPersonalEmail" + seed
                );
    }

    public static OnboardingProfile mkOnboarding_Profile() {
        return mkOnboarding_Profile("");
    }

    public static void assertProfilesEqual(OnboardingProfile entity, OnboardingProfileDTO dto) {
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getMiddleName(), dto.getMiddleName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getSocialSecurityNumber(), dto.getSocialSecurityNumber());
        assertEquals(entity.getBirthDate(), dto.getBirthDate());
        assertEquals(entity.getCurrentAddress(), dto.getCurrentAddress());
        assertEquals(entity.getPreviousAddress(), dto.getPreviousAddress());
        assertEquals(entity.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(entity.getSecondPhoneNumber(), dto.getSecondPhoneNumber());
        assertEquals(entity.getPersonalEmail(), dto.getPersonalEmail());
    }

    public static void assertProfilesEqual(OnboardingProfileCreateDTO entity, OnboardingProfileDTO dto) {
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getMiddleName(), dto.getMiddleName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getSocialSecurityNumber(), dto.getSocialSecurityNumber());
        assertEquals(entity.getBirthDate(), dto.getBirthDate());
        assertEquals(entity.getCurrentAddress(), dto.getCurrentAddress());
        assertEquals(entity.getPreviousAddress(), dto.getPreviousAddress());
        assertEquals(entity.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(entity.getSecondPhoneNumber(), dto.getSecondPhoneNumber());
        assertEquals(entity.getPersonalEmail(), dto.getPersonalEmail());
    }

    public static OnboardingProfileDTO toDto(OnboardingProfile entity)
   {
       OnboardingProfileDTO dto= new OnboardingProfileDTO();
       dto.setId(entity.getId());
       dto.setFirstName(entity.getFirstName());
       dto.setMiddleName(entity.getMiddleName());
       dto.setLastName(entity.getLastName());
       dto.setSocialSecurityNumber(entity.getSocialSecurityNumber());
       dto.setBirthDate(entity.getBirthDate());
       dto.setCurrentAddress(entity.getCurrentAddress());
       dto.setPreviousAddress(entity.getPreviousAddress());
       dto.setPhoneNumber(entity.getPhoneNumber());
       dto.setSecondPhoneNumber(entity.getSecondPhoneNumber());
       dto.setPersonalEmail(entity.getPersonalEmail());

       return dto;
    }
}
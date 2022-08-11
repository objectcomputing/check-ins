package com.objectcomputing.checkins.services.employmentpreferences;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmploymentDesiredAvailabilityTestUtil {

    public static EmploymentDesiredAvailabilityCreateDTO mkCreateEmploymentDesiredAvailabilityDTO() {
        EmploymentDesiredAvailabilityCreateDTO dto = new EmploymentDesiredAvailabilityCreateDTO();
        dto.setDesiredPosition("TestDesiredPosition");
        LocalDate date = LocalDate.of(2020, 1, 8);
        dto.setDesiredStartDate(date);
        dto.setDesiredSalary("TestSalary");
        dto.setCurrentlyEmployed(true);
        dto.setContactCurrentEmployer(true);
        dto.setPreviousEmploymentOCI(true);
        dto.setNoncompeteAgreement(true);
        dto.setNoncompeteExpirationDate(date);

        return dto;
    }

    // ResponseDTO here is used as an UpdateDTO
    public static EmploymentDesiredAvailabilityDTO mkUpdateEmploymentDesiredAvailabilityDTO() {
        EmploymentDesiredAvailabilityDTO dto = new EmploymentDesiredAvailabilityDTO();
        dto.setDesiredPosition("TestDesiredPosition");
        LocalDate date = LocalDate.of(2020, 1, 8);
        dto.setDesiredStartDate(date);
        dto.setDesiredSalary("TestSalary");
        dto.setCurrentlyEmployed(true);
        dto.setContactCurrentEmployer(true);
        dto.setPreviousEmploymentOCI(true);
        dto.setNoncompeteAgreement(true);
        dto.setNoncompeteExpirationDate(date);

        return dto;
    }

    public static EmploymentDesiredAvailability mkEmploymentDesiredAvailability(String seed) {
        LocalDate date = LocalDate.of(2020, 1, 8);
        return new EmploymentDesiredAvailability("TestDesiredPosition" + seed,
                date,
                "TestSalary" + seed,
                true,
                true,
                true,
                true,
                date);
    }

    public static EmploymentDesiredAvailability mkEmploymentDesiredAvailability() { return mkEmploymentDesiredAvailability(""); }

    public static void assertProfilesEqual(EmploymentDesiredAvailability entity, EmploymentDesiredAvailabilityDTO dto) {
        assertEquals(entity.getDesiredPosition(), dto.getDesiredPosition());
        assertEquals(entity.getDesiredStartDate(), dto.getDesiredStartDate());
        assertEquals(entity.getDesiredSalary(), dto.getDesiredSalary());
        assertEquals(entity.getCurrentlyEmployed(), dto.getCurrentlyEmployed());
        assertEquals(entity.getContactCurrentEmployer(), dto.getContactCurrentEmployer());
        assertEquals(entity.getPreviousEmploymentOCI(), dto.getPreviousEmploymentOCI());
        assertEquals(entity.getNoncompeteAgreement(), dto.getNoncompeteAgreement());
        assertEquals(entity.getNoncompeteExpirationDate(), dto.getNoncompeteExpirationDate());
    }

    public static EmploymentDesiredAvailabilityDTO toDto(EmploymentDesiredAvailability entity) {
        EmploymentDesiredAvailabilityDTO dto = new EmploymentDesiredAvailabilityDTO();
        dto.setId(entity.getId());
        dto.setDesiredPosition(entity.getDesiredPosition());
        dto.setDesiredStartDate(entity.getDesiredStartDate());
        dto.setDesiredSalary(entity.getDesiredSalary());
        dto.setCurrentlyEmployed(entity.getCurrentlyEmployed());
        dto.setContactCurrentEmployer(entity.getContactCurrentEmployer());
        dto.setPreviousEmploymentOCI(entity.getPreviousEmploymentOCI());
        dto.setNoncompeteAgreement(entity.getNoncompeteAgreement());
        dto.setNoncompeteExpirationDate(entity.getNoncompeteExpirationDate());

        return dto;
    }

}

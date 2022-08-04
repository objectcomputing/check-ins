package com.objectcomputing.checkins.services.employmenthistory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

public class EmploymentHistoryTestUtil {

    public static void assertHistoryEqual(EmploymentHistory entity, EmploymentHistoryDTO dto) {
        assertEquals(entity.getCompany(), dto.getCompany());
        assertEquals(entity.getCompanyAddress(), dto.getCompanyAddress());
        assertEquals(entity.getJobTitle(), dto.getJobTitle());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getEndDate(), dto.getEndDate());
        assertEquals(entity.getReason(), dto.getReason());
    }

    public static EmploymentHistoryCreateDTO mkCreateEmploymentHistoryDTO() {
        EmploymentHistoryCreateDTO dto = new EmploymentHistoryCreateDTO();
        dto.setCompany("OCI");
        dto.setCompanyAddress("I forgot.");
        dto.setJobTitle("Intern");
        dto.setStartDate(LocalDate.of(2022, 6, 9));
        dto.setEndDate(LocalDate.now());
        dto.setReason("Internship ended.");
        return dto;
    }

    public static EmploymentHistoryDTO mkUpdateEmploymentHistoryDTO() {
        EmploymentHistoryDTO dto = new EmploymentHistoryDTO();
        dto.setCompany("OCI");
        dto.setCompanyAddress("I forgot.");
        dto.setJobTitle("Intern");
        dto.setStartDate(LocalDate.of(2022, 6, 9));
        dto.setEndDate(LocalDate.now());
        dto.setReason("Internship ended.");
        return dto;
    }

    public static EmploymentHistoryDTO toDto(EmploymentHistory entity) {
        EmploymentHistoryDTO dto = new EmploymentHistoryDTO();
        dto.setId(entity.getId());
        dto.setCompany(entity.getCompany());
        dto.setCompanyAddress(entity.getCompanyAddress());
        dto.setJobTitle(entity.getJobTitle());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setReason(entity.getReason());
        return dto;
    }
}

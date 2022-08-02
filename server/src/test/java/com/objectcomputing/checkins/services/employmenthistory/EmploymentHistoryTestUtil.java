package com.objectcomputing.checkins.services.employmenthistory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmploymentHistoryTestUtil {

    public static void assertHistoryEqual(EmploymentHistory entity, EmploymentHistoryDTO dto) {
        assertEquals(entity.getCompany(), dto.getCompany());
        assertEquals(entity.getCompanyAddress(), dto.getCompanyAddress());
        assertEquals(entity.getJobTitle(), dto.getJobTitle());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getEndDate(), dto.getEndDate());
        assertEquals(entity.getReason(), dto.getReason());
    }
}

package com.objectcomputing.checkins.services.education;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EducationTestUtil {
    
    public static void assertEducationEqual(Education entity, EducationDTO dto) {
        assertEquals(entity.getHighestDegree(), dto.getHighestDegree());
        assertEquals(entity.getInstitution(), dto.getInstitution());
        assertEquals(entity.getLocation(), dto.getLocation());
        assertEquals(entity.getDegree(), dto.getDegree());
        assertEquals(entity.getCompletionDate(), dto.getCompletionDate());
        assertEquals(entity.getMajor(), dto.getMajor());
        assertEquals(entity.getAdditionalInfo(), dto.getAdditionalInfo());
    }
}

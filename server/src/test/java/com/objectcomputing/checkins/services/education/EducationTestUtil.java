package com.objectcomputing.checkins.services.education;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

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

    public static EducationDTO mkUpdateEducationDTO() {
        EducationDTO dto = new EducationDTO();
        dto.setHighestDegree("Masters");
        dto.setInstitution("UMSL");
        dto.setLocation("St. Louis");
        dto.setDegree("Masters");
        dto.setCompletionDate(LocalDate.now());
        dto.setMajor("Comp Sci");
        dto.setAdditionalInfo(":)");
        return dto;
    }

    public static EducationDTO toDto(Education entity) {
        EducationDTO dto = new EducationDTO();
        dto.setId(entity.getId());
        dto.setHighestDegree(entity.getHighestDegree());
        dto.setInstitution(entity.getInstitution());
        dto.setLocation(entity.getLocation());
        dto.setDegree(entity.getDegree());
        dto.setCompletionDate(entity.getCompletionDate());
        dto.setMajor(entity.getMajor());
        dto.setAdditionalInfo(entity.getAdditionalInfo());

        return dto;
    }
}

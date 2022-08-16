package com.objectcomputing.checkins.services.workingenvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkingEnvironmentTestUtil {
    public static void assertEnvironmentEquals(WorkingEnvironment entity, WorkingEnvironmentResponseDTO dto) {
        assertEquals(entity.getWorkLocation(), dto.getWorkLocation());
        assertEquals(entity.getKeyType(), dto.getKeyType());
        assertEquals(entity.getOsType(), dto.getOsType());
        assertEquals(entity.getAccessories(), dto.getAccessories());
        assertEquals(entity.getOtherAccessories(), dto.getOtherAccessories());
    }

    public static WorkingEnvironmentResponseDTO mkUpdateWorkingEnvironment() {
        WorkingEnvironmentResponseDTO dto = new WorkingEnvironmentResponseDTO();
        dto.setWorkLocation("Hybrid");
        dto.setKeyType("Key Fob");
        dto.setOsType("Windows");
        dto.setAccessories("Monitor");
        dto.setOtherAccessories("No thanks I'm ok :)");
        return dto;
    }

    public static WorkingEnvironmentResponseDTO toDto(WorkingEnvironment entity) {
        WorkingEnvironmentResponseDTO dto = new WorkingEnvironmentResponseDTO();
        dto.setId(entity.getId());
        dto.setWorkLocation(entity.getWorkLocation());
        dto.setKeyType(entity.getKeyType());
        dto.setOsType(entity.getOsType());
        dto.setAccessories(entity.getAccessories());
        dto.setOtherAccessories(entity.getOtherAccessories());
        return dto;
    }
}

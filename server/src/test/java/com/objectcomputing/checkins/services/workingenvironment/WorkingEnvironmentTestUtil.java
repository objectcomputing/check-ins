package com.objectcomputing.checkins.services.workingenvironment;

import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironment;
import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironmentDTO;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkingEnvironmentTestUtil {
    public static void assertEnvironmentEquals(WorkingEnvironment entity, WorkingEnvironmentDTO dto) {
        assertEquals(entity.getWorkLocation(), dto.getWorkLocation());
        assertEquals(entity.getKeyType(), dto.getKeyType());
        assertEquals(entity.getOsType(), dto.getOsType());
        assertEquals(entity.getAccessories(), dto.getAccessories());
        assertEquals(entity.getOtherAccessories(), dto.getOtherAccessories());
    }

    public static WorkingEnvironmentDTO mkUpdateWorkingEnvironment(NewHireAccountEntity account) {
        WorkingEnvironmentDTO dto = new WorkingEnvironmentDTO();
        dto.setWorkLocation("Hybrid");
        dto.setKeyType("Key Fob");
        dto.setOsType("Windows");
        dto.setAccessories("Monitor");
        dto.setOtherAccessories("No thanks I'm ok :)");
        if(account != null) dto.setEmailAddress(account.getEmailAddress());
        return dto;
    }

    public static WorkingEnvironmentDTO toDto(WorkingEnvironment entity) {
        WorkingEnvironmentDTO dto = new WorkingEnvironmentDTO();
        dto.setId(entity.getId());
        dto.setWorkLocation(entity.getWorkLocation());
        dto.setKeyType(entity.getKeyType());
        dto.setOsType(entity.getOsType());
        dto.setAccessories(entity.getAccessories());
        dto.setOtherAccessories(entity.getOtherAccessories());
        dto.setEmailAddress(entity.getNewHireAccount().getEmailAddress());
        return dto;
    }
}
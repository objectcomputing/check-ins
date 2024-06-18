package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActionItemCreateDTOTest extends TestContainersSuite {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        ActionItemCreateDTO dto = new ActionItemCreateDTO();
        assertNull(dto.getCheckinid());
        assertNull(dto.getCreatedbyid());
        assertNull(dto.getDescription());
    }

    @Test
    void testConstraintViolation() {
        ActionItemCreateDTO dto = new ActionItemCreateDTO();

        Set<ConstraintViolation<ActionItemCreateDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size());
        for (ConstraintViolation<ActionItemCreateDTO> violation : violations) {
            assertEquals("must not be null", violation.getMessage());
        }
    }

    @Test
    void testPopulatedDTO() {
        ActionItemCreateDTO dto = new ActionItemCreateDTO();

        UUID checkinId = UUID.randomUUID();
        UUID createById = UUID.randomUUID();

        dto.setCheckinid(checkinId);
        assertEquals(dto.getCheckinid(), checkinId);

        dto.setCreatedbyid(createById);
        assertEquals(dto.getCreatedbyid(), createById);

        dto.setDescription("DNC");
        assertEquals("DNC", dto.getDescription());

        Set<ConstraintViolation<ActionItemCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

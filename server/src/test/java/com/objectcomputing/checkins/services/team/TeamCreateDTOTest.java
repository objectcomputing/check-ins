package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TeamCreateDTOTest extends TestContainersSuite {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        TeamCreateDTO dto = new TeamCreateDTO();
        assertNull(dto.getDescription());
        assertNull(dto.getName());
    }

    @Test
    void testConstraintViolation() {
        TeamCreateDTO dto = new TeamCreateDTO();

        dto.setName("");

        Set<ConstraintViolation<TeamCreateDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        for (ConstraintViolation<TeamCreateDTO> violation : violations) {
            assertEquals("must not be blank", violation.getMessage());
        }
    }

    @Test
    void testPopulatedDTO() {
        TeamCreateDTO dto = new TeamCreateDTO();

        final String name = "Melt man";
        dto.setName(name);
        assertEquals(dto.getName(), name);

        final String description = "with the power to melt";
        dto.setDescription(description);
        assertEquals(dto.getDescription(), description);

        Set<ConstraintViolation<TeamCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

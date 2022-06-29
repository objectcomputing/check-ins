package com.objectcomputing.checkins.services.team;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamCreateDTOTest {

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
            assertEquals(violation.getMessage(), "must not be blank");
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

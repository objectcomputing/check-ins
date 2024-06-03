package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GuildCreateDTOTest extends TestContainersSuite {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        GuildCreateDTO dto = new GuildCreateDTO();
        assertNull(dto.getDescription());
        assertNull(dto.getName());
    }

    @Test
    void testConstraintViolationName() {
        GuildCreateDTO dto = new GuildCreateDTO();
        dto.setName("");
        dto.setCommunity(false);

        Set<ConstraintViolation<GuildCreateDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        for (ConstraintViolation<GuildCreateDTO> violation : violations) {
            assertEquals("must not be blank", violation.getMessage());
        }
    }

    @Test
    void testPopulatedDTO() {
        GuildCreateDTO dto = new GuildCreateDTO();

        dto.setName("Melt man");
        dto.setCommunity(false);
        dto.setDescription("with the power to melt");

        Set<ConstraintViolation<GuildCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

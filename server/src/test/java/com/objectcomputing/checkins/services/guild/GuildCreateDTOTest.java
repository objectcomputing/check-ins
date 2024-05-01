package com.objectcomputing.checkins.services.guild;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuildCreateDTOTest {

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
        dto.setIsCommunity(false);

        Set<ConstraintViolation<GuildCreateDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        for (ConstraintViolation<GuildCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be blank");
        }
    }

    @Test
    void testConstraintViolationIsCommunity() {
        GuildCreateDTO dto = new GuildCreateDTO();
        dto.setName("name");

        Set<ConstraintViolation<GuildCreateDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        for (ConstraintViolation<GuildCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        GuildCreateDTO dto = new GuildCreateDTO();

        dto.setName("Melt man");
        dto.setIsCommunity(false);
        dto.setDescription("with the power to melt");

        Set<ConstraintViolation<GuildCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

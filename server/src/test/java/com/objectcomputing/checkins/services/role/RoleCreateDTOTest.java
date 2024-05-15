package com.objectcomputing.checkins.services.role;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class RoleCreateDTOTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        RoleCreateDTO dto = new RoleCreateDTO();
        assertNull(dto.getRole());
    }

    @Test
    void testConstraintViolation() {
        RoleCreateDTO dto = new RoleCreateDTO();

        Set<ConstraintViolation<RoleCreateDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 1);
        for (ConstraintViolation<RoleCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        RoleCreateDTO dto = new RoleCreateDTO();

        RoleType roleType = RoleType.ADMIN;

        dto.setRole(roleType.name());
        assertEquals(dto.getRole(), roleType.name());


        Set<ConstraintViolation<RoleCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

package com.objectcomputing.checkins.services.role;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoleCreateDTOTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        RoleCreateDTO dto = new RoleCreateDTO();
        assertNull(dto.getRole());
        assertNull(dto.getMemberid());
    }

    @Test
    void testConstraintViolation() {
        RoleCreateDTO dto = new RoleCreateDTO();

        Set<ConstraintViolation<RoleCreateDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<RoleCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        RoleCreateDTO dto = new RoleCreateDTO();

        RoleType roleType = RoleType.ADMIN;
        UUID memberId = UUID.randomUUID();

        dto.setRole(roleType);
        assertEquals(dto.getRole(), roleType);

        dto.setMemberid(memberId);
        assertEquals(dto.getMemberid(), memberId);

        Set<ConstraintViolation<RoleCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

package com.objectcomputing.checkins.services.memberprofile.currentuser;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class CurrentUserDtoTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        CurrentUserDTO dto = new CurrentUserDTO();
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getRole());
        assertNull(dto.getPdlId());
        assertNull(dto.getLocation());
        assertNull(dto.getWorkEmail());
        assertNull(dto.getInsperityId());
        assertNull(dto.getStartDate());
        assertNull(dto.getBioText());
        assertNull(dto.getImageUrl());
    }

    @Test
    void testConstraintViolation() {
        CurrentUserDTO dto = new CurrentUserDTO();

        Set<ConstraintViolation<CurrentUserDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 3);
        for (ConstraintViolation<CurrentUserDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        CurrentUserDTO dto = new CurrentUserDTO();

        UUID id = UUID.randomUUID();

        dto.setId(id);
        assertEquals(dto.getId(), id);

        dto.setName("some.name");
        assertEquals("some.name", dto.getName());

        dto.setWorkEmail("some.email");
        assertEquals("some.email", dto.getWorkEmail());

        Set<ConstraintViolation<CurrentUserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

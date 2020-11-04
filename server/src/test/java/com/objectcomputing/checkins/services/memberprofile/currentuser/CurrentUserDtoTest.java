package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class CurrentUserDtoTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        CurrentUserDTO dto = new CurrentUserDTO();
        assertNull(dto.getName());
        assertNull(dto.getRole());
        assertNull(dto.getImageUrl());
        assertNull(dto.getMemberProfile());
    }

    @Test
    void testConstraintViolation() {
        CurrentUserDTO dto = new CurrentUserDTO();

        Set<ConstraintViolation<CurrentUserDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 1);
        for (ConstraintViolation<CurrentUserDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        CurrentUserDTO dto = new CurrentUserDTO();

        dto.setName("some.name");
        assertEquals("some.name", dto.getName());

        dto.setMemberProfile(new MemberProfileEntity());

        Set<ConstraintViolation<CurrentUserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

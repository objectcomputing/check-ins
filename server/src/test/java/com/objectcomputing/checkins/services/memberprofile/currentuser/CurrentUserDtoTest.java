package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserDtoTest extends TestContainersSuite {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        CurrentUserDTO dto = new CurrentUserDTO();
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getRole());
        assertNull(dto.getImageUrl());
        assertNull(dto.getMemberProfile());
    }

    @Test
    void testConstraintViolation() {
        CurrentUserDTO dto = new CurrentUserDTO();

        Set<ConstraintViolation<CurrentUserDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 4);
        int nullViolations = 0, blankViolations = 0;
        for (ConstraintViolation<CurrentUserDTO> violation : violations) {
            if (violation.getMessage().equals("must not be null")) {
                ++nullViolations;
            } else if (violation.getMessage().equals("must not be blank")) {
                ++blankViolations;
            }
        }
        assertEquals(1, nullViolations);
        assertEquals(3, blankViolations);
    }

    @Test
    void testPopulatedDTO() {
        CurrentUserDTO dto = new CurrentUserDTO();

        dto.setFirstName("some.first.name");
        assertEquals("some.first.name", dto.getFirstName());
        dto.setLastName("some.last.name");
        assertEquals("some.last.name", dto.getLastName());
        dto.setName(dto.getFirstName() + ' ' + dto.getLastName());

        dto.setMemberProfile(new MemberProfile());

        Set<ConstraintViolation<CurrentUserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

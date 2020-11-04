package com.objectcomputing.checkins.services.team.member;

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
class TeamMemberDTOTest {

    /*
    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        TeamMemberDTO dto = new TeamMemberDTO();
        assertNull(dto.getId());
        assertNull(dto.getMemberid());
        assertNull(dto.isLead());
    }

    @Test
    void testConstraintViolation() {
        TeamMemberDTO dto = new TeamMemberDTO();

        Set<ConstraintViolation<TeamMemberDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<TeamMemberDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        TeamMemberDTO dto = new TeamMemberDTO();

        UUID teamId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        dto.setId(teamId);
        assertEquals(dto.getId(), teamId);

        dto.setMemberid(memberId);
        assertEquals(dto.getMemberid(), memberId);

        dto.setLead(true);
        assertTrue(dto.isLead());

        Set<ConstraintViolation<TeamMemberDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }*/
}

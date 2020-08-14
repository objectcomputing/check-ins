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
class TeamMemberCreateDTOTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        TeamMemberCreateDTO dto = new TeamMemberCreateDTO();
        assertNull(dto.getTeamid());
        assertNull(dto.getMemberid());
        assertNull(dto.isLead());
    }

    @Test
    void testConstraintViolation() {
        TeamMemberCreateDTO dto = new TeamMemberCreateDTO();

        Set<ConstraintViolation<TeamMemberCreateDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<TeamMemberCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        TeamMemberCreateDTO dto = new TeamMemberCreateDTO();

        UUID teamId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        dto.setTeamid(teamId);
        assertEquals(dto.getTeamid(), teamId);

        dto.setMemberid(memberId);
        assertEquals(dto.getMemberid(), memberId);

        dto.setLead(true);
        assertTrue(dto.isLead());

        Set<ConstraintViolation<TeamMemberCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

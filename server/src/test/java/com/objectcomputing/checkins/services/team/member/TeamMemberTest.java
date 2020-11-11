package com.objectcomputing.checkins.services.team.member;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamMemberTest {

    @Inject
    private Validator validator;


    @Test
    void testTeamMemberInstantiation() {
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        final UUID supervisorId = UUID.randomUUID();

        TeamMember teamMember = new TeamMember(teamId, memberId, lead, supervisorId);

        assertEquals(teamId, teamMember.getTeamid());
        assertEquals(memberId, teamMember.getMemberid());
        assertEquals(lead, teamMember.isLead());
        assertEquals(supervisorId, teamMember.getSupervisorid());
    }

    @Test
    void testTeamMemberInstantiation2() {
        final UUID id = UUID.randomUUID();
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        final UUID supervisorId = UUID.randomUUID();

        TeamMember teamMember = new TeamMember(id, teamId, memberId, lead, supervisorId);

        assertEquals(id, teamMember.getId());
        assertEquals(teamId, teamMember.getTeamid());
        assertEquals(memberId, teamMember.getMemberid());
        assertEquals(teamMember.isLead(), lead);
        assertEquals(supervisorId, teamMember.getSupervisorid());

        Set<ConstraintViolation<TeamMember>> violations = validator.validate(teamMember);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        final UUID supervisorId = UUID.randomUUID();

        TeamMember teamMember = new TeamMember(id, teamId, memberId, lead, supervisorId);

        teamMember.setTeamid(null);
        teamMember.setMemberid(null);
        teamMember.setSupervisorid(null);

        Set<ConstraintViolation<TeamMember>> violations = validator.validate(teamMember);
        assertEquals(2, violations.size());
        for (ConstraintViolation<TeamMember> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        final UUID supervisorId = UUID.randomUUID();

        TeamMember tm = new TeamMember(id, teamId, memberId, lead, supervisorId);
        TeamMember tm2 = new TeamMember(id, teamId, memberId, lead, supervisorId);

        assertEquals(tm, tm2);

        tm2.setId(null);

        assertNotEquals(tm, tm2);

        tm2.setId(tm.getId());

        assertEquals(tm, tm2);

        tm2.setLead(false);

        assertNotEquals(tm, tm2);
    }

    @Test
    void testHash() {
        HashMap<TeamMember, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        final UUID supervisorId = UUID.randomUUID();

        TeamMember tm = new TeamMember(id, teamId, memberId, lead, supervisorId);

        map.put(tm, true);

        assertTrue(map.get(tm));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        final UUID supervisorId = UUID.randomUUID();

        TeamMember tm = new TeamMember(id, teamId, memberId, lead, supervisorId);

        String toString = tm.toString();
        assertTrue(toString.contains(teamId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(memberId.toString()));
        assertTrue(toString.contains(String.valueOf(lead)));
        assertTrue(toString.contains(supervisorId.toString()));
    }
}

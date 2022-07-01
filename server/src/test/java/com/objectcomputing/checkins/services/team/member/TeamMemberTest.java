package com.objectcomputing.checkins.services.team.member;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.inject.Inject;
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
        TeamMember teamMember = new TeamMember(teamId, memberId, lead);
        assertEquals(teamId, teamMember.getTeamId());
        assertEquals(memberId, teamMember.getMemberId());
        assertEquals(lead, teamMember.isLead());
    }

    @Test
    void testTeamMemberInstantiation2() {
        final UUID id = UUID.randomUUID();
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        TeamMember teamMember = new TeamMember(id, teamId, memberId, lead);
        assertEquals(id, teamMember.getId());
        assertEquals(teamId, teamMember.getTeamId());
        assertEquals(memberId, teamMember.getMemberId());
        assertEquals(teamMember.isLead(), lead);

        Set<ConstraintViolation<TeamMember>> violations = validator.validate(teamMember);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        TeamMember teamMember = new TeamMember(id, teamId, memberId, lead);

        teamMember.setTeamId(null);
        teamMember.setMemberId(null);

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
        TeamMember tm = new TeamMember(id, teamId, memberId, lead);
        TeamMember tm2 = new TeamMember(id, teamId, memberId, lead);

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
        TeamMember tm = new TeamMember(id, teamId, memberId, lead);

        map.put(tm, true);

        assertTrue(map.get(tm));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final UUID teamId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        TeamMember tm = new TeamMember(id, teamId, memberId, lead);

        String toString = tm.toString();
        assertTrue(toString.contains(teamId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(memberId.toString()));
        assertTrue(toString.contains(String.valueOf(lead)));
    }
}
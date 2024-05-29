package com.objectcomputing.checkins.services.guild.member;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GuildMemberTest extends TestContainersSuite {

    @Inject
    private Validator validator;


    @Test
    void testGuildMemberInstantiation() {
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember guildMember = new GuildMember(guildId, memberId, lead);
        assertEquals(guildId, guildMember.getGuildId());
        assertEquals(memberId, guildMember.getMemberId());
        assertEquals(lead, guildMember.getLead());
    }

    @Test
    void testGuildMemberInstantiation2() {
        final UUID id = UUID.randomUUID();
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember guildMember = new GuildMember(id, guildId, memberId, lead);
        assertEquals(id, guildMember.getId());
        assertEquals(guildId, guildMember.getGuildId());
        assertEquals(memberId, guildMember.getMemberId());
        assertEquals(guildMember.getLead(), lead);

        Set<ConstraintViolation<GuildMember>> violations = validator.validate(guildMember);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember guildMember = new GuildMember(id, guildId, memberId, lead);

        guildMember.setGuildId(null);
        guildMember.setMemberId(null);

        Set<ConstraintViolation<GuildMember>> violations = validator.validate(guildMember);
        assertEquals(2, violations.size());
        for (ConstraintViolation<GuildMember> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember tm = new GuildMember(id, guildId, memberId, lead);
        GuildMember tm2 = new GuildMember(id, guildId, memberId, lead);

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
        HashMap<GuildMember, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember tm = new GuildMember(id, guildId, memberId, lead);

        map.put(tm, true);

        assertTrue(map.get(tm));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember tm = new GuildMember(id, guildId, memberId, lead);

        String toString = tm.toString();
        assertTrue(toString.contains(guildId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(memberId.toString()));
        assertTrue(toString.contains(String.valueOf(lead)));
    }
}

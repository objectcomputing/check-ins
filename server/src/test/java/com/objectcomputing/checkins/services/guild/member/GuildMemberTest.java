package com.objectcomputing.checkins.services.guild.member;

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
class GuildMemberTest {

    @Inject
    private Validator validator;


    @Test
    void testGuildMemberInstantiation() {
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember guildMember = new GuildMember(guildId, memberId, lead);
        assertEquals(guildId, guildMember.getGuildid());
        assertEquals(memberId, guildMember.getMemberid());
        assertEquals(lead, guildMember.isLead());
    }

    @Test
    void testGuildMemberInstantiation2() {
        final UUID id = UUID.randomUUID();
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember guildMember = new GuildMember(id, guildId, memberId, lead);
        assertEquals(id, guildMember.getId());
        assertEquals(guildId, guildMember.getGuildid());
        assertEquals(memberId, guildMember.getMemberid());
        assertEquals(guildMember.isLead(), lead);

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

        guildMember.setGuildid(null);
        guildMember.setMemberid(null);

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
        GuildMember g = new GuildMember(id, guildId, memberId, lead);
        GuildMember g2 = new GuildMember(id, guildId, memberId, lead);

        assertEquals(g, g2);

        g2.setId(null);

        assertNotEquals(g, g2);

        g2.setId(g.getId());

        assertEquals(g, g2);

        g2.setLead(false);

        assertNotEquals(g, g2);
    }

    @Test
    void testHash() {
        HashMap<GuildMember, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember g = new GuildMember(id, guildId, memberId, lead);

        map.put(g, true);

        assertTrue(map.get(g));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final UUID guildId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final boolean lead = true;
        GuildMember g = new GuildMember(id, guildId, memberId, lead);

        String toString = g.toString();
        assertTrue(toString.contains(guildId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(memberId.toString()));
        assertTrue(toString.contains(String.valueOf(lead)));
    }
}

package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class GuildMemberCreateDTOTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        GuildMemberCreateDTO dto = new GuildMemberCreateDTO();
        assertNull(dto.getGuildid());
        assertNull(dto.getMemberid());
        assertNull(dto.isLead());
    }

    @Test
    void testConstraintViolation() {
        GuildMemberCreateDTO dto = new GuildMemberCreateDTO();

        Set<ConstraintViolation<GuildMemberCreateDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<GuildMemberCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        GuildMemberCreateDTO dto = new GuildMemberCreateDTO();

        UUID guildId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        dto.setGuildid(guildId);
        assertEquals(dto.getGuildid(), guildId);

        dto.setMemberid(memberId);
        assertEquals(dto.getMemberid(), memberId);

        dto.setLead(true);
        assertTrue(dto.isLead());

        Set<ConstraintViolation<GuildMemberCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}

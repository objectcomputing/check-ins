package com.objectcomputing.checkins.services.member_skill;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class MemberSkillsCreateDTOTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        MemberSkillCreateDTO dto = new MemberSkillCreateDTO();
        assertNull(dto.getMemberid());
        assertNull(dto.getSkillid());
        assertNull(dto.getSkilllevel());
        assertNull(dto.getLastuseddate());
    }

    @Test
    void testConstraintViolation() {
        MemberSkillCreateDTO dto = new MemberSkillCreateDTO();

        Set<ConstraintViolation<MemberSkillCreateDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<MemberSkillCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        MemberSkillCreateDTO dto = new MemberSkillCreateDTO();

        UUID memberId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        dto.setMemberid(memberId);
        assertEquals(dto.getMemberid(), memberId);

        dto.setSkillid(skillId);
        assertEquals(dto.getSkillid(), skillId);

        Set<ConstraintViolation<MemberSkillCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

}

package com.objectcomputing.checkins.services.member_skill;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.member_skill.skillsreport.SkillLevel;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberSkillServiceImplTest extends TestContainersSuite
                                 implements MemberProfileFixture, MemberSkillFixture, SkillFixture {
    @Inject
    private MemberSkillServiceImpl memberSkillsServices;

    @Test
    void testRead() {
        Skill skill = createSkill("Skill1", false, "First", false);
        MemberProfile member = createADefaultMemberProfile();
        MemberSkill memberSkill = createMemberSkill(member, skill, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        assertEquals(memberSkill, memberSkillsServices.read(memberSkill.getId()));
    }

    @Test
    void testReadNullId() {
        assertThrows(ConstraintViolationException.class, () -> memberSkillsServices.read(null));
    }

    @Test
    void testSave() {
        Skill skill = createSkill("Skill1", false, "First", false);
        MemberProfile member = createADefaultMemberProfile();
        MemberSkill memberSkill = new MemberSkill(member.getId(), skill.getId());
        assertEquals(memberSkill, memberSkillsServices.save(memberSkill));
    }

    @Test
    void testSaveWithId() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        BadArgException exception = assertThrows(BadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Found unexpected id %s for member skill", memberSkill.getId()), exception.getMessage());
    }

    @Test
    void testSaveActionItemNullMemberId() {
        MemberSkill memberSkill = new MemberSkill(null, UUID.randomUUID());

        BadArgException exception = assertThrows(BadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Invalid member skill %s", memberSkill), exception.getMessage());
    }

    @Test
    void testSaveActionItemNullSkillId() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), null);

        BadArgException exception = assertThrows(BadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Invalid member skill %s", memberSkill), exception.getMessage());
    }

    @Test
    void testSaveNullMemberSkill() {
        assertNull(memberSkillsServices.save(null));
    }

    @Test
    void testSaveMemberSkillAlreadyExistingSkill() {
        Skill skill = createSkill("Skill1", false, "First", false);
        MemberProfile member = createADefaultMemberProfile();
        MemberSkill savedSkill = createMemberSkill(member, skill, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        MemberSkill memberSkill = new MemberSkill(member.getId(), skill.getId(), SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Member %s already has this skill %s",
                memberSkill.getMemberid(), memberSkill.getSkillid()), exception.getMessage());
    }

    @Test
    void testSaveMemberSkillNonExistingSkill() {
        MemberProfile member = createADefaultMemberProfile();
        MemberSkill memberSkill = new MemberSkill(member.getId(), UUID.randomUUID());

        BadArgException exception = assertThrows(BadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Skill %s doesn't exist", memberSkill.getSkillid()), exception.getMessage());
    }

    @Test
    void testSaveMemberSkillNonExistingMember() {
        Skill skill = createSkill("Skill1", false, "First", false);
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), skill.getId());

        BadArgException exception = assertThrows(BadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Member Profile %s doesn't exist", memberSkill.getMemberid()), exception.getMessage());
    }

    @Test
    void testFindByFieldsNullParams() {
        Skill skill1 = createSkill("Skill1", false, "First", false);
        Skill skill2 = createSkill("Skill2", false, "Second", false);
        Skill skill3 = createSkill("Skill3", false, "Third", false);
        MemberProfile member1 = createADefaultMemberProfile();
        MemberSkill ms1 = createMemberSkill(member1, skill1, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        MemberSkill ms2 = createMemberSkill(member1, skill2, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        MemberSkill ms3 = createMemberSkill(member1, skill3, SkillLevel.NOVICE_LEVEL, LocalDate.now());

        Set<MemberSkill> memberSkillSet = Set.of(ms1, ms2, ms3);
        assertEquals(memberSkillSet, memberSkillsServices.findByFields(null, null));
    }

    @Test
    void testFindByFieldsMemberId() {
        Skill skill1 = createSkill("Skill1", false, "First", false);
        Skill skill2 = createSkill("Skill2", false, "Second", false);
        Skill skill3 = createSkill("Skill3", false, "Third", false);
        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberSkill ms1 = createMemberSkill(member1, skill1, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        MemberSkill ms2 = createMemberSkill(member2, skill2, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        MemberSkill ms3 = createMemberSkill(member1, skill3, SkillLevel.NOVICE_LEVEL, LocalDate.now());

        List<MemberSkill> memberSkillSet = List.of(ms1, ms2, ms3);
        List<MemberSkill> memberSkillsToFind = List.of(memberSkillSet.get(1));
        MemberSkill memberSkill = memberSkillsToFind.get(0);

        assertEquals(new HashSet<>(memberSkillsToFind), memberSkillsServices.findByFields(memberSkill.getMemberid(), null));
    }

    @Test
    void testFindByFieldsSkillId() {
        Skill skill1 = createSkill("Skill1", false, "First", false);
        Skill skill2 = createSkill("Skill2", false, "Second", false);
        Skill skill3 = createSkill("Skill3", false, "Third", false);
        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberSkill ms1 = createMemberSkill(member1, skill1, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        MemberSkill ms2 = createMemberSkill(member2, skill2, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        MemberSkill ms3 = createMemberSkill(member1, skill3, SkillLevel.NOVICE_LEVEL, LocalDate.now());

        List<MemberSkill> memberSkillSet = List.of(ms1, ms2, ms3);

        List<MemberSkill> memberSkillsToFind = List.of(memberSkillSet.get(1));
        MemberSkill memberSkill = memberSkillsToFind.get(0);

        assertEquals(new HashSet<>(memberSkillsToFind), memberSkillsServices.findByFields(null, memberSkill.getSkillid()));
    }

    @Test
    void testFindByFieldsAll() {
        Skill skill1 = createSkill("Skill1", false, "First", false);
        Skill skill2 = createSkill("Skill2", false, "Second", false);
        Skill skill3 = createSkill("Skill3", false, "Third", false);
        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberSkill ms1 = createMemberSkill(member1, skill1, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        MemberSkill ms2 = createMemberSkill(member2, skill2, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        MemberSkill ms3 = createMemberSkill(member1, skill3, SkillLevel.NOVICE_LEVEL, LocalDate.now());

        List<MemberSkill> memberSkillSet = List.of(ms1, ms2, ms3);

        List<MemberSkill> memberSkillsToFind = List.of(memberSkillSet.get(1));

        MemberSkill memberSkill = memberSkillsToFind.get(0);

        assertEquals(new HashSet<>(memberSkillsToFind), memberSkillsServices
                .findByFields(memberSkill.getMemberid(), memberSkill.getSkillid()));
    }

    @Test
    void testReadAll() {
        Skill skill1 = createSkill("Skill1", false, "First", false);
        Skill skill2 = createSkill("Skill2", false, "Second", false);
        Skill skill3 = createSkill("Skill3", false, "Third", false);
        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberSkill ms1 = createMemberSkill(member1, skill1, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        MemberSkill ms2 = createMemberSkill(member2, skill2, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        MemberSkill ms3 = createMemberSkill(member1, skill3, SkillLevel.NOVICE_LEVEL, LocalDate.now());

        Set<MemberSkill> memberSkillSet = Set.of(ms1, ms2, ms3);

        assertEquals(memberSkillSet, memberSkillsServices.findByFields(null,null));
    }

    @Test
    void testDelete() {
        Skill skill = createSkill("Skill1", false, "First", false);
        memberSkillsServices.delete(skill.getId());
        assertFalse(getMemberSkillRepository().findById(skill.getId())
                                              .isPresent());
    }
}

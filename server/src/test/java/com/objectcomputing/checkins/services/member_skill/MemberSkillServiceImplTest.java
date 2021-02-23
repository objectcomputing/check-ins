package com.objectcomputing.checkins.services.member_skill;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberSkillServiceImplTest {

    @Mock
    private MemberSkillRepository memberSkillRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private MemberSkillServiceImpl memberSkillsServices;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(memberSkillRepository, skillRepository, memberProfileRepository);
    }

    @Test
    void testRead() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        when(memberSkillRepository.findById(memberSkill.getId())).thenReturn(Optional.of(memberSkill));

        assertEquals(memberSkill, memberSkillsServices.read(memberSkill.getId()));

        verify(memberSkillRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(memberSkillsServices.read(null));

        verify(memberSkillRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID());
        Skill skill = new Skill();

        when(skillRepository.findById(eq(memberSkill.getSkillid()))).thenReturn(Optional.of(skill));
        when(memberProfileRepository.findById(eq(memberSkill.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(memberSkillRepository.save(eq(memberSkill))).thenReturn(memberSkill);

        assertEquals(memberSkill, memberSkillsServices.save(memberSkill));

        verify(skillRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(memberSkillRepository, times(1)).save(any(MemberSkill.class));
    }

    @Test
    void testSaveWithId() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        MemberSkillBadArgException exception = assertThrows(MemberSkillBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Found unexpected id %s for member skill", memberSkill.getId()), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveActionItemNullMemberId() {
        MemberSkill memberSkill = new MemberSkill(null, UUID.randomUUID());

        MemberSkillBadArgException exception = assertThrows(MemberSkillBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Invalid member skill %s", memberSkill), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveActionItemNullSkillId() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), null);

        MemberSkillBadArgException exception = assertThrows(MemberSkillBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Invalid member skill %s", memberSkill), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveNullMemberSkill() {
        assertNull(memberSkillsServices.save(null));

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveMemberSkillAlreadyExistingSkill() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID());

        when(skillRepository.findById(eq(memberSkill.getSkillid()))).thenReturn(Optional.of(new Skill()));
        when(memberProfileRepository.findById(eq(memberSkill.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(memberSkillRepository.findByMemberidAndSkillid(eq(memberSkill.getMemberid()), eq(memberSkill.getSkillid())))
        .thenReturn(Optional.of(memberSkill));

        MemberSkillAlreadyExistsException exception = assertThrows(MemberSkillAlreadyExistsException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Member %s already has this skill %s",
                memberSkill.getMemberid(), memberSkill.getSkillid()), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(memberSkillRepository, times(1)).findByMemberidAndSkillid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveMemberSkillNonExistingSkill() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID());

        when(skillRepository.findById(eq(memberSkill.getSkillid()))).thenReturn(Optional.empty());
        when(memberProfileRepository.findById(eq(memberSkill.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));

        MemberSkillBadArgException exception = assertThrows(MemberSkillBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Skill %s doesn't exist", memberSkill.getSkillid()), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testSaveMemberSkillNonExistingMember() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID());

        when(skillRepository.findById(eq(memberSkill.getSkillid()))).thenReturn(Optional.of(new Skill()));
        when(memberProfileRepository.findById(eq(memberSkill.getMemberid()))).thenReturn(Optional.empty());

        MemberSkillBadArgException exception = assertThrows(MemberSkillBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Member Profile %s doesn't exist", memberSkill.getMemberid()), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testFindByFieldsNullParams() {
        Set<MemberSkill> memberSkillSet = Set.of(
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID())
        );

        when(memberSkillRepository.findAll()).thenReturn(memberSkillSet);

        assertEquals(memberSkillSet, memberSkillsServices.findByFields(null, null));

        verify(memberSkillRepository, times(1)).findAll();
        verify(memberSkillRepository, never()).findByMemberid(any(UUID.class));
        verify(memberSkillRepository, never()).findBySkillid(any(UUID.class));
    }

    @Test
    void testFindByFieldsMemberId() {
        List<MemberSkill> memberSkillSet = List.of(
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID())
        );

        List<MemberSkill> memberSkillsToFind = List.of(memberSkillSet.get(1));
        MemberSkill memberSkill = memberSkillsToFind.get(0);

        when(memberSkillRepository.findAll()).thenReturn(memberSkillSet);
        when(memberSkillRepository.findByMemberid(memberSkill.getMemberid())).thenReturn(memberSkillsToFind);

        assertEquals(new HashSet<>(memberSkillsToFind), memberSkillsServices.findByFields(memberSkill.getMemberid(), null));

        verify(memberSkillRepository, times(1)).findAll();
        verify(memberSkillRepository, times(1)).findByMemberid(any(UUID.class));
        verify(memberSkillRepository, never()).findBySkillid(any(UUID.class));
    }

    @Test
    void testFindByFieldsSkillId() {
        List<MemberSkill> memberSkillSet = List.of(
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID())
        );

        List<MemberSkill> memberSkillsToFind = List.of(memberSkillSet.get(1));
        MemberSkill memberSkill = memberSkillsToFind.get(0);

        when(memberSkillRepository.findAll()).thenReturn(memberSkillSet);
        when(memberSkillRepository.findBySkillid(memberSkill.getSkillid())).thenReturn(memberSkillsToFind);

        assertEquals(new HashSet<>(memberSkillsToFind), memberSkillsServices.findByFields(null, memberSkill.getSkillid()));

        verify(memberSkillRepository, times(1)).findAll();
        verify(memberSkillRepository, times(1)).findBySkillid(any(UUID.class));
        verify(memberSkillRepository, never()).findByMemberid(any(UUID.class));
    }

    @Test
    void testFindByFieldsAll() {
        List<MemberSkill> memberSkillSet = List.of(
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID())
        );

        List<MemberSkill> memberSkillsToFind = List.of(memberSkillSet.get(1));

        MemberSkill memberSkill = memberSkillsToFind.get(0);
        when(memberSkillRepository.findAll()).thenReturn(memberSkillSet);
        when(memberSkillRepository.findBySkillid(memberSkill.getSkillid())).thenReturn(memberSkillsToFind);
        when(memberSkillRepository.findByMemberid(memberSkill.getMemberid())).thenReturn(memberSkillsToFind);

        assertEquals(new HashSet<>(memberSkillsToFind), memberSkillsServices
                .findByFields(memberSkill.getMemberid(), memberSkill.getSkillid()));

        verify(memberSkillRepository, times(1)).findAll();
        verify(memberSkillRepository, times(1)).findByMemberid(any(UUID.class));
        verify(memberSkillRepository, times(1)).findBySkillid(any(UUID.class));
    }

    @Test
    void testReadAll() {
        Set<MemberSkill> memberSkillSet = Set.of(
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID()),
                new MemberSkill(UUID.randomUUID(), UUID.randomUUID())
        );

        when(memberSkillRepository.findAll()).thenReturn(memberSkillSet);

        assertEquals(memberSkillSet, memberSkillsServices.findByFields(null,null));

        verify(memberSkillRepository, times(1)).findAll();
    }

    @Test
    void testDelete() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(memberSkillRepository).deleteById(any(UUID.class));

        memberSkillsServices.delete(uuid);

        verify(memberSkillRepository, times(1)).deleteById(any(UUID.class));
    }
}

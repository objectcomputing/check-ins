package com.objectcomputing.checkins.services.memberskills;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.action_item.ActionItemBadArgException;
import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.action_item.ActionItemServicesImpl;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberSkills.*;
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

import javax.inject.Inject;
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

        MemberSkillsBadArgException exception = assertThrows(MemberSkillsBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Found unexpected id %s for member skill", memberSkill.getId()), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveActionItemNullMemberId() {
        MemberSkill memberSkill = new MemberSkill(null, UUID.randomUUID());

        MemberSkillsBadArgException exception = assertThrows(MemberSkillsBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Invalid member skill %s", memberSkill), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveActionItemNullSkillId() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), null);

        MemberSkillsBadArgException exception = assertThrows(MemberSkillsBadArgException.class, () -> memberSkillsServices.save(memberSkill));
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
// currently not working
    @Test
    void testSaveMemberSkillNonExistingSkill() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID());

        when(skillRepository.findById(eq(memberSkill.getSkillid()))).thenReturn(Optional.empty());
        when(memberProfileRepository.findById(eq(memberSkill.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));

        MemberSkillsBadArgException exception = assertThrows(MemberSkillsBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Skill %s doesn't exist", memberSkill.getSkillid()), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(skillRepository, never()).findById(any(UUID.class));
    }

    // currently not working - doesn't call skillRepository.findById
    @Test
    void testSaveActionItemNonExistingMember() {
        MemberSkill memberSkill = new MemberSkill(UUID.randomUUID(), UUID.randomUUID());

        when(skillRepository.findById(eq(memberSkill.getSkillid()))).thenReturn(Optional.of(new Skill()));
        when(memberProfileRepository.findById(eq(memberSkill.getMemberid()))).thenReturn(Optional.empty());

        MemberSkillsBadArgException exception = assertThrows(MemberSkillsBadArgException.class, () -> memberSkillsServices.save(memberSkill));
        assertEquals(String.format("Member Profile %s doesn't exist", memberSkill.getMemberid()), exception.getMessage());

        verify(memberSkillRepository, never()).save(any(MemberSkill.class));
        verify(skillRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
    }

//
//    @Test
//    void testFindByFieldsNullParams() {
//        Set<ActionItem> actionItemSet = Set.of(
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
//        );
//
//        when(actionItemRepository.findAll()).thenReturn(actionItemSet);
//
//        assertEquals(actionItemSet, services.findByFields(null, null));
//
//        verify(actionItemRepository, times(1)).findAll();
//        verify(actionItemRepository, never()).findByCheckinid(any(UUID.class));
//        verify(actionItemRepository, never()).findByCreatedbyid(any(UUID.class));
//    }
//
//    @Test
//    void testFindByFieldsCheckInId() {
//        List<ActionItem> actionItems = List.of(
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
//        );
//
//        List<ActionItem> actionItemsToFind = List.of(actionItems.get(1));
//        ActionItem actionItem = actionItemsToFind.get(0);
//
//        when(actionItemRepository.findAll()).thenReturn(actionItems);
//        when(actionItemRepository.findByCheckinid(actionItem.getCheckinid())).thenReturn(actionItemsToFind);
//
//        assertEquals(new HashSet<>(actionItemsToFind), services.findByFields(actionItem.getCheckinid(), null));
//
//        verify(actionItemRepository, times(1)).findAll();
//        verify(actionItemRepository, times(1)).findByCheckinid(any(UUID.class));
//        verify(actionItemRepository, never()).findByCreatedbyid(any(UUID.class));
//    }
//
//    @Test
//    void testFindByFieldsCreateById() {
//        List<ActionItem> actionItems = List.of(
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
//        );
//
//        List<ActionItem> actionItemsToFind = List.of(actionItems.get(1));
//        ActionItem actionItem = actionItemsToFind.get(0);
//
//        when(actionItemRepository.findAll()).thenReturn(actionItems);
//        when(actionItemRepository.findByCreatedbyid(actionItem.getCreatedbyid())).thenReturn(actionItemsToFind);
//
//        assertEquals(new HashSet<>(actionItemsToFind), services.findByFields(null, actionItem.getCreatedbyid()));
//
//        verify(actionItemRepository, times(1)).findAll();
//        verify(actionItemRepository, times(1)).findByCreatedbyid(any(UUID.class));
//        verify(actionItemRepository, never()).findByCheckinid(any(UUID.class));
//    }
//
//    @Test
//    void testFindByFieldsAll() {
//        List<ActionItem> actionItems = List.of(
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
//        );
//
//        List<ActionItem> actionItemsToFind = List.of(actionItems.get(1));
//
//        ActionItem actionItem = actionItemsToFind.get(0);
//        when(actionItemRepository.findAll()).thenReturn(actionItems);
//        when(actionItemRepository.findByCreatedbyid(actionItem.getCreatedbyid())).thenReturn(actionItemsToFind);
//        when(actionItemRepository.findByCheckinid(actionItem.getCheckinid())).thenReturn(actionItemsToFind);
//
//        assertEquals(new HashSet<>(actionItemsToFind), services
//                .findByFields(actionItem.getCheckinid(), actionItem.getCreatedbyid()));
//
//        verify(actionItemRepository, times(1)).findAll();
//        verify(actionItemRepository, times(1)).findByCreatedbyid(any(UUID.class));
//        verify(actionItemRepository, times(1)).findByCheckinid(any(UUID.class));
//    }
//
//    @Test
//    void testReadAll() {
//        Set<ActionItem> actionItems = Set.of(
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
//                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
//        );
//
//        when(actionItemRepository.findAll()).thenReturn(actionItems);
//
//        assertEquals(actionItems, services.readAll());
//
//        verify(actionItemRepository, times(1)).findAll();
//    }
//
//    @Test
//    void testDelete() {
//        UUID uuid = UUID.randomUUID();
//
//        doAnswer(an -> {
//            assertEquals(uuid, an.getArgument(0));
//            return null;
//        }).when(actionItemRepository).deleteById(any(UUID.class));
//
//        services.delete(uuid);
//
//        verify(actionItemRepository, times(1)).deleteById(any(UUID.class));
//    }
}

package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
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
class ActionItemServicesImplTest {

    @Mock
    private CheckInRepository checkinRepository;

    @Mock
    private ActionItemRepository actionItemRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private ActionItemServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(checkinRepository, actionItemRepository, memberProfileRepository);
    }

    @Test
    void testRead() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(actionItemRepository.findById(actionItem.getId())).thenReturn(Optional.of(actionItem));

        assertEquals(actionItem, services.read(actionItem.getId()));

        verify(actionItemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));

        verify(actionItemRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(actionItem.getCheckinid()))).thenReturn(Optional.of(checkin));
        when(memberProfileRepository.findById(eq(actionItem.getCreatedbyid()))).thenReturn(Optional.of(new MemberProfile()));
        when(actionItemRepository.save(eq(actionItem))).thenReturn(actionItem);

        assertEquals(actionItem, services.save(actionItem));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(actionItemRepository, times(1)).save(any(ActionItem.class));
    }

    @Test
    void testSaveWithId() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.save(actionItem));
        assertEquals(String.format("Found unexpected id %s for action item", actionItem.getId()), exception.getMessage());

        verify(actionItemRepository, never()).save(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveActionItemNullCheckInId() {
        ActionItem actionItem = new ActionItem(null, UUID.randomUUID(), "dnc");

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.save(actionItem));
        assertEquals(String.format("Invalid actionItem %s", actionItem), exception.getMessage());

        verify(actionItemRepository, never()).save(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveActionItemNullCreateById() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), null, "dnc");

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.save(actionItem));
        assertEquals(String.format("Invalid actionItem %s", actionItem), exception.getMessage());

        verify(actionItemRepository, never()).save(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveNullActionItem() {
        assertNull(services.save(null));

        verify(actionItemRepository, never()).save(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveActionItemNonExistingCheckIn() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(checkinRepository.findById(eq(actionItem.getCheckinid()))).thenReturn(Optional.empty());

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.save(actionItem));
        assertEquals(String.format("CheckIn %s doesn't exist", actionItem.getCheckinid()), exception.getMessage());

        verify(actionItemRepository, never()).save(any(ActionItem.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveActionItemNonExistingMember() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(checkinRepository.findById(eq(actionItem.getCheckinid()))).thenReturn(Optional.of(new CheckIn()));
        when(memberProfileRepository.findById(eq(actionItem.getCreatedbyid()))).thenReturn(Optional.empty());

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.save(actionItem));
        assertEquals(String.format("Member %s doesn't exist", actionItem.getCreatedbyid()), exception.getMessage());

        verify(actionItemRepository, never()).save(any(ActionItem.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdate() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(actionItem.getCheckinid()))).thenReturn(Optional.of(checkin));
        when(memberProfileRepository.findById(eq(actionItem.getCreatedbyid()))).thenReturn(Optional.of(new MemberProfile()));
        when(actionItemRepository.findById(actionItem.getId())).thenReturn(Optional.of(actionItem));
        when(actionItemRepository.update(eq(actionItem))).thenReturn(actionItem);

        assertEquals(actionItem, services.update(actionItem));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(actionItemRepository, times(1)).findById(any(UUID.class));
        verify(actionItemRepository, times(1)).update(any(ActionItem.class));
    }

    @Test
    void testUpdateWithoutId() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.update(actionItem));
        assertEquals(String.format("Unable to locate actionItem to update with id %s", actionItem.getId()), exception.getMessage());

        verify(actionItemRepository, never()).update(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(actionItemRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateActionItemNullCheckInId() {
        ActionItem actionItem = new ActionItem(null, UUID.randomUUID(), "dnc");

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.update(actionItem));
        assertEquals(String.format("Invalid actionItem %s", actionItem), exception.getMessage());

        verify(actionItemRepository, never()).update(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(actionItemRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateActionItemNullCreateById() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), null, "dnc");

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.update(actionItem));
        assertEquals(String.format("Invalid actionItem %s", actionItem), exception.getMessage());

        verify(actionItemRepository, never()).update(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(actionItemRepository, never()).findById(any(UUID.class));
    }


    @Test
    void testUpdateActionItemDoesNotExist() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(actionItemRepository.findById(eq(actionItem.getCheckinid()))).thenReturn(Optional.empty());

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.update(actionItem));
        assertEquals(String.format("Unable to locate actionItem to update with id %s", actionItem.getId()), exception.getMessage());

        verify(actionItemRepository, never()).update(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(actionItemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckInDoesNotExist() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(actionItemRepository.findById(eq(actionItem.getId()))).thenReturn(Optional.of(actionItem));
        when(checkinRepository.findById(eq(actionItem.getCheckinid()))).thenReturn(Optional.empty());

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.update(actionItem));
        assertEquals(String.format("CheckIn %s doesn't exist", actionItem.getCheckinid()), exception.getMessage());

        verify(actionItemRepository, never()).update(any(ActionItem.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(actionItemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateMemberDoesNotExist() {
        ActionItem actionItem = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(actionItemRepository.findById(eq(actionItem.getId()))).thenReturn(Optional.of(actionItem));
        when(checkinRepository.findById(eq(actionItem.getCheckinid()))).thenReturn(Optional.of(new CheckIn()));
        when(memberProfileRepository.findById(eq(actionItem.getCreatedbyid()))).thenReturn(Optional.empty());

        ActionItemBadArgException exception = assertThrows(ActionItemBadArgException.class, () -> services.update(actionItem));
        assertEquals(String.format("Member %s doesn't exist", actionItem.getCreatedbyid()), exception.getMessage());

        verify(actionItemRepository, never()).update(any(ActionItem.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(actionItemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateNullActionItem() {
        assertNull(services.update(null));

        verify(actionItemRepository, never()).update(any(ActionItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(actionItemRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testFindByFieldsNullParams() {
        Set<ActionItem> actionItemSet = Set.of(
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        when(actionItemRepository.findAll()).thenReturn(actionItemSet);

        assertEquals(actionItemSet, services.findByFields(null, null));

        verify(actionItemRepository, times(1)).findAll();
        verify(actionItemRepository, never()).findByCheckinid(any(UUID.class));
        verify(actionItemRepository, never()).findByCreatedbyid(any(UUID.class));
    }

    @Test
    void testFindByFieldsCheckInId() {
        List<ActionItem> actionItems = List.of(
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        List<ActionItem> actionItemsToFind = List.of(actionItems.get(1));
        ActionItem actionItem = actionItemsToFind.get(0);

        when(actionItemRepository.findAll()).thenReturn(actionItems);
        when(actionItemRepository.findByCheckinid(actionItem.getCheckinid())).thenReturn(actionItemsToFind);

        assertEquals(new HashSet<>(actionItemsToFind), services.findByFields(actionItem.getCheckinid(), null));

        verify(actionItemRepository, times(1)).findAll();
        verify(actionItemRepository, times(1)).findByCheckinid(any(UUID.class));
        verify(actionItemRepository, never()).findByCreatedbyid(any(UUID.class));
    }

    @Test
    void testFindByFieldsCreateById() {
        List<ActionItem> actionItems = List.of(
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        List<ActionItem> actionItemsToFind = List.of(actionItems.get(1));
        ActionItem actionItem = actionItemsToFind.get(0);

        when(actionItemRepository.findAll()).thenReturn(actionItems);
        when(actionItemRepository.findByCreatedbyid(actionItem.getCreatedbyid())).thenReturn(actionItemsToFind);

        assertEquals(new HashSet<>(actionItemsToFind), services.findByFields(null, actionItem.getCreatedbyid()));

        verify(actionItemRepository, times(1)).findAll();
        verify(actionItemRepository, times(1)).findByCreatedbyid(any(UUID.class));
        verify(actionItemRepository, never()).findByCheckinid(any(UUID.class));
    }

    @Test
    void testFindByFieldsAll() {
        List<ActionItem> actionItems = List.of(
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        List<ActionItem> actionItemsToFind = List.of(actionItems.get(1));

        ActionItem actionItem = actionItemsToFind.get(0);
        when(actionItemRepository.findAll()).thenReturn(actionItems);
        when(actionItemRepository.findByCreatedbyid(actionItem.getCreatedbyid())).thenReturn(actionItemsToFind);
        when(actionItemRepository.findByCheckinid(actionItem.getCheckinid())).thenReturn(actionItemsToFind);

        assertEquals(new HashSet<>(actionItemsToFind), services
                .findByFields(actionItem.getCheckinid(), actionItem.getCreatedbyid()));

        verify(actionItemRepository, times(1)).findAll();
        verify(actionItemRepository, times(1)).findByCreatedbyid(any(UUID.class));
        verify(actionItemRepository, times(1)).findByCheckinid(any(UUID.class));
    }

    @Test
    void testReadAll() {
        Set<ActionItem> actionItems = Set.of (
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        when(actionItemRepository.findAll()).thenReturn(actionItems);

        assertEquals(actionItems, services.readAll());

        verify(actionItemRepository, times(1)).findAll();
    }

    @Test
    void testDelete() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(actionItemRepository).deleteById(any(UUID.class));

        services.delete(uuid);

        verify(actionItemRepository, times(1)).deleteById(any(UUID.class));
    }
}

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
    void testSaveWithExistingDisplayOrder() {
        ActionItem currentActionItem = new ActionItem(
                UUID.fromString("35fb243a-eaa0-4c89-abca-2e088e03fb05"),
                UUID.fromString("33d6110b-a468-41f7-9b69-feadaa6fe0e1"),
                "I was already here!");
        currentActionItem.setPriority(6.0);
        ActionItem actionItem = new ActionItem(
                UUID.fromString("35fb243a-eaa0-4c89-abca-2e088e03fb05"),
                UUID.fromString("33d6110b-a468-41f7-9b69-feadaa6fe0e1"),
                "Described!");

        actionItem.setPriority(7.0);

        when(checkinRepository.findById(actionItem.getCheckinid())).thenReturn(Optional.of(new CheckIn()));
        when(memberProfileRepository.findById(actionItem.getCheckinid())).thenReturn(Optional.of(new MemberProfile()));
        when(actionItemRepository.findMaxPriorityByCheckinid(actionItem.getCheckinid())).thenReturn(Optional.of(currentActionItem.getPriority()));
        when(actionItemRepository.save(actionItem)).thenReturn(actionItem);

        ActionItem result = actionItemRepository.save(actionItem);

        assertEquals(7, result.getPriority());
    }

    @Test
    void testSaveNoExistingDisplayOrder() {
        ActionItem actionItem = new ActionItem(
                UUID.fromString("35fb243a-eaa0-4c89-abca-2e088e03fb05"),
                UUID.fromString("33d6110b-a468-41f7-9b69-feadaa6fe0e1"),
                "Described!");

        actionItem.setPriority(1.0);

        when(checkinRepository.findById(actionItem.getCheckinid())).thenReturn(Optional.of(new CheckIn()));
        when(memberProfileRepository.findById(actionItem.getCheckinid())).thenReturn(Optional.of(new MemberProfile()));
        when(actionItemRepository.findMaxPriorityByCheckinid(actionItem.getCheckinid())).thenReturn(Optional.empty());
        when(actionItemRepository.save(actionItem)).thenReturn(actionItem);

        ActionItem result = actionItemRepository.save(actionItem);

        assertEquals(1.0, result.getPriority());
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
        List<ActionItem> actionItemList = List.of(
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc", 1.0),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc", 2.0),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc", 3.0)
        );

        when(actionItemRepository.search(null, null)).thenReturn(actionItemList);

        Set<ActionItem> result = services.findByFields(null, null);
        int i = 0;

        for (ActionItem setItem : result) {
            assertEquals(actionItemList.get(i++), setItem);
        }
    }

    @Test
    void testFindByFieldsCheckInId() {
        ActionItem actionItemToFind = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(actionItemRepository.search(actionItemToFind.getCheckinid().toString(), null)).thenReturn(List.of(actionItemToFind));

        assertEquals(Set.of(actionItemToFind), services.findByFields(actionItemToFind.getCheckinid(), null));
    }

    @Test
    void testFindByFieldsCreateById() {
        ActionItem actionItemToFind = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");
        Set<ActionItem> actionItems = Set.of(actionItemToFind,
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        when(actionItemRepository.findAll()).thenReturn(actionItems);
        when(actionItemRepository.search(null, actionItemToFind.getCreatedbyid().toString())).thenReturn(List.of(actionItemToFind));

        assertEquals(Set.of(actionItemToFind), services.findByFields(null, actionItemToFind.getCreatedbyid()));
    }

    @Test
    void testFindByFieldsAll() {
        ActionItem actionItemToFind = new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        ActionItem actionItem = actionItemToFind;
        when(actionItemRepository.search(actionItem.getCheckinid().toString(), actionItem.getCreatedbyid().toString())).thenReturn(List.of(actionItemToFind));

        assertEquals(Set.of(actionItemToFind), services
                .findByFields(actionItem.getCheckinid(), actionItem.getCreatedbyid()));
    }

    @Test
    void testReadAll() {
        Set<ActionItem> actionItems = Set.of(
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new ActionItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        when(actionItemRepository.findAll()).thenReturn(actionItems);

//        assertEquals(actionItems, services.readAll());

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

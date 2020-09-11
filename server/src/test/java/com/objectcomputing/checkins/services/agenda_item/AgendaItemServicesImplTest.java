package com.objectcomputing.checkins.services.agenda_item;

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
class AgendaItemServicesImplTest {

    @Mock
    private CheckInRepository checkinRepository;

    @Mock
    private AgendaItemRepository agendaItemRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private AgendaItemServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(checkinRepository, agendaItemRepository, memberProfileRepository);
    }

    @Test
    void testRead() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(agendaItemRepository.findById(agendaItem.getId())).thenReturn(Optional.of(agendaItem));

        assertEquals(agendaItem, services.read(agendaItem.getId()));

        verify(agendaItemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));

        verify(agendaItemRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(agendaItem.getCheckinid()))).thenReturn(Optional.of(checkin));
        when(memberProfileRepository.findById(eq(agendaItem.getCreatedbyid()))).thenReturn(Optional.of(new MemberProfile()));
        when(agendaItemRepository.save(eq(agendaItem))).thenReturn(agendaItem);

        assertEquals(agendaItem, services.save(agendaItem));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(agendaItemRepository, times(1)).save(any(AgendaItem.class));
    }

    @Test
    void testSaveWithId() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.save(agendaItem));
        assertEquals(String.format("Found unexpected id %s for agenda item", agendaItem.getId()), exception.getMessage());

        verify(agendaItemRepository, never()).save(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveAgendaItemNullCheckInId() {
        AgendaItem agendaItem = new AgendaItem(null, UUID.randomUUID(), "dnc");

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.save(agendaItem));
        assertEquals(String.format("Invalid agendaItem %s", agendaItem), exception.getMessage());

        verify(agendaItemRepository, never()).save(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveAgendaItemNullCreateById() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), null, "dnc");

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.save(agendaItem));
        assertEquals(String.format("Invalid agendaItem %s", agendaItem), exception.getMessage());

        verify(agendaItemRepository, never()).save(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveNullAgendaItem() {
        assertNull(services.save(null));

        verify(agendaItemRepository, never()).save(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveAgendaItemNonExistingCheckIn() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(checkinRepository.findById(eq(agendaItem.getCheckinid()))).thenReturn(Optional.empty());

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.save(agendaItem));
        assertEquals(String.format("CheckIn %s doesn't exist", agendaItem.getCheckinid()), exception.getMessage());

        verify(agendaItemRepository, never()).save(any(AgendaItem.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveAgendaItemNonExistingMember() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(checkinRepository.findById(eq(agendaItem.getCheckinid()))).thenReturn(Optional.of(new CheckIn()));
        when(memberProfileRepository.findById(eq(agendaItem.getCreatedbyid()))).thenReturn(Optional.empty());

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.save(agendaItem));
        assertEquals(String.format("Member %s doesn't exist", agendaItem.getCreatedbyid()), exception.getMessage());

        verify(agendaItemRepository, never()).save(any(AgendaItem.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdate() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(agendaItem.getCheckinid()))).thenReturn(Optional.of(checkin));
        when(memberProfileRepository.findById(eq(agendaItem.getCreatedbyid()))).thenReturn(Optional.of(new MemberProfile()));
        when(agendaItemRepository.findById(agendaItem.getId())).thenReturn(Optional.of(agendaItem));
        when(agendaItemRepository.update(eq(agendaItem))).thenReturn(agendaItem);

        assertEquals(agendaItem, services.update(agendaItem));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(agendaItemRepository, times(1)).findById(any(UUID.class));
        verify(agendaItemRepository, times(1)).update(any(AgendaItem.class));
    }

    @Test
    void testUpdateWithoutId() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.update(agendaItem));
        assertEquals(String.format("Unable to locate agendaItem to update with id %s", agendaItem.getId()), exception.getMessage());

        verify(agendaItemRepository, never()).update(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(agendaItemRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateAgendaItemNullCheckInId() {
        AgendaItem agendaItem = new AgendaItem(null, UUID.randomUUID(), "dnc");

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.update(agendaItem));
        assertEquals(String.format("Invalid agendaItem %s", agendaItem), exception.getMessage());

        verify(agendaItemRepository, never()).update(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(agendaItemRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateAgendaItemNullCreateById() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), null, "dnc");

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.update(agendaItem));
        assertEquals(String.format("Invalid agendaItem %s", agendaItem), exception.getMessage());

        verify(agendaItemRepository, never()).update(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(agendaItemRepository, never()).findById(any(UUID.class));
    }


    @Test
    void testUpdateAgendaItemDoesNotExist() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(agendaItemRepository.findById(eq(agendaItem.getCheckinid()))).thenReturn(Optional.empty());

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.update(agendaItem));
        assertEquals(String.format("Unable to locate agendaItem to update with id %s", agendaItem.getId()), exception.getMessage());

        verify(agendaItemRepository, never()).update(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(agendaItemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckInDoesNotExist() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(agendaItemRepository.findById(eq(agendaItem.getId()))).thenReturn(Optional.of(agendaItem));
        when(checkinRepository.findById(eq(agendaItem.getCheckinid()))).thenReturn(Optional.empty());

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.update(agendaItem));
        assertEquals(String.format("CheckIn %s doesn't exist", agendaItem.getCheckinid()), exception.getMessage());

        verify(agendaItemRepository, never()).update(any(AgendaItem.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(agendaItemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateMemberDoesNotExist() {
        AgendaItem agendaItem = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "dnc");
        when(agendaItemRepository.findById(eq(agendaItem.getId()))).thenReturn(Optional.of(agendaItem));
        when(checkinRepository.findById(eq(agendaItem.getCheckinid()))).thenReturn(Optional.of(new CheckIn()));
        when(memberProfileRepository.findById(eq(agendaItem.getCreatedbyid()))).thenReturn(Optional.empty());

        AgendaItemBadArgException exception = assertThrows(AgendaItemBadArgException.class, () -> services.update(agendaItem));
        assertEquals(String.format("Member %s doesn't exist", agendaItem.getCreatedbyid()), exception.getMessage());

        verify(agendaItemRepository, never()).update(any(AgendaItem.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(agendaItemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateNullAgendaItem() {
        assertNull(services.update(null));

        verify(agendaItemRepository, never()).update(any(AgendaItem.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(agendaItemRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testFindByFieldsNullParams() {
        Set<AgendaItem> agendaItemSet = Set.of(
                new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        when(agendaItemRepository.search(null, null)).thenReturn(agendaItemSet);

        assertEquals(agendaItemSet, services.findByFields(null, null));

    }

    @Test
    void testFindByFieldsCheckInId() {
        AgendaItem agendaItemToFind = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        when(agendaItemRepository.search(agendaItemToFind.getCheckinid().toString(), null)).thenReturn(Set.of(agendaItemToFind));

        assertEquals(Set.of(agendaItemToFind), services.findByFields(agendaItemToFind.getCheckinid(), null));
    }

    @Test
    void testFindByFieldsCreateById() {
        AgendaItem agendaItemToFind = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");
        Set<AgendaItem> agendaItems = Set.of(agendaItemToFind,
                new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        when(agendaItemRepository.findAll()).thenReturn(agendaItems);
        when(agendaItemRepository.search(null, agendaItemToFind.getCreatedbyid().toString())).thenReturn(Set.of(agendaItemToFind));

        assertEquals(Set.of(agendaItemToFind), services.findByFields(null, agendaItemToFind.getCreatedbyid()));
    }

    @Test
    void testFindByFieldsAll() {
        AgendaItem agendaItemToFind = new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc");

        AgendaItem agendaItem = agendaItemToFind;
        when(agendaItemRepository.search(agendaItem.getCheckinid().toString(), agendaItem.getCreatedbyid().toString())).thenReturn(Set.of(agendaItemToFind));

        assertEquals(Set.of(agendaItemToFind), services
                .findByFields(agendaItem.getCheckinid(), agendaItem.getCreatedbyid()));
    }

    @Test
    void testReadAll() {
        Set<AgendaItem> agendaItems = Set.of(
                new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc"),
                new AgendaItem(UUID.randomUUID(), UUID.randomUUID(), "dnc")
        );

        when(agendaItemRepository.findAll()).thenReturn(agendaItems);

        assertEquals(agendaItems, services.readAll());

        verify(agendaItemRepository, times(1)).findAll();
    }

    @Test
    void testDelete() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(agendaItemRepository).deleteById(any(UUID.class));

        services.delete(uuid);

        verify(agendaItemRepository, times(1)).deleteById(any(UUID.class));
    }
}

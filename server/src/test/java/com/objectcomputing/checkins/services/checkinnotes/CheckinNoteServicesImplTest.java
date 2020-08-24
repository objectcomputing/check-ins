package com.objectcomputing.checkins.services.checkinnotes;

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
public class CheckinNoteServicesImplTest {

    @Mock
    private CheckInRepository checkinRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @Mock
    private CheckinNoteRepository  checkinNoteRepository;

    @InjectMocks
    private CheckinNoteServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
      Mockito.reset(checkinRepository,checkinNoteRepository,memberProfileRepository);
    }

    @Test
    void testRead() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        when(checkinNoteRepository.findById(checkinNote.getId())).thenReturn(Optional.of(checkinNote));

        assertEquals(checkinNote, services.read(checkinNote.getId()));

        verify(checkinNoteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));

        verify(checkinNoteRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(checkinNote.getCheckinid()))).thenReturn(Optional.of(checkin));
        when(memberProfileRepository.findById(eq(checkinNote.getCreatedbyid()))).thenReturn(Optional.of(new MemberProfile()));
        when(checkinNoteRepository.save(eq(checkinNote))).thenReturn(checkinNote);

        assertEquals(checkinNote, services.save(checkinNote));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(checkinNoteRepository, times(1)).save(any(CheckinNote.class));
    }

    @Test
    void testSaveWithId() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.save(checkinNote));
        assertEquals(String.format("Found unexpected id %s for check in note", checkinNote.getId()), exception.getMessage());

        verify(checkinNoteRepository, never()).save(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinNoteNullCheckInId() {
        CheckinNote checkinNote = new CheckinNote(null, UUID.randomUUID(), "test");

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.save(checkinNote));
        assertEquals(String.format("Invalid checkin note %s", checkinNote), exception.getMessage());

        verify(checkinNoteRepository, never()).save(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinNoteNullCreateById() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), null, "test");

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.save(checkinNote));
        assertEquals(String.format("Invalid checkin note %s", checkinNote), exception.getMessage());

        verify(checkinNoteRepository, never()).save(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveNullCheckinNote() {
        assertNull(services.save(null));

        verify(checkinNoteRepository, never()).save(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinNoteNonExistingCheckIn() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test");

        when(checkinRepository.findById(eq(checkinNote.getCheckinid()))).thenReturn(Optional.empty());

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.save(checkinNote));
        assertEquals(String.format("CheckIn %s doesn't exist", checkinNote.getCheckinid()), exception.getMessage());

        verify(checkinNoteRepository, never()).save(any(CheckinNote.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinNoteNonExistingMember() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test");

        when(checkinRepository.findById(eq(checkinNote.getCheckinid()))).thenReturn(Optional.of(new CheckIn()));
        when(memberProfileRepository.findById(eq(checkinNote.getCreatedbyid()))).thenReturn(Optional.empty());

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.save(checkinNote));
        assertEquals(String.format("Member %s doesn't exist", checkinNote.getCreatedbyid()), exception.getMessage());

        verify(checkinNoteRepository, never()).save(any(CheckinNote.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdate() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(checkinNote.getCheckinid()))).thenReturn(Optional.of(checkin));
        when(memberProfileRepository.findById(eq(checkinNote.getCreatedbyid()))).thenReturn(Optional.of(new MemberProfile()));
        when(checkinNoteRepository.findById(checkinNote.getId())).thenReturn(Optional.of(checkinNote));
        when(checkinNoteRepository.update(eq(checkinNote))).thenReturn(checkinNote);

        assertEquals(checkinNote, services.update(checkinNote));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(checkinNoteRepository, times(1)).findById(any(UUID.class));
        verify(checkinNoteRepository, times(1)).update(any(CheckinNote.class));
    }

    @Test
    void testUpdateWithoutId() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test");

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.update(checkinNote));
        assertEquals(String.format("Unable to locate checkin note to update with id %s", checkinNote.getId()), exception.getMessage());

        verify(checkinNoteRepository, never()).update(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(checkinNoteRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckinNoteNullCheckInId() {
        CheckinNote checkinNote = new CheckinNote(null, UUID.randomUUID(), "test");

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.update(checkinNote));
        assertEquals(String.format("Invalid checkin note %s", checkinNote), exception.getMessage());

        verify(checkinNoteRepository, never()).update(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(checkinNoteRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckinNoteNullCreateById() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), null, "test");

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.update(checkinNote));
        assertEquals(String.format("Invalid checkin note %s", checkinNote), exception.getMessage());

        verify(checkinNoteRepository, never()).update(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(checkinNoteRepository, never()).findById(any(UUID.class));
    }


    @Test
    void testUpdateCheckinNoteDoesNotExist() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");
        when(checkinNoteRepository.findById(eq(checkinNote.getCheckinid()))).thenReturn(Optional.empty());

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.update(checkinNote));
        assertEquals(String.format("Unable to locate checkin note to update with id %s", checkinNote.getId()), exception.getMessage());

        verify(checkinNoteRepository, never()).update(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(checkinNoteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckInDoesNotExist() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");
        when(checkinNoteRepository.findById(eq(checkinNote.getId()))).thenReturn(Optional.of(checkinNote));
        when(checkinRepository.findById(eq(checkinNote.getCheckinid()))).thenReturn(Optional.empty());

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.update(checkinNote));
        assertEquals(String.format("CheckIn %s doesn't exist", checkinNote.getCheckinid()), exception.getMessage());

        verify(checkinNoteRepository, never()).update(any(CheckinNote.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(checkinNoteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateMemberDoesNotExist() {
        CheckinNote checkinNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "test");
        when(checkinNoteRepository.findById(eq(checkinNote.getId()))).thenReturn(Optional.of(checkinNote));
        when(checkinRepository.findById(eq(checkinNote.getCheckinid()))).thenReturn(Optional.of(new CheckIn()));
        when(memberProfileRepository.findById(eq(checkinNote.getCreatedbyid()))).thenReturn(Optional.empty());

        CheckinNotesBadArgException exception = assertThrows(CheckinNotesBadArgException.class, () -> services.update(checkinNote));
        assertEquals(String.format("Member %s doesn't exist", checkinNote.getCreatedbyid()), exception.getMessage());

        verify(checkinNoteRepository, never()).update(any(CheckinNote.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(checkinNoteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateNullCheckinNote() {
        assertNull(services.update(null));

        verify(checkinNoteRepository, never()).update(any(CheckinNote.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(checkinNoteRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testFindByFieldsNullParams() {
        Set<CheckinNote> checkinNoteSet = Set.of(
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test"),
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test"),
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test")
        );

        when(checkinNoteRepository.findAll()).thenReturn(checkinNoteSet);

        assertEquals(checkinNoteSet, services.findByFields(null, null));

        verify(checkinNoteRepository, times(1)).findAll();
        verify(checkinNoteRepository, never()).findByCheckinid(any(UUID.class));
        verify(checkinNoteRepository, never()).findByCreatedbyid(any(UUID.class));
    }

    @Test
    void testFindByFieldsCheckInId() {
        List<CheckinNote> checkinNotes = List.of(
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test"),
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test"),
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test")
        );

        List<CheckinNote> checkinNotesToFind = List.of(checkinNotes.get(1));
        CheckinNote checkinNote = checkinNotesToFind.get(0);

        when(checkinNoteRepository.findAll()).thenReturn(checkinNotes);
        when(checkinNoteRepository.findByCheckinid(checkinNote.getCheckinid())).thenReturn(checkinNotesToFind);

        assertEquals(new HashSet<>(checkinNotesToFind), services.findByFields(checkinNote.getCheckinid(), null));

        verify(checkinNoteRepository, times(1)).findAll();
        verify(checkinNoteRepository, times(1)).findByCheckinid(any(UUID.class));
        verify(checkinNoteRepository, never()).findByCreatedbyid(any(UUID.class));
    }

    @Test
    void testFindByFieldsCreateById() {
        List<CheckinNote> checkinNotes = List.of(
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test"),
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test"),
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test")
        );

        List<CheckinNote> checkinNotesToFind = List.of(checkinNotes.get(1));
        CheckinNote checkinNote = checkinNotesToFind.get(0);

        when(checkinNoteRepository.findAll()).thenReturn(checkinNotes);
        when(checkinNoteRepository.findByCreatedbyid(checkinNote.getCreatedbyid())).thenReturn(checkinNotesToFind);

        assertEquals(new HashSet<>(checkinNotesToFind), services.findByFields(null, checkinNote.getCreatedbyid()));

        verify(checkinNoteRepository, times(1)).findAll();
        verify(checkinNoteRepository, times(1)).findByCreatedbyid(any(UUID.class));
        verify(checkinNoteRepository, never()).findByCheckinid(any(UUID.class));
    }

    @Test
    void testFindByFieldsAll() {
        List<CheckinNote> checkinNotes = List.of(
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test"),
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test"),
                new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), "test")
        );

        List<CheckinNote> checkinNotesToFind = List.of(checkinNotes.get(1));

        CheckinNote checkinNote = checkinNotesToFind.get(0);
        when(checkinNoteRepository.findAll()).thenReturn(checkinNotes);
        when(checkinNoteRepository.findByCreatedbyid(checkinNote.getCreatedbyid())).thenReturn(checkinNotesToFind);
        when(checkinNoteRepository.findByCheckinid(checkinNote.getCheckinid())).thenReturn(checkinNotesToFind);

        assertEquals(new HashSet<>(checkinNotesToFind), services
                .findByFields(checkinNote.getCheckinid(), checkinNote.getCreatedbyid()));

        verify(checkinNoteRepository, times(1)).findAll();
        verify(checkinNoteRepository, never()).findByCreatedbyid(any(UUID.class));
        verify(checkinNoteRepository, times(1)).findByCheckinid(any(UUID.class));
    }


    @Test
    void testDelete() {
        UUID uuid = UUID.randomUUID();

        doAnswer(an -> {
            assertEquals(uuid, an.getArgument(0));
            return null;
        }).when(checkinNoteRepository).deleteById(any(UUID.class));

        services.delete(uuid);

        verify(checkinNoteRepository, times(1)).deleteById(any(UUID.class));
    }
}

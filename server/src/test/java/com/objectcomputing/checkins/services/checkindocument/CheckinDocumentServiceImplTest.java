package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckinDocumentServiceImplTest {

    @Mock
    private CheckInRepository checkinRepository;

    @Mock
    private CheckinDocumentRepository checkinDocumentRepository;

    @InjectMocks
    private CheckinDocumentServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(checkinRepository, checkinDocumentRepository);
    }

    @Test
    void testRead() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "exampleDocId");

        Set<CheckinDocument> checkinDocumentSet = Set.of(
                new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "doc1"),
                new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "doc2"),
                new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "doc3")
        );

        when(checkinDocumentRepository.findByCheckinsId(cd.getCheckinsId())).thenReturn(checkinDocumentSet);

        assertEquals(checkinDocumentSet, services.read(cd.getCheckinsId()));

        verify(checkinDocumentRepository, times(1)).findByCheckinsId(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertTrue(services.read(null).isEmpty());

        verify(checkinDocumentRepository, never()).findByCheckinsId(any(UUID.class));
    }

    @Test
    void testSave() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), "docId");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(cd.getCheckinsId()))).thenReturn(Optional.of(checkin));
        when(checkinDocumentRepository.save(eq(cd))).thenReturn(cd);

        assertEquals(cd, services.save(cd));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(checkinDocumentRepository, times(1)).save(any(CheckinDocument.class));
    }

    @Test
    void testSaveWithId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "docId");

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Found unexpected CheckinDocument id %s, please try updating instead", cd.getId()), exception.getMessage());

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinDocumentNullCheckinsId() {
        CheckinDocument cd = new CheckinDocument(null, "docId");

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinDocumentNullUploadDocId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), null);

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveNullCheckinDocument() {
        assertNull(services.save(null));

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinDocumentNonExistingCheckIn() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), "docId");

        when(checkinRepository.findById(eq(cd.getCheckinsId()))).thenReturn(Optional.empty());

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("CheckIn %s doesn't exist", cd.getCheckinsId()), exception.getMessage());

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinDocumentExistingUploadDocId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), "docId");

        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(cd.getCheckinsId()))).thenReturn(Optional.of(checkin));
        when(checkinDocumentRepository.findByUploadDocId(eq(cd.getUploadDocId()))).thenReturn(Optional.of(cd));

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("CheckinDocument with document ID %s already exists", cd.getUploadDocId()), exception.getMessage());

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdate() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "docId");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(eq(cd.getCheckinsId()))).thenReturn(Optional.of(checkin));
        when(checkinDocumentRepository.findById(cd.getId())).thenReturn(Optional.of(cd));
        when(checkinDocumentRepository.update(eq(cd))).thenReturn(cd);

        assertEquals(cd, services.update(cd));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(checkinDocumentRepository, times(1)).findById(cd.getId());
        verify(checkinDocumentRepository, times(1)).update(any(CheckinDocument.class));
    }

    @Test
    void testUpdateWithoutId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), "docId");

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("CheckinDocument id %s not found, please try inserting instead", cd.getId()), exception.getMessage());

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckinDocumentNullCheckinsId() {
        CheckinDocument cd = new CheckinDocument(null, "docId");

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckinDocumentNullUploadDocId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), null);

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckinDocumentDoesNotExist() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "docId");
        when(checkinDocumentRepository.findById(eq(cd.getCheckinsId()))).thenReturn(Optional.empty());

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("CheckinDocument id %s not found, please try inserting instead", cd.getId()), exception.getMessage());

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckInDoesNotExist() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "docId");
        when(checkinDocumentRepository.findById(eq(cd.getId()))).thenReturn(Optional.of(cd));
        when(checkinRepository.findById(eq(cd.getCheckinsId()))).thenReturn(Optional.empty());

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("CheckIn %s doesn't exist", cd.getCheckinsId()), exception.getMessage());

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateNullCheckinDocument() {
        assertNull(services.update(null));

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testDelete() {

        when(checkinDocumentRepository.existsByCheckinsId(any(UUID.class))).thenReturn(true);

        services.deleteByCheckinId(UUID.randomUUID());

        verify(checkinDocumentRepository, times(1)).deleteByCheckinsId(any(UUID.class));
    }

    @Test
    void testDeleteNonExistingCheckinsId() {
        UUID uuid = UUID.randomUUID();

        when(checkinDocumentRepository.existsByCheckinsId(any(UUID.class))).thenReturn(false);

        CheckinDocumentBadArgException exception = assertThrows(CheckinDocumentBadArgException.class, () -> services.deleteByCheckinId(uuid));
        assertEquals(String.format("CheckinDocument with CheckinsId %s does not exist", uuid), exception.getMessage());

        verify(checkinDocumentRepository, times(0)).deleteByCheckinsId(any(UUID.class));
    }
}

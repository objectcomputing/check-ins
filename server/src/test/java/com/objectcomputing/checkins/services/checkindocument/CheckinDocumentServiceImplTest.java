package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Disabled in nativeTest, as we get an exception from Mockito
// => org.graalvm.nativeimage.MissingReflectionRegistrationError: The program tried to reflectively access the proxy class
// inheriting [org.mockito.plugins.MockMaker] without it being registered for runtime reflection
@DisabledInNativeImage
class CheckinDocumentServiceImplTest extends TestContainersSuite {

    @Mock
    private CheckInRepository checkinRepository;

    @Mock
    private CheckinDocumentRepository checkinDocumentRepository;

    @Mock
    private CurrentUserServices currentUserServices;

    @InjectMocks
    private CheckinDocumentServicesImpl services;

    private AutoCloseable mockFinalizer;

    @BeforeAll
    void initMocks() {
        mockFinalizer = MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        reset(checkinRepository, checkinDocumentRepository, currentUserServices);
    }

    @AfterAll
    void close() throws Exception {
        mockFinalizer.close();
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
    void testFindByUploadDocId() {

        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "exampleDocId");
        when(checkinDocumentRepository.findByUploadDocId(any(String.class))).thenReturn(Optional.of(cd));
        assertEquals(cd, services.getFindByUploadDocId(cd.getUploadDocId()));
        verify(checkinDocumentRepository, times(1)).findByUploadDocId(any(String.class));
    }

    @Test
    void testFindByUploadDocIdWhenRecordDoesNotExist() {

        String id = "some.id";
        when(checkinDocumentRepository.findByUploadDocId(any(String.class))).thenReturn(Optional.empty());
        BadArgException exception = assertThrows(BadArgException.class, () -> services.getFindByUploadDocId(id));
        assertEquals(String.format("CheckinDocument with document id %s does not exist", id), exception.getMessage());
        verify(checkinDocumentRepository, times(1)).findByUploadDocId(any(String.class));
    }

    @Test
    void testSave() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), "docId");
        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(cd.getCheckinsId())).thenReturn(Optional.of(checkin));
        when(checkinDocumentRepository.save(cd)).thenReturn(cd);

        assertEquals(cd, services.save(cd));

        verify(checkinRepository, times(1)).findById(any(UUID.class));
        verify(checkinDocumentRepository, times(1)).save(any(CheckinDocument.class));
    }

    @Test
    void testSaveWithId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Found unexpected CheckinDocument id %s, please try updating instead", cd.getId()), exception.getMessage());

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinDocumentNullCheckinsId() {
        CheckinDocument cd = new CheckinDocument(null, "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinDocumentNullUploadDocId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), null);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
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

        when(checkinRepository.findById(cd.getCheckinsId())).thenReturn(Optional.empty());

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("CheckIn %s doesn't exist", cd.getCheckinsId()), exception.getMessage());

        verify(checkinDocumentRepository, never()).save(any(CheckinDocument.class));
        verify(checkinRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testSaveCheckinDocumentExistingUploadDocId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), "docId");

        CheckIn checkin = new CheckIn();

        when(checkinRepository.findById(cd.getCheckinsId())).thenReturn(Optional.of(checkin));
        when(checkinDocumentRepository.findByUploadDocId(cd.getUploadDocId())).thenReturn(Optional.of(cd));

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
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

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("CheckinDocument id %s not found, please try inserting instead", cd.getId()), exception.getMessage());

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckinDocumentNullCheckinsId() {
        CheckinDocument cd = new CheckinDocument(null, "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckinDocumentNullUploadDocId() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), null);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckinDocumentDoesNotExist() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "docId");
        when(checkinDocumentRepository.findById(cd.getCheckinsId())).thenReturn(Optional.empty());

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("CheckinDocument id %s not found, please try inserting instead", cd.getId()), exception.getMessage());

        verify(checkinRepository, never()).findById(any(UUID.class));
        verify(checkinDocumentRepository, never()).update(any(CheckinDocument.class));
        verify(checkinDocumentRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateCheckInDoesNotExist() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "docId");
        when(checkinDocumentRepository.findById(cd.getId())).thenReturn(Optional.of(cd));
        when(checkinRepository.findById(cd.getCheckinsId())).thenReturn(Optional.empty());

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
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
    void testDeleteByCheckinId() {
        when(checkinDocumentRepository.existsByCheckinsId(any(UUID.class))).thenReturn(true);
        when(currentUserServices.isAdmin()).thenReturn(true);

        services.deleteByCheckinId(UUID.randomUUID());

        verify(checkinDocumentRepository, times(1)).deleteByCheckinsId(any(UUID.class));
    }

    @Test
    void testDeleteNonExistingCheckinsId() {
        UUID uuid = UUID.randomUUID();

        when(checkinDocumentRepository.existsByCheckinsId(any(UUID.class))).thenReturn(false);
        when(currentUserServices.isAdmin()).thenReturn(true);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.deleteByCheckinId(uuid));
        assertEquals(String.format("CheckinDocument with CheckinsId %s does not exist", uuid), exception.getMessage());

        verify(checkinDocumentRepository, times(0)).deleteByCheckinsId(any(UUID.class));
    }

    @Test
    void testDeleteByUploadDocId() {

        when(checkinDocumentRepository.existsByUploadDocId(any(String.class))).thenReturn(true);
        when(currentUserServices.isAdmin()).thenReturn(true);

        services.deleteByUploadDocId("Test.Upload.Doc.Id");

        verify(checkinDocumentRepository, times(1)).deleteByUploadDocId(any(String.class));
        verify(checkinDocumentRepository, times(1)).existsByUploadDocId(any(String.class));
    }

    @Test
    void testDeleteNonExistingUploadDocId() {
        String id = "Test.Id";
        when(checkinDocumentRepository.existsByUploadDocId(any(String.class))).thenReturn(false);
        when(currentUserServices.isAdmin()).thenReturn(true);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.deleteByUploadDocId(id));

        assertEquals(String.format("CheckinDocument with uploadDocId %s does not exist", id), exception.getMessage());
        verify(checkinDocumentRepository, times(0)).deleteByUploadDocId(any(String.class));
        verify(checkinDocumentRepository, times(1)).existsByUploadDocId(any(String.class));
    }
}


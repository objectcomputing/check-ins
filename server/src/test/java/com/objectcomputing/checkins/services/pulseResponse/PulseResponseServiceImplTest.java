package com.objectcomputing.checkins.services.pulseresponse;

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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PulseResponseServiceImplTest {

    @Mock
    private MemberProfileRepository memberprofileRepository;

    @Mock
    private PulseResponseRepository pulseResponseRepository;

    @InjectMocks
    private PulseResponseServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(memberprofileRepository);
        Mockito.reset(pulseResponseRepository);

    }

    @Test
    void testRead() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01),UUID.randomUUID(),"exampleDocId" , "exampleDocId2");

        Set<PulseResponse> pulseResponseSet = Set.of(
                new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "doc1", "doc1"),
                new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "doc2", "doc2"),
                new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "doc3", "doc3")
        );

        when(pulseResponseRepository.findByTeamMemberId(cd.getTeamMemberId())).thenReturn(pulseResponseSet);

        assertEquals(pulseResponseSet, services.read(cd.getTeamMemberId()));

        verify(pulseResponseRepository, times(1)).findByTeamMemberId(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertTrue(services.read(null).isEmpty());

        verify(pulseResponseRepository, never()).findByTeamMemberId(any(UUID.class));
    }

    @Test
    void testSave() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "docId", "docId2");
        MemberProfile memberprofile = new MemberProfile();

        when(memberprofileRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.of(memberprofile));
        when(pulseResponseRepository.save(eq(cd))).thenReturn(cd);

        assertEquals(cd, services.save(cd));

        verify(memberprofileRepository, times(1)).findById(any(UUID.class));
        verify(pulseResponseRepository, times(1)).save(any(PulseResponse.class));
    }

    @Test
    void testSaveWithId() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "docId", "docId2");

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Found unexpected PulseResponse id %s, please try updating instead", cd.getId()), exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSavePulseResponseNullTeamMemberId() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), null, "docId", "docId2");

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Invalid PulseResponse %s", cd), exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSavePulseResponseNullUploadDocId() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), null, null);

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Invalid PulseResponse %s", cd), exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveNullPulseResponse() {
        assertNull(services.save(null));

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSavePulseResponseNonExistingMemberProfile() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "docId", "docId2");

        when(memberprofileRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.empty());

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("MemberProfile %s doesn't exist", cd.getTeamMemberId()), exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testSavePulseResponseExistingUploadDocId() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "docId", "docId2");

        MemberProfile memberprofile = new MemberProfile();

        when(memberprofileRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.of(memberprofile));
        when(pulseResponseRepository.findByInternalFeelings(eq(cd.getInternalFeelings()))).thenReturn(Optional.of(cd));

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.save(cd));
        assertEquals(String.format("PulseResponse with internalFeelings ID docId already exists", cd.getInternalFeelings()), exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdate() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "docId", "docId2");
        MemberProfile memberprofile = new MemberProfile();

        when(memberprofileRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.of(memberprofile));
        when(pulseResponseRepository.findById(cd.getId())).thenReturn(Optional.of(cd));
        when(pulseResponseRepository.update(eq(cd))).thenReturn(cd);

        assertEquals(cd, services.update(cd));

        verify(memberprofileRepository, times(1)).findById(any(UUID.class));
        verify(pulseResponseRepository, times(1)).findById(cd.getId());
        verify(pulseResponseRepository, times(1)).update(any(PulseResponse.class));
    }

    @Test
    void testUpdateWithoutId() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "docId", "docId2");

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("PulseResponse id %s not found, please try inserting instead", cd.getId()), exception.getMessage());

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdatePulseResponseNullTeamMemberId() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), null, "docId", "docId2");

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Invalid PulseResponse %s", cd), exception.getMessage());

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdatePulseResponseNullUploadDocId() {
        PulseResponse cd = new PulseResponse(LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), null, null);

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Invalid PulseResponse %s", cd), exception.getMessage());

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdatePulseResponseDoesNotExist() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "docId", "docId2");
        when(pulseResponseRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.empty());

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("PulseResponse id %s not found, please try inserting instead", cd.getId()), exception.getMessage());

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateMemberProfileDoesNotExist() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),LocalDate.of(2019, 1, 01),LocalDate.of(2019, 1, 01), UUID.randomUUID(), "docId", "docId2");
        when(pulseResponseRepository.findById(eq(cd.getId()))).thenReturn(Optional.of(cd));
        when(memberprofileRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.empty());

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.update(cd));
        assertEquals(String.format("MemberProfile %s doesn't exist", cd.getTeamMemberId()), exception.getMessage());

        verify(memberprofileRepository, times(1)).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateNullPulseResponse() {
        assertNull(services.update(null));

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testDelete() {

        when(pulseResponseRepository.existsByTeamMemberId(any(UUID.class))).thenReturn(true);

        services.delete(UUID.randomUUID());

        verify(pulseResponseRepository, times(1)).deleteByTeamMemberId(any(UUID.class));
    }

    @Test
    void testDeleteNonExistingTeamMemberId() {
        UUID uuid = UUID.randomUUID();

        when(pulseResponseRepository.existsByTeamMemberId(any(UUID.class))).thenReturn(false);

        PulseResponseBadArgException exception = assertThrows(PulseResponseBadArgException.class, () -> services.delete(uuid));
        assertEquals(String.format("PulseResponse with TeamMemberId %s does not exist", uuid), exception.getMessage());

        verify(pulseResponseRepository, times(0)).deleteByTeamMemberId(any(UUID.class));
    }
}

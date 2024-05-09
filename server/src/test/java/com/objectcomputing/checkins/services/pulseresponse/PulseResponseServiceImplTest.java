package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

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
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(memberprofileRepository);
        Mockito.reset(pulseResponseRepository);

    }

    @Test
    void testRead() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),1, 2, LocalDate.of(2019, 1, 1),UUID.randomUUID(),"examplePRId" , "examplePRId2");

        when(pulseResponseRepository.findById(cd.getId())).thenReturn(Optional.of(cd));

        assertEquals(cd, services.read(cd.getId()));

        verify(pulseResponseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));
        verify(pulseResponseRepository, never()).findByTeamMemberId(any(UUID.class));
    }

    @Test
    void testSave() {
        PulseResponse cd = new PulseResponse(1, 2, LocalDate.of(2019, 1, 1),UUID.randomUUID(), "PRId", "PRId2");
        MemberProfile memberprofile = new MemberProfile();

        when(memberprofileRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.of(memberprofile));
        when(pulseResponseRepository.save(eq(cd))).thenReturn(cd);

        assertEquals(cd, services.save(cd));

        verify(memberprofileRepository, times(1)).findById(any(UUID.class));
        verify(pulseResponseRepository, times(1)).save(any(PulseResponse.class));
    }

    @Test
    void testSaveWithId() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),1, 2, LocalDate.of(2019, 1, 1), UUID.randomUUID(), "PRId", "PRId2");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Found unexpected id for pulseresponse %s", cd.getId()), exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSavePulseResponseNullTeamMemberId() {
        PulseResponse cd = new PulseResponse(1, 2, LocalDate.of(2019, 1, 1),null, "PRId", "PRId2");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals("Member null doesn't exists", exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSavePulseResponseNullPRId() {
        PulseResponse cd = new PulseResponse(1, 2, LocalDate.of(2019, 1, 1), UUID.randomUUID(), null, null);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Member %s doesn't exists", cd.getTeamMemberId()), exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
    }

    @Test
    void testSaveNullPulseResponse() {
        assertNull(services.save(null));

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSavePulseResponseNonExistingMemberProfile() {
        PulseResponse cd = new PulseResponse(1, 2, LocalDate.of(2019, 1, 1),UUID.randomUUID(), "PRId", "PRId2");

        when(memberprofileRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.empty());

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Member %s doesn't exists", cd.getTeamMemberId()), exception.getMessage());

        verify(pulseResponseRepository, never()).save(any(PulseResponse.class));
        verify(memberprofileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdate() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),1, 2, LocalDate.of(2019, 1, 1), UUID.randomUUID(), "PRId", "PRId2");
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
        PulseResponse cd = new PulseResponse(1, 2, LocalDate.of(2019, 1, 1),UUID.randomUUID(), "PRId", "PRId2");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Unable to find pulseresponse record with id %s", cd.getId()), exception.getMessage());

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdatePulseResponseNullTeamMemberId() {
        PulseResponse cd = new PulseResponse(1, 2, LocalDate.of(2019, 1, 1), null, "PRId", "PRId2");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals("Unable to find pulseresponse record with id null", exception.getMessage());

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdatePulseResponseNullPRId() {
        PulseResponse cd = new PulseResponse(1, 2, LocalDate.of(2019, 1, 1), UUID.randomUUID(), null, null);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals("Unable to find pulseresponse record with id null", exception.getMessage());

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdatePulseResponseDoesNotExist() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(), 1, 2, LocalDate.of(2019, 1, 1), UUID.randomUUID(), "PRId", "PRId2");
        when(pulseResponseRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.empty());

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Unable to find pulseresponse record with id %s", cd.getId()), exception.getMessage());

        verify(memberprofileRepository, never()).findById(any(UUID.class));
        verify(pulseResponseRepository, never()).update(any(PulseResponse.class));
        verify(pulseResponseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateMemberProfileDoesNotExist() {
        PulseResponse cd = new PulseResponse(UUID.randomUUID(),1, 2, LocalDate.of(2019, 1, 1), UUID.randomUUID(), "PRId", "PRId2");
        when(pulseResponseRepository.findById(eq(cd.getId()))).thenReturn(Optional.of(cd));
        when(memberprofileRepository.findById(eq(cd.getTeamMemberId()))).thenReturn(Optional.empty());

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Member %s doesn't exist", cd.getTeamMemberId()), exception.getMessage());

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
}

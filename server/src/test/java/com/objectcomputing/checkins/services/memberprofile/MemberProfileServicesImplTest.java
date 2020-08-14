package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;

import java.util.List;
import java.util.Set;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberProfileServicesImplTest {

    @Mock
    private MemberProfileRepository mockMemberProfileRepository;

    @InjectMocks
    private MemberProfileServicesImpl testObject;

    @BeforeAll
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAll() {
        MemberProfile profileOne = mkMemberProfile();
        MemberProfile profileTwo = mkMemberProfile("2");
        MemberProfile profileThree = mkMemberProfile("3");

        when(mockMemberProfileRepository.findAll())
                .thenReturn(List.of(profileOne, profileTwo, profileThree));
        Mockito.verifyNoMoreInteractions(mockMemberProfileRepository);

        Set<MemberProfile> actual = testObject.findByValues(null, null, null);

        assertEquals(3, actual.size());
        assertTrue(actual.contains(profileOne));
        assertTrue(actual.contains(profileTwo));
        assertTrue(actual.contains(profileThree));
    }

    @Test
    public void testFindSpecific() {
        MemberProfile profileOne = mkMemberProfile();
        profileOne.setPdlId(testPdlId);
        MemberProfile profileTwo = mkMemberProfile("2");
        profileTwo.setName(profileOne.getName());
        profileTwo.setRole(profileOne.getRole());
        MemberProfile profileThree = mkMemberProfile("3");
        profileThree.setName(profileOne.getName());
        profileThree.setPdlId(testPdlId);

        when(mockMemberProfileRepository.findAll())
                .thenReturn(List.of(profileOne, profileTwo, profileThree));
        when(mockMemberProfileRepository.findByName(profileOne.getName()))
                .thenReturn(List.of(profileOne, profileTwo, profileThree));
        when(mockMemberProfileRepository.findByRole(profileOne.getRole()))
                .thenReturn(List.of(profileOne, profileTwo));
        when(mockMemberProfileRepository.findByPdlId(profileOne.getPdlId()))
                .thenReturn(List.of(profileOne, profileThree));

        Set<MemberProfile> actual = testObject.findByValues(profileOne.getName(), profileOne.getRole(), profileOne.getPdlId());

        assertEquals(1, actual.size());
        assertTrue(actual.contains(profileOne));
    }

    @Test
    public void testFindByUUIDSuccess() {
        MemberProfile expected = mkMemberProfile();
        expected.setUuid(testUuid);

        when(mockMemberProfileRepository.findByUuid(expected.getUuid())).thenReturn(expected);

        MemberProfile actual = testObject.getById(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test
    public void testFindByUUIDNotFound() {
        MemberProfile expected = mkMemberProfile();
        expected.setUuid(testUuid);

        when(mockMemberProfileRepository.findByUuid(expected.getUuid())).thenReturn(null);
        verify(mockMemberProfileRepository, atLeastOnce()).findByUuid(testUuid);

        MemberProfileDoesNotExistException thrown = assertThrows(MemberProfileDoesNotExistException.class, () -> {
            testObject.getById(testUuid);
        });

        assertEquals("No member profile for id", thrown.getMessage());
    }

    @Test
    public void testSaveNew() {
        MemberProfile in = mkMemberProfile();
        MemberProfile expected = mkMemberProfile();
        expected.setUuid(testUuid);

        when(mockMemberProfileRepository.save(in)).thenReturn(expected);

        MemberProfile actual = testObject.saveProfile(in);

        assertEquals(expected, actual);
    }

    @Test
    public void testSaveUpdate() {
        MemberProfile expected = mkMemberProfile();
        expected.setUuid(testUuid);

        when(mockMemberProfileRepository.findByUuid(testUuid)).thenReturn(expected);
        when(mockMemberProfileRepository.update(expected)).thenReturn(expected);

        MemberProfile actual = testObject.saveProfile(expected);

        assertEquals(expected, actual);
    }

    @Test
    public void testSaveUpdateNoExistingRecord() {
        MemberProfile expected = mkMemberProfile();
        expected.setUuid(testUuid);

        when(mockMemberProfileRepository.findByUuid(testUuid)).thenReturn(null);

        MemberProfileBadArgException thrown = assertThrows(MemberProfileBadArgException.class, () -> {
            testObject.saveProfile(expected);
        });

        assertEquals("No profile exists for this ID", thrown.getMessage());
    }
}

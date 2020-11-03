package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.member_skill.MemberSkillAlreadyExistsException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberProfileEntityServicesImplTest {

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
        MemberProfileEntity profileOne = mkMemberProfile();
        MemberProfileEntity profileTwo = mkMemberProfile("2");
        MemberProfileEntity profileThree = mkMemberProfile("3");

        when(mockMemberProfileRepository.search(null, null, null, null))
                .thenReturn(List.of(profileOne, profileTwo, profileThree));

        Set<MemberProfileEntity> actual = testObject.findByValues(null, null, null, null);

        assertEquals(3, actual.size());
        assertTrue(actual.contains(profileOne));
        assertTrue(actual.contains(profileTwo));
        assertTrue(actual.contains(profileThree));
    }

    @Test
    public void testFindSpecific() {
        MemberProfileEntity profileOne = mkMemberProfile();
        profileOne.setPdlId(testPdlId);
        MemberProfileEntity profileTwo = mkMemberProfile("2");
        profileTwo.setName(profileOne.getName());
        profileTwo.setTitle(profileOne.getTitle());
        MemberProfileEntity profileThree = mkMemberProfile("3");
        profileThree.setName(profileOne.getName());
        profileThree.setPdlId(testPdlId);

        when(mockMemberProfileRepository.search(profileOne.getName(), profileOne.getTitle(), profileOne.getPdlId().toString(), profileOne.getWorkEmail()))
                .thenReturn(Collections.singletonList(profileOne));

        Set<MemberProfileEntity> actual = testObject.findByValues(profileOne.getName(), profileOne.getTitle(), profileOne.getPdlId(), profileOne.getWorkEmail());

        assertEquals(1, actual.size());
        assertTrue(actual.contains(profileOne));
    }

    @Test
    public void testFindByIdSuccess() {
        MemberProfileEntity expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.findById(expected.getId())).thenReturn(java.util.Optional.of(expected));

        MemberProfileEntity actual = testObject.getById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void testFindByIdNotFound() {
        MemberProfileEntity expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.findById(expected.getId())).thenReturn(Optional.empty());

        MemberProfileDoesNotExistException thrown = assertThrows(MemberProfileDoesNotExistException.class, () -> {
            testObject.getById(testUuid);
        });

        assertEquals("No member profile for id", thrown.getMessage());
        verify(mockMemberProfileRepository, atLeastOnce()).findById(testUuid);
    }

    @Test
    public void testSaveNew() {
        MemberProfileEntity in = mkMemberProfile();
        MemberProfileEntity expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.save(in)).thenReturn(expected);

        MemberProfileEntity actual = testObject.saveProfile(in);

        assertEquals(expected, actual);
    }

    @Test
    public void testSaveMemberSameEmail() {
        MemberProfileEntity alreadyExists = mkMemberProfile();
        alreadyExists.setId(UUID.randomUUID());

        when(mockMemberProfileRepository.findByWorkEmail(eq(alreadyExists.getWorkEmail()))).thenReturn(java.util.Optional.of(alreadyExists));

        MemberProfileEntity in = mkMemberProfile("3");
        in.setWorkEmail(alreadyExists.getWorkEmail());

        MemberSkillAlreadyExistsException response = assertThrows(MemberSkillAlreadyExistsException.class, () -> testObject.saveProfile(in));

        assertEquals(String.format("Email %s already exists in database", in.getWorkEmail()), response.getMessage());
    }

    @Test
    public void testSaveUpdate() {
        MemberProfileEntity expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.findById(testUuid)).thenReturn(java.util.Optional.of(expected));
        when(mockMemberProfileRepository.update(expected)).thenReturn(expected);

        MemberProfileEntity actual = testObject.saveProfile(expected);

        assertEquals(expected, actual);
    }

    @Test
    public void testSaveUpdateNoExistingRecord() {
        MemberProfileEntity expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.findById(testUuid)).thenReturn(null);

        MemberProfileBadArgException thrown = assertThrows(MemberProfileBadArgException.class, () -> {
            testObject.saveProfile(expected);
        });

        assertEquals("No member profile exists for the ID", thrown.getMessage());
    }
}

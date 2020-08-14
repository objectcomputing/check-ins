package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamBadArgException;
import com.objectcomputing.checkins.services.team.TeamRepository;
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
class TeamMemberServicesImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private TeamMemberServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(teamRepository, teamMemberRepository, memberProfileRepository);
    }

    @Test
    void testRead() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);

        when(teamMemberRepository.findById(teamMember.getId())).thenReturn(Optional.of(teamMember));

        assertEquals(teamMember, services.read(teamMember.getId()));

        verify(teamMemberRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));

        verify(teamMemberRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false);
        Team team = new Team(teamMember.getTeamid(), "Wayne's", "World");

        when(teamRepository.findById(eq(teamMember.getTeamid()))).thenReturn(Optional.of(team));
        when(memberProfileRepository.findById(eq(teamMember.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(teamMemberRepository
                .findByTeamidAndMemberid(eq(teamMember.getTeamid()), eq(teamMember.getMemberid())))
                .thenReturn(Optional.empty());
        when(teamMemberRepository.save(eq(teamMember))).thenReturn(teamMember);

        assertEquals(teamMember, services.save(teamMember));

        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(teamMemberRepository, times(1))
                .findByTeamidAndMemberid(any(UUID.class), any(UUID.class));
        verify(teamMemberRepository, times(1)).save(any(TeamMember.class));
    }

    @Test
    void testSaveWithId() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(teamMember));
        assertEquals(String.format("Found unexpected id %s for team member", teamMember.getId()), exception.getMessage());

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never())
                .findByTeamidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveTeamMemberNullTeamId() {
        TeamMember teamMember = new TeamMember(null, UUID.randomUUID(), true);

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(teamMember));
        assertEquals(String.format("Invalid teamMember %s", teamMember), exception.getMessage());

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never())
                .findByTeamidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveTeamMemberNullMemberId() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), null, true);

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(teamMember));
        assertEquals(String.format("Invalid teamMember %s", teamMember), exception.getMessage());

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never())
                .findByTeamidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveNullTeamMember() {
        assertNull(services.save(null));

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never())
                .findByTeamidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveTeamMemberNonExistingTeam() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamRepository.findById(eq(teamMember.getTeamid()))).thenReturn(Optional.empty());

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(teamMember));
        assertEquals(String.format("Team %s doesn't exist", teamMember.getTeamid()), exception.getMessage());

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never())
                .findByTeamidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveTeamMemberNonExistingMember() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamRepository.findById(eq(teamMember.getTeamid()))).thenReturn(Optional.of(new Team("n", "d")));
        when(memberProfileRepository.findById(eq(teamMember.getMemberid()))).thenReturn(Optional.empty());

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(teamMember));
        assertEquals(String.format("Member %s doesn't exist", teamMember.getMemberid()), exception.getMessage());

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(teamMemberRepository, never())
                .findByTeamidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveTeamMemberAlreadyExistingMember() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), true);

        when(teamRepository.findById(eq(teamMember.getTeamid()))).thenReturn(Optional.of(new Team("n", "d")));
        when(memberProfileRepository.findById(eq(teamMember.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(teamMemberRepository.findByTeamidAndMemberid(eq(teamMember.getTeamid()), eq(teamMember.getMemberid())))
                .thenReturn(Optional.of(teamMember));

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(teamMember));
        assertEquals(String.format("Member %s already exists in team %s",
                teamMember.getMemberid(), teamMember.getTeamid()), exception.getMessage());

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(teamMemberRepository, times(1))
                .findByTeamidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testUpdate() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);
        Team team = new Team(teamMember.getTeamid(), "Wayne's", "World");

        when(teamRepository.findById(eq(teamMember.getTeamid()))).thenReturn(Optional.of(team));
        when(memberProfileRepository.findById(eq(teamMember.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(teamMemberRepository.findById(teamMember.getId())).thenReturn(Optional.of(teamMember));
        when(teamMemberRepository.update(eq(teamMember))).thenReturn(teamMember);

        assertEquals(teamMember, services.update(teamMember));

        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(teamMemberRepository, times(1)).findById(any(UUID.class));
        verify(teamMemberRepository, times(1)).update(any(TeamMember.class));
    }

    @Test
    void testUpdateWithoutId() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false);

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(teamMember));
        assertEquals(String.format("Unable to locate teamMember to update with id %s", teamMember.getId()), exception.getMessage());

        verify(teamMemberRepository, never()).update(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateTeamMemberNullTeamId() {
        TeamMember teamMember = new TeamMember(null, UUID.randomUUID(), true);

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(teamMember));
        assertEquals(String.format("Invalid teamMember %s", teamMember), exception.getMessage());

        verify(teamMemberRepository, never()).update(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateTeamMemberNullMemberId() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), null, true);

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(teamMember));
        assertEquals(String.format("Invalid teamMember %s", teamMember), exception.getMessage());

        verify(teamMemberRepository, never()).update(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never()).findById(any(UUID.class));
    }


    @Test
    void testUpdateTeamMemberDoesNotExist() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);
        when(teamMemberRepository.findById(eq(teamMember.getTeamid()))).thenReturn(Optional.empty());

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(teamMember));
        assertEquals(String.format("Unable to locate teamMember to update with id %s", teamMember.getId()), exception.getMessage());

        verify(teamMemberRepository, never()).update(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateTeamDoesNotExist() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);
        when(teamMemberRepository.findById(eq(teamMember.getId()))).thenReturn(Optional.of(teamMember));
        when(teamRepository.findById(eq(teamMember.getTeamid()))).thenReturn(Optional.empty());

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(teamMember));
        assertEquals(String.format("Team %s doesn't exist", teamMember.getTeamid()), exception.getMessage());

        verify(teamMemberRepository, never()).update(any(TeamMember.class));
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateMemberDoesNotExist() {
        TeamMember teamMember = new TeamMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);
        when(teamMemberRepository.findById(eq(teamMember.getId()))).thenReturn(Optional.of(teamMember));
        when(teamRepository.findById(eq(teamMember.getTeamid()))).thenReturn(Optional.of(new Team("n", "d")));
        when(memberProfileRepository.findById(eq(teamMember.getMemberid()))).thenReturn(Optional.empty());

        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(teamMember));
        assertEquals(String.format("Member %s doesn't exist", teamMember.getMemberid()), exception.getMessage());

        verify(teamMemberRepository, never()).update(any(TeamMember.class));
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(teamMemberRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateNullTeamMember() {
        assertNull(services.update(null));

        verify(teamMemberRepository, never()).update(any(TeamMember.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testFindByFieldsNullParams() {
        Set<TeamMember> teamMemberSet = Set.of(
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        when(teamMemberRepository.findAll()).thenReturn(teamMemberSet);

        assertEquals(teamMemberSet, services.findByFields(null, null, null));

        verify(teamMemberRepository, times(1)).findAll();
        verify(teamMemberRepository, never()).findByTeamid(any(UUID.class));
        verify(teamMemberRepository, never()).findByMemberid(any(UUID.class));
        verify(teamMemberRepository, never()).findByLead(any(Boolean.class));
    }

    @Test
    void testFindByFieldsTeamId() {
        List<TeamMember> teamMembers = List.of(
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        List<TeamMember> teamMembersToFind = List.of(teamMembers.get(1));
        TeamMember teamMember = teamMembersToFind.get(0);

        when(teamMemberRepository.findAll()).thenReturn(teamMembers);
        when(teamMemberRepository.findByTeamid(teamMember.getTeamid())).thenReturn(teamMembersToFind);

        assertEquals(new HashSet<>(teamMembersToFind), services.findByFields(teamMember.getTeamid(), null, null));

        verify(teamMemberRepository, times(1)).findAll();
        verify(teamMemberRepository, times(1)).findByTeamid(any(UUID.class));
        verify(teamMemberRepository, never()).findByMemberid(any(UUID.class));
        verify(teamMemberRepository, never()).findByLead(any(Boolean.class));
    }

    @Test
    void testFindByFieldsMemberId() {
        List<TeamMember> teamMembers = List.of(
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        List<TeamMember> teamMembersToFind = List.of(teamMembers.get(1));
        TeamMember teamMember = teamMembersToFind.get(0);

        when(teamMemberRepository.findAll()).thenReturn(teamMembers);
        when(teamMemberRepository.findByMemberid(teamMember.getMemberid())).thenReturn(teamMembersToFind);

        assertEquals(new HashSet<>(teamMembersToFind), services.findByFields(null, teamMember.getMemberid(), null));

        verify(teamMemberRepository, times(1)).findAll();
        verify(teamMemberRepository, times(1)).findByMemberid(any(UUID.class));
        verify(teamMemberRepository, never()).findByTeamid(any(UUID.class));
        verify(teamMemberRepository, never()).findByLead(any(Boolean.class));
    }

    @Test
    void testFindByFieldsLead() {
        List<TeamMember> teamMembers = List.of(
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        List<TeamMember> teamMembersToFind = List.of(teamMembers.get(1));

        TeamMember teamMember = teamMembersToFind.get(0);
        when(teamMemberRepository.findAll()).thenReturn(teamMembers);
        when(teamMemberRepository.findByLead(teamMember.isLead())).thenReturn(teamMembersToFind);

        assertEquals(new HashSet<>(teamMembersToFind), services.findByFields(null, null, teamMember.isLead()));

        verify(teamMemberRepository, times(1)).findAll();
        verify(teamMemberRepository, never()).findByMemberid(any(UUID.class));
        verify(teamMemberRepository, never()).findByTeamid(any(UUID.class));
        verify(teamMemberRepository, times(1)).findByLead(any(Boolean.class));
    }

    @Test
    void testFindByFieldsAll() {
        List<TeamMember> teamMembers = List.of(
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new TeamMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        List<TeamMember> teamMembersToFind = List.of(teamMembers.get(1));

        TeamMember teamMember = teamMembersToFind.get(0);
        when(teamMemberRepository.findAll()).thenReturn(teamMembers);
        when(teamMemberRepository.findByMemberid(teamMember.getMemberid())).thenReturn(teamMembersToFind);
        when(teamMemberRepository.findByLead(teamMember.isLead())).thenReturn(teamMembersToFind);
        when(teamMemberRepository.findByTeamid(teamMember.getTeamid())).thenReturn(teamMembersToFind);

        assertEquals(new HashSet<>(teamMembersToFind), services
                .findByFields(teamMember.getTeamid(), teamMember.getMemberid(), teamMember.isLead()));

        verify(teamMemberRepository, times(1)).findAll();
        verify(teamMemberRepository, times(1)).findByMemberid(any(UUID.class));
        verify(teamMemberRepository, times(1)).findByTeamid(any(UUID.class));
        verify(teamMemberRepository, times(1)).findByLead(any(Boolean.class));
    }
}

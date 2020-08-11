package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;
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
class TeamServicesImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private TeamServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(teamRepository, teamMemberRepository);
    }

    @Test
    void testRead() {
        Team team = new Team(UUID.randomUUID(), "Hello", "World");
        when(teamRepository.findById(team.getTeamid())).thenReturn(Optional.of(team));
        assertEquals(team, services.read(team.getTeamid()));
        verify(teamRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));
        verify(teamRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        Team team = new Team("It's the end of the", "World");
        when(teamRepository.findByName(eq(team.getName()))).thenReturn(Optional.empty());
        when(teamRepository.save(eq(team))).thenReturn(team);
        assertEquals(team, services.save(team));
        verify(teamRepository, times(1)).findByName(any(String.class));
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void testSaveWithId() {
        Team team = new Team(UUID.randomUUID(), "Wayne's", "World");
        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(team));
        assertTrue(exception.getMessage().contains(String.format("unexpected teamid %s", team.getTeamid())));
        verify(teamRepository, never()).findByName(any(String.class));
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testSaveTeamNameExists() {
        Team team = new Team("Disney", "World");
        when(teamRepository.findByName(eq(team.getName()))).thenReturn(Optional.of(team));
        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(team));
        assertTrue(exception.getMessage().contains(String.format("name %s already exists", team.getName())));
        verify(teamRepository, times(1)).findByName(any(String.class));
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testSaveNullTeam() {
        assertNull(services.save(null));
        verify(teamRepository, never()).findByName(any(String.class));
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testUpdate() {
        Team team = new Team(UUID.randomUUID(), "Dog eat dog", "World");
        when(teamRepository.findById(eq(team.getTeamid()))).thenReturn(Optional.of(team));
        when(teamRepository.update(eq(team))).thenReturn(team);
        assertEquals(team, services.update(team));
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(teamRepository, times(1)).update(any(Team.class));
    }

    @Test
    void testUpdateWithoutId() {
        Team team = new Team("Bobby's", "World");
        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(team));
        assertTrue(exception.getMessage().contains(String.format("%s does not exist", team.getTeamid())));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(teamRepository, never()).update(any(Team.class));
    }

    @Test
    void testUpdateTeamDoesNotExist() {
        Team team = new Team(UUID.randomUUID(), "Wayne's", "World 2");
        when(teamRepository.findById(eq(team.getTeamid()))).thenReturn(Optional.empty());
        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(team));
        assertTrue(exception.getMessage().contains(String.format("%s does not exist", team.getTeamid())));
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(teamRepository, never()).update(any(Team.class));
    }

    @Test
    void testUpdateNullTeam() {
        assertNull(services.update(null));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(teamRepository, never()).update(any(Team.class));
    }

    @Test
    void testFindByFieldsNullParams() {
        Set<Team> teamSet = Set.of(
                new Team(UUID.randomUUID(), "World", "Health Organization"),
                new Team(UUID.randomUUID(), "World", "Wide Web")
        );

        when(teamRepository.findAll()).thenReturn(teamSet);
        assertEquals(teamSet, services.findByFields(null, null));
        verify(teamRepository, times(1)).findAll();
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(teamRepository, never()).findByNameIlike(any(String.class));
        verify(teamMemberRepository, never()).findByMemberid(any(UUID.class));
    }

    @Test
    void testFindByFieldName() {
        List<Team> team = List.of(
                new Team(UUID.randomUUID(), "What a Wonderful", "World"),
                new Team(UUID.randomUUID(), "World", "History")
        );

        List<Team> teamToFind = List.of(team.get(1));
        final String nameSearch = "World";
        when(teamRepository.findAll()).thenReturn(team);
        when(teamRepository.findByNameIlike(eq(nameSearch))).thenReturn(teamToFind);
        assertEquals(new HashSet<>(teamToFind), services.findByFields(nameSearch, null));
        verify(teamRepository, times(1)).findAll();
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(teamRepository, times(1)).findByNameIlike(any(String.class));
        verify(teamMemberRepository, never()).findByMemberid(any(UUID.class));
    }

    @Test
    void testFindByFieldMemberid() {
        List<Team> team = List.of(
                new Team(UUID.randomUUID(), "A Brave New", "World"),
                new Team(UUID.randomUUID(), "World", "of Warcraft")
        );

        final UUID memberId = UUID.randomUUID();
        List<Team> teamToFind = List.of(team.get(0));
        when(teamRepository.findAll()).thenReturn(team);
        when(teamRepository.findById(eq(teamToFind.get(0).getTeamid()))).thenReturn(Optional.of(teamToFind.get(0)));
        when(teamMemberRepository.findByMemberid(eq(memberId))).thenReturn(Collections.singletonList(
                new TeamMember(UUID.randomUUID(), teamToFind.get(0).getTeamid(), memberId, true)));
        assertEquals(new HashSet<>(teamToFind), services.findByFields(null, memberId));
        verify(teamRepository, times(1)).findAll();
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(teamRepository, never()).findByNameIlike(any(String.class));
        verify(teamMemberRepository, times(1)).findByMemberid(any(UUID.class));
    }

    @Test
    void testFindByFieldNameAndMemberid() {
        List<Team> team = List.of(
                new Team(UUID.randomUUID(), "World", "Series"),
                new Team(UUID.randomUUID(), "Super Mario", "World")
        );

        final UUID memberId = UUID.randomUUID();
        final String nameSearch = "World";
        List<Team> teamToFind = List.of(team.get(0));
        when(teamRepository.findAll()).thenReturn(team);
        when(teamRepository.findByNameIlike(eq(nameSearch))).thenReturn(teamToFind);
        when(teamRepository.findById(eq(teamToFind.get(0).getTeamid()))).thenReturn(Optional.of(teamToFind.get(0)));
        when(teamMemberRepository.findByMemberid(eq(memberId))).thenReturn(Collections.singletonList(
                new TeamMember(UUID.randomUUID(), teamToFind.get(0).getTeamid(), memberId, true)));
        assertEquals(new HashSet<>(teamToFind), services.findByFields(nameSearch, memberId));
        verify(teamRepository, times(1)).findAll();
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(teamRepository, times(1)).findByNameIlike(any(String.class));
        verify(teamMemberRepository, times(1)).findByMemberid(any(UUID.class));
    }
}

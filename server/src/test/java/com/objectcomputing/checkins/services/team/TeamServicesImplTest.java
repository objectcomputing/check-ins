package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;
import io.micronaut.test.annotation.MicronautTest;
import nu.studer.sample.tables.pojos.Team;
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
        Team teamEntity = new Team(UUID.randomUUID().toString(), "Hello", "World");
        when(teamRepository.findById(UUID.fromString(teamEntity.getId()))).thenReturn(Optional.of(teamEntity));
        assertEquals(teamEntity, services.read(UUID.fromString(teamEntity.getId())));
        verify(teamRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));
        verify(teamRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        Team teamEntity = new Team(null, "It's the end of the", "World");
        when(teamRepository.findByName(eq(teamEntity.getName()))).thenReturn(Optional.empty());
        when(teamRepository.save(eq(teamEntity))).thenReturn(teamEntity);
        assertEquals(teamEntity, services.save(new TeamCreateDTO(teamEntity.getName(), teamEntity.getDescription())));
        verify(teamRepository, times(1)).findByName(any(String.class));
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void testSaveWithId() {
        Team teamEntity = new Team(UUID.randomUUID().toString(), "Wayne's", "World");
        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(new TeamCreateDTO(teamEntity.getName(), teamEntity.getDescription())));
        assertTrue(exception.getMessage().contains(String.format("unexpected id %s", teamEntity.getId())));
        verify(teamRepository, never()).findByName(any(String.class));
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testSaveTeamNameExists() {
        Team teamEntity = new Team(null, "Disney", "World");
        when(teamRepository.findByName(eq(teamEntity.getName()))).thenReturn(Optional.of(teamEntity));
        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.save(new TeamCreateDTO(teamEntity.getName(), teamEntity.getDescription())));
        assertTrue(exception.getMessage().contains(String.format("name %s already exists", teamEntity.getName())));
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
        Team teamEntity = new Team(UUID.randomUUID().toString(), "Dog eat dog", "World");
        when(teamRepository.findById(eq(UUID.fromString(teamEntity.getId())))).thenReturn(Optional.of(teamEntity));
        when(teamRepository.update(eq(teamEntity))).thenReturn(teamEntity);
        assertEquals(teamEntity, services.update(new TeamUpdateDTO(teamEntity.getId(), teamEntity.getName(), teamEntity.getDescription())));
        verify(teamRepository, times(1)).findById(any(UUID.class));
        verify(teamRepository, times(1)).update(any(Team.class));
    }

    @Test
    void testUpdateWithoutId() {
        Team teamEntity = new Team(null, "Bobby's", "World");
        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(
                new TeamUpdateDTO(teamEntity.getId(), teamEntity.getName(), teamEntity.getDescription())));
        assertTrue(exception.getMessage().contains(String.format("%s does not exist", teamEntity.getId())));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(teamRepository, never()).update(any(Team.class));
    }

    @Test
    void testUpdateTeamDoesNotExist() {
        Team teamEntity = new Team(UUID.randomUUID().toString(), "Wayne's", "World 2");
        when(teamRepository.findById(eq(UUID.fromString(teamEntity.getId())))).thenReturn(Optional.empty());
        TeamBadArgException exception = assertThrows(TeamBadArgException.class, () -> services.update(
                new TeamUpdateDTO(teamEntity.getId(), teamEntity.getName(), teamEntity.getDescription())));
        assertTrue(exception.getMessage().contains(String.format("%s does not exist", teamEntity.getId())));
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
        List<Team> teamEntitySet = List.of(
                new Team(UUID.randomUUID().toString(), "World", "Health Organization"),
                new Team(UUID.randomUUID().toString(), "World", "Wide Web")
        );

        when(teamRepository.search(null, null)).thenReturn(teamEntitySet);
        assertEquals(teamEntitySet, services.findByFields(null, null));
    }

    @Test
    void testFindByFieldName() {
        List<Team> teamEntity = List.of(
                new Team(UUID.randomUUID().toString(), "What a Wonderful", "World"),
                new Team(UUID.randomUUID().toString(), "World", "History")
        );

        List<Team> teamEntityToFind = List.of(teamEntity.get(1));
        final String nameSearch = "World";
        when(teamRepository.search(eq(nameSearch), isNull())).thenReturn(teamEntityToFind);
        assertEquals(new HashSet<>(teamEntityToFind), services.findByFields(nameSearch, null));
    }

    @Test
    void testFindByFieldMemberid() {
        List<Team> teamEntity = List.of(
                new Team(UUID.randomUUID().toString(), "A Brave New", "World"),
                new Team(UUID.randomUUID().toString(), "World", "of Warcraft")
        );

        final UUID memberId = UUID.randomUUID();
        List<Team> teamEntityToFind = List.of(teamEntity.get(0));
        when(teamRepository.search(isNull(), eq(memberId))).thenReturn(teamEntityToFind);
        assertEquals(new HashSet<>(teamEntityToFind), services.findByFields(null, memberId));
    }

    @Test
    void testFindByFieldNameAndMemberid() {
        List<Team> teamEntity = List.of(
                new Team(UUID.randomUUID().toString(), "World", "Series"),
                new Team(UUID.randomUUID().toString(), "Super Mario", "World")
        );

        final UUID memberId = UUID.randomUUID();
        final String nameSearch = "World";
        List<Team> teamEntityToFind = List.of(teamEntity.get(0));
        when(teamRepository.search(eq(nameSearch), eq(memberId))).thenReturn(teamEntityToFind);
        assertEquals(new HashSet<>(teamEntityToFind), services.findByFields(nameSearch, memberId));
    }
}

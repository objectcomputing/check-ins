package com.objectcomputing.checkins.services.guild.member;

import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildBadArgException;
import com.objectcomputing.checkins.services.guild.GuildRepository;
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
class GuildMemberServicesImplTest {

    @Mock
    private GuildRepository guildRepository;

    @Mock
    private GuildMemberRepository guildMemberRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private GuildMemberServicesImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(guildRepository, guildMemberRepository, memberProfileRepository);
    }

    @Test
    void testRead() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);

        when(guildMemberRepository.findById(guildMember.getId())).thenReturn(Optional.of(guildMember));

        assertEquals(guildMember, services.read(guildMember.getId()));

        verify(guildMemberRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testReadNullId() {
        assertNull(services.read(null));

        verify(guildMemberRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSave() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false);
        Guild guild = new Guild(guildMember.getGuildid(), "Wayne's", "World");

        when(guildRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.of(guild));
        when(memberProfileRepository.findById(eq(guildMember.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(guildMemberRepository
                .findByGuildidAndMemberid(eq(guildMember.getGuildid()), eq(guildMember.getMemberid())))
                .thenReturn(Optional.empty());
        when(guildMemberRepository.save(eq(guildMember))).thenReturn(guildMember);

        assertEquals(guildMember, services.save(guildMember));

        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(guildMemberRepository, times(1))
                .findByGuildidAndMemberid(any(UUID.class), any(UUID.class));
        verify(guildMemberRepository, times(1)).save(any(GuildMember.class));
    }

    @Test
    void testSaveWithId() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Found unexpected id %s for guild member", guildMember.getId()), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never())
                .findByGuildidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveGuildMemberNullGuildId() {
        GuildMember guildMember = new GuildMember(null, UUID.randomUUID(), true);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Invalid guildMember %s", guildMember), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never())
                .findByGuildidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveGuildMemberNullMemberId() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), null, true);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Invalid guildMember %s", guildMember), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never())
                .findByGuildidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveNullGuildMember() {
        assertNull(services.save(null));

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never())
                .findByGuildidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveGuildMemberNonExistingGuild() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), true);

        when(guildRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.empty());

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Guild %s doesn't exist", guildMember.getGuildid()), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never())
                .findByGuildidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveGuildMemberNonExistingMember() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), true);

        when(guildRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.of(new Guild("n", "d")));
        when(memberProfileRepository.findById(eq(guildMember.getMemberid()))).thenReturn(Optional.empty());

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Member %s doesn't exist", guildMember.getMemberid()), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(guildMemberRepository, never())
                .findByGuildidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testSaveGuildMemberAlreadyExistingMember() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), true);

        when(guildRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.of(new Guild("n", "d")));
        when(memberProfileRepository.findById(eq(guildMember.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(guildMemberRepository.findByGuildidAndMemberid(eq(guildMember.getGuildid()), eq(guildMember.getMemberid())))
                .thenReturn(Optional.of(guildMember));

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Member %s already exists in guild %s",
                guildMember.getMemberid(), guildMember.getGuildid()), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(guildMemberRepository, times(1))
                .findByGuildidAndMemberid(any(UUID.class), any(UUID.class));
    }

    @Test
    void testUpdate() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);
        Guild guild = new Guild(guildMember.getGuildid(), "Wayne's", "World");

        when(guildRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.of(guild));
        when(memberProfileRepository.findById(eq(guildMember.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));
        when(guildMemberRepository.findById(guildMember.getId())).thenReturn(Optional.of(guildMember));
        when(guildMemberRepository.update(eq(guildMember))).thenReturn(guildMember);

        assertEquals(guildMember, services.update(guildMember));

        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(guildMemberRepository, times(1)).findById(any(UUID.class));
        verify(guildMemberRepository, times(1)).update(any(GuildMember.class));
    }

    @Test
    void testUpdateWithoutId() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.update(guildMember));
        assertEquals(String.format("Unable to locate guildMember to update with id %s", guildMember.getId()), exception.getMessage());

        verify(guildMemberRepository, never()).update(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateGuildMemberNullGuildId() {
        GuildMember guildMember = new GuildMember(null, UUID.randomUUID(), true);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.update(guildMember));
        assertEquals(String.format("Invalid guildMember %s", guildMember), exception.getMessage());

        verify(guildMemberRepository, never()).update(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testUpdateGuildMemberNullMemberId() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), null, true);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.update(guildMember));
        assertEquals(String.format("Invalid guildMember %s", guildMember), exception.getMessage());

        verify(guildMemberRepository, never()).update(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never()).findById(any(UUID.class));
    }


    @Test
    void testUpdateGuildMemberDoesNotExist() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);
        when(guildMemberRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.empty());

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.update(guildMember));
        assertEquals(String.format("Unable to locate guildMember to update with id %s", guildMember.getId()), exception.getMessage());

        verify(guildMemberRepository, never()).update(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateGuildDoesNotExist() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);
        when(guildMemberRepository.findById(eq(guildMember.getId()))).thenReturn(Optional.of(guildMember));
        when(guildRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.empty());

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.update(guildMember));
        assertEquals(String.format("Guild %s doesn't exist", guildMember.getGuildid()), exception.getMessage());

        verify(guildMemberRepository, never()).update(any(GuildMember.class));
        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateMemberDoesNotExist() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);
        when(guildMemberRepository.findById(eq(guildMember.getId()))).thenReturn(Optional.of(guildMember));
        when(guildRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.of(new Guild("n", "d")));
        when(memberProfileRepository.findById(eq(guildMember.getMemberid()))).thenReturn(Optional.empty());

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.update(guildMember));
        assertEquals(String.format("Member %s doesn't exist", guildMember.getMemberid()), exception.getMessage());

        verify(guildMemberRepository, never()).update(any(GuildMember.class));
        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
        verify(guildMemberRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testUpdateNullGuildMember() {
        assertNull(services.update(null));

        verify(guildMemberRepository, never()).update(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
        verify(guildMemberRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testFindByFieldsNullParams() {
        Set<GuildMember> guildMemberSet = Set.of(
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        when(guildMemberRepository.findAll()).thenReturn(guildMemberSet);

        assertEquals(guildMemberSet, services.findByFields(null, null, null));

        verify(guildMemberRepository, times(1)).findAll();
        verify(guildMemberRepository, never()).findByGuildid(any(UUID.class));
        verify(guildMemberRepository, never()).findByMemberid(any(UUID.class));
        verify(guildMemberRepository, never()).findByLead(any(Boolean.class));
    }

    @Test
    void testFindByFieldsGuildId() {
        List<GuildMember> guildMembers = List.of(
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        List<GuildMember> guildMembersToFind = List.of(guildMembers.get(1));
        GuildMember guildMember = guildMembersToFind.get(0);

        when(guildMemberRepository.findAll()).thenReturn(guildMembers);
        when(guildMemberRepository.findByGuildid(guildMember.getGuildid())).thenReturn(guildMembersToFind);

        assertEquals(new HashSet<>(guildMembersToFind), services.findByFields(guildMember.getGuildid(), null, null));

        verify(guildMemberRepository, times(1)).findAll();
        verify(guildMemberRepository, times(1)).findByGuildid(any(UUID.class));
        verify(guildMemberRepository, never()).findByMemberid(any(UUID.class));
        verify(guildMemberRepository, never()).findByLead(any(Boolean.class));
    }

    @Test
    void testFindByFieldsMemberId() {
        List<GuildMember> guildMembers = List.of(
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        List<GuildMember> guildMembersToFind = List.of(guildMembers.get(1));
        GuildMember guildMember = guildMembersToFind.get(0);

        when(guildMemberRepository.findAll()).thenReturn(guildMembers);
        when(guildMemberRepository.findByMemberid(guildMember.getMemberid())).thenReturn(guildMembersToFind);

        assertEquals(new HashSet<>(guildMembersToFind), services.findByFields(null, guildMember.getMemberid(), null));

        verify(guildMemberRepository, times(1)).findAll();
        verify(guildMemberRepository, times(1)).findByMemberid(any(UUID.class));
        verify(guildMemberRepository, never()).findByGuildid(any(UUID.class));
        verify(guildMemberRepository, never()).findByLead(any(Boolean.class));
    }

    @Test
    void testFindByFieldsLead() {
        List<GuildMember> guildMembers = List.of(
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        List<GuildMember> guildMembersToFind = List.of(guildMembers.get(1));

        GuildMember guildMember = guildMembersToFind.get(0);
        when(guildMemberRepository.findAll()).thenReturn(guildMembers);
        when(guildMemberRepository.findByLead(guildMember.isLead())).thenReturn(guildMembersToFind);

        assertEquals(new HashSet<>(guildMembersToFind), services.findByFields(null, null, guildMember.isLead()));

        verify(guildMemberRepository, times(1)).findAll();
        verify(guildMemberRepository, never()).findByMemberid(any(UUID.class));
        verify(guildMemberRepository, never()).findByGuildid(any(UUID.class));
        verify(guildMemberRepository, times(1)).findByLead(any(Boolean.class));
    }

    @Test
    void testFindByFieldsAll() {
        List<GuildMember> guildMembers = List.of(
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        List<GuildMember> guildMembersToFind = List.of(guildMembers.get(1));

        GuildMember guildMember = guildMembersToFind.get(0);
        when(guildMemberRepository.findAll()).thenReturn(guildMembers);
        when(guildMemberRepository.findByMemberid(guildMember.getMemberid())).thenReturn(guildMembersToFind);
        when(guildMemberRepository.findByLead(guildMember.isLead())).thenReturn(guildMembersToFind);
        when(guildMemberRepository.findByGuildid(guildMember.getGuildid())).thenReturn(guildMembersToFind);

        assertEquals(new HashSet<>(guildMembersToFind), services
                .findByFields(guildMember.getGuildid(), guildMember.getMemberid(), guildMember.isLead()));

        verify(guildMemberRepository, times(1)).findAll();
        verify(guildMemberRepository, times(1)).findByMemberid(any(UUID.class));
        verify(guildMemberRepository, times(1)).findByGuildid(any(UUID.class));
        verify(guildMemberRepository, times(1)).findByLead(any(Boolean.class));
    }
}

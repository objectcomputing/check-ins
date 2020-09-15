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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
        when(guildMemberRepository.save(eq(guildMember))).thenReturn(guildMember);

        assertEquals(guildMember, services.save(guildMember));

        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void testSaveWithId() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Found unexpected id %s for guild member", guildMember.getId()), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveGuildMemberNullGuildId() {
        GuildMember guildMember = new GuildMember(null, UUID.randomUUID(), true);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Invalid guildMember %s", guildMember), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveGuildMemberNullMemberId() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), null, true);

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Invalid guildMember %s", guildMember), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testSaveNullGuildMember() {
        assertNull(services.save(null));

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, never()).findById(any(UUID.class));
        verify(memberProfileRepository, never()).findById(any(UUID.class));
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
    }

    @Test
    void testSaveGuildMemberAlreadyExistingMember() {
        GuildMember guildMember = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), true);

        when(guildRepository.findById(eq(guildMember.getGuildid()))).thenReturn(Optional.of(new Guild("n", "d")));
        when(memberProfileRepository.findById(eq(guildMember.getMemberid()))).thenReturn(Optional.of(new MemberProfile()));

        when(guildMemberRepository.search(eq(guildMember.getGuildid().toString()), eq(guildMember.getMemberid().toString()),
                eq(guildMember.isLead())))
                .thenReturn(Set.of(guildMember));

        GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guildMember));
        assertEquals(String.format("Member %s already exists in guild %s",
                guildMember.getMemberid(), guildMember.getGuildid()), exception.getMessage());

        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        verify(guildRepository, times(1)).findById(any(UUID.class));
        verify(memberProfileRepository, times(1)).findById(any(UUID.class));
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

        when(guildMemberRepository.search(null, null, null)).thenReturn(guildMemberSet);

        assertEquals(guildMemberSet, services.findByFields(null, null, null));

    }

    @Test
    void testFindByFieldsGuildId() {

        GuildMember guildMemberToFind = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false);

        when(guildMemberRepository.search(guildMemberToFind.getGuildid().toString(), null, null)).thenReturn(Set.of(guildMemberToFind));

        assertEquals(Set.of(guildMemberToFind), services.findByFields(guildMemberToFind.getGuildid(), null, null));

    }

    @Test
    void testFindByFieldsMemberId() {
        GuildMember guildMemberToFind = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false);
        Set<GuildMember> guildMembers = Set.of(
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );

        when(guildMemberRepository.findAll()).thenReturn(guildMembers);
        when(guildMemberRepository.search(null, guildMemberToFind.getMemberid().toString(), null)).thenReturn(Set.of(guildMemberToFind));

        assertEquals(Set.of(guildMemberToFind), services.findByFields(null, guildMemberToFind.getMemberid(), null));

    }

    @Test
    void testFindByFieldsLead() {
        GuildMember guildMemberToFind = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false);
        List<GuildMember> guildMembers = List.of(
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false),
                new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false)
        );


        when(guildMemberRepository.findAll()).thenReturn(guildMembers);
        when(guildMemberRepository.search(null, null, guildMemberToFind.isLead())).thenReturn(Set.of(guildMemberToFind));

        assertEquals(Set.of(guildMemberToFind), services.findByFields(null, null, guildMemberToFind.isLead()));

    }

    @Test
    void testFindByFieldsAll() {

        GuildMember guildMemberToFind = new GuildMember(UUID.randomUUID(), UUID.randomUUID(), false);


        GuildMember guildMember = guildMemberToFind;

        when(guildMemberRepository.search(guildMember.getGuildid().toString(), guildMember.getMemberid().toString(), guildMember.isLead())).thenReturn(Set.of(guildMemberToFind));

        assertEquals(Set.of(guildMemberToFind), services
                .findByFields(guildMember.getGuildid(), guildMember.getMemberid(), guildMember.isLead()));

    }
}

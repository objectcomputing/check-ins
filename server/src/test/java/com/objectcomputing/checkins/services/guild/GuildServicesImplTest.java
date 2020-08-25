// package com.objectcomputing.checkins.services.guild;

// import com.objectcomputing.checkins.services.guild.member.GuildMember;
// import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
// import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
// import io.micronaut.test.annotation.MicronautTest;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;

// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @MicronautTest
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// class GuildServicesImplTest {

//     @Mock
//     private GuildRepository guildRepository;

//     @Mock
//     private GuildMemberRepository guildMemberRepository;

//     @Mock
//     private MemberProfileRepository memberProfileRepository;

//     @InjectMocks
//     private GuildServicesImpl services;

//     @BeforeAll
//     void initMocks() {
//         MockitoAnnotations.initMocks(this);
//     }

//     @BeforeEach
//     void resetMocks() {
//         Mockito.reset(guildRepository, guildMemberRepository);
//     }

//     @Test
//     void testRead() {
//         Guild guild = new Guild(UUID.randomUUID(), "Hello", "World");
//         when(guildRepository.findById(guild.getGuildid())).thenReturn(Optional.of(guild));
//         assertEquals(guild, services.read(guild.getGuildid()));
//         verify(guildRepository, times(1)).findById(any(UUID.class));
//     }

//     @Test
//     void testReadNullId() {
//         assertNull(services.read(null));
//         verify(guildRepository, never()).findById(any(UUID.class));
//     }

//     @Test
//     void testSave() {
//         Guild guild = new Guild("It's the end of the", "World");
//         when(guildRepository.findByName(eq(guild.getName()))).thenReturn(Optional.empty());
//         when(guildRepository.save(eq(guild))).thenReturn(guild);
//         assertEquals(guild, services.save(guild));
//         verify(guildRepository, times(1)).findByName(any(String.class));
//         verify(guildRepository, times(1)).save(any(Guild.class));
//     }

//     @Test
//     void testSaveWithId() {
//         Guild guild = new Guild(UUID.randomUUID(), "Wayne's", "World");
//         GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guild));
//         assertTrue(exception.getMessage().contains(String.format("unexpected guildid %s", guild.getGuildid())));
//         verify(guildRepository, never()).findByName(any(String.class));
//         verify(guildRepository, never()).save(any(Guild.class));
//     }

//     @Test
//     void testSaveGuildNameExists() {
//         Guild guild = new Guild("Disney", "World");
//         when(guildRepository.findByName(eq(guild.getName()))).thenReturn(Optional.of(guild));
//         GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.save(guild));
//         assertTrue(exception.getMessage().contains(String.format("name %s already exists", guild.getName())));
//         verify(guildRepository, times(1)).findByName(any(String.class));
//         verify(guildRepository, never()).save(any(Guild.class));
//     }

//     @Test
//     void testSaveNullGuild() {
//         assertNull(services.save(null));
//         verify(guildRepository, never()).findByName(any(String.class));
//         verify(guildRepository, never()).save(any(Guild.class));
//     }

//     @Test
//     void testUpdate() {
//         Guild guild = new Guild(UUID.randomUUID(), "Dog eat dog", "World");
//         when(guildRepository.findById(eq(guild.getGuildid()))).thenReturn(Optional.of(guild));
//         when(guildRepository.update(eq(guild))).thenReturn(guild);
//         assertEquals(guild, services.update(guild));
//         verify(guildRepository, times(1)).findById(any(UUID.class));
//         verify(guildRepository, times(1)).update(any(Guild.class));
//     }

//     @Test
//     void testUpdateWithoutId() {
//         Guild guild = new Guild("Bobby's", "World");
//         GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.update(guild));
//         assertTrue(exception.getMessage().contains(String.format("%s does not exist", guild.getGuildid())));
//         verify(guildRepository, never()).findById(any(UUID.class));
//         verify(guildRepository, never()).update(any(Guild.class));
//     }

//     @Test
//     void testUpdateGuildDoesNotExist() {
//         Guild guild = new Guild(UUID.randomUUID(), "Wayne's", "World 2");
//         when(guildRepository.findById(eq(guild.getGuildid()))).thenReturn(Optional.empty());
//         GuildBadArgException exception = assertThrows(GuildBadArgException.class, () -> services.update(guild));
//         assertTrue(exception.getMessage().contains(String.format("%s does not exist", guild.getGuildid())));
//         verify(guildRepository, times(1)).findById(any(UUID.class));
//         verify(guildRepository, never()).update(any(Guild.class));
//     }

//     @Test
//     void testUpdateNullGuild() {
//         assertNull(services.update(null));
//         verify(guildRepository, never()).findById(any(UUID.class));
//         verify(guildRepository, never()).update(any(Guild.class));
//     }

//     @Test
//     void testFindByFieldsNullParams() {
//         Set<Guild> guildSet = Set.of(
//                 new Guild(UUID.randomUUID(), "World", "Health Organization"),
//                 new Guild(UUID.randomUUID(), "World", "Wide Web")
//         );

//         when(guildRepository.findAll()).thenReturn(guildSet);
//         assertEquals(guildSet, services.findByFields(null, null));
//         verify(guildRepository, times(1)).findAll();
//         verify(guildRepository, never()).findById(any(UUID.class));
//         verify(guildRepository, never()).findByNameIlike(any(String.class));
//         verify(guildMemberRepository, never()).findByMemberid(any(UUID.class));
//     }

//     @Test
//     void testFindByFieldName() {
//         List<Guild> guild = List.of(
//                 new Guild(UUID.randomUUID(), "What a Wonderful", "World"),
//                 new Guild(UUID.randomUUID(), "World", "History")
//         );

//         List<Guild> guildToFind = List.of(guild.get(1));
//         final String nameSearch = "World";
//         when(guildRepository.findAll()).thenReturn(guild);
//         when(guildRepository.findByNameIlike(eq(nameSearch))).thenReturn(guildToFind);
//         assertEquals(new HashSet<>(guildToFind), services.findByFields(nameSearch, null));
//         verify(guildRepository, times(1)).findAll();
//         verify(guildRepository, never()).findById(any(UUID.class));
//         verify(guildRepository, times(1)).findByNameIlike(any(String.class));
//         verify(guildMemberRepository, never()).findByMemberid(any(UUID.class));
//     }

//     @Test
//     void testFindByFieldMemberid() {
//         List<Guild> guild = List.of(
//                 new Guild(UUID.randomUUID(), "A Brave New", "World"),
//                 new Guild(UUID.randomUUID(), "World", "of Warcraft")
//         );

//         final UUID memberId = UUID.randomUUID();
//         List<Guild> guildToFind = List.of(guild.get(0));
//         when(guildRepository.findAll()).thenReturn(guild);
//         when(guildRepository.findById(eq(guildToFind.get(0).getGuildid()))).thenReturn(Optional.of(guildToFind.get(0)));
//         when(guildMemberRepository.findByMemberid(eq(memberId))).thenReturn(Collections.singletonList(
//                 new GuildMember(UUID.randomUUID(), guildToFind.get(0).getGuildid(), memberId, true)));
//         assertEquals(new HashSet<>(guildToFind), services.findByFields(null, memberId));
//         verify(guildRepository, times(1)).findAll();
//         verify(guildRepository, times(1)).findById(any(UUID.class));
//         verify(guildRepository, never()).findByNameIlike(any(String.class));
//         verify(guildMemberRepository, times(1)).findByMemberid(any(UUID.class));
//     }

//     @Test
//     void testFindByFieldNameAndMemberid() {
//         List<Guild> guild = List.of(
//                 new Guild(UUID.randomUUID(), "World", "Series"),
//                 new Guild(UUID.randomUUID(), "Super Mario", "World")
//         );

//         final UUID memberId = UUID.randomUUID();
//         final String nameSearch = "World";
//         List<Guild> guildToFind = List.of(guild.get(0));
//         when(guildRepository.findAll()).thenReturn(guild);
//         when(guildRepository.findByNameIlike(eq(nameSearch))).thenReturn(guildToFind);
//         when(guildRepository.findById(eq(guildToFind.get(0).getGuildid()))).thenReturn(Optional.of(guildToFind.get(0)));
//         when(guildMemberRepository.findByMemberid(eq(memberId))).thenReturn(Collections.singletonList(
//                 new GuildMember(UUID.randomUUID(), guildToFind.get(0).getGuildid(), memberId, true)));
//         assertEquals(new HashSet<>(guildToFind), services.findByFields(nameSearch, memberId));
//         verify(guildRepository, times(1)).findAll();
//         verify(guildRepository, times(1)).findById(any(UUID.class));
//         verify(guildRepository, times(1)).findByNameIlike(any(String.class));
//         verify(guildMemberRepository, times(1)).findByMemberid(any(UUID.class));
//     }
// }

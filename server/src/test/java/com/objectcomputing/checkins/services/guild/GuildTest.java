package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.guild.member.GuildMemberHistoryRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.env.Environment;
import io.micronaut.core.util.StringUtils;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
// Disabled in nativeTest, as we get an exception from Mockito
//    => java.lang.NoClassDefFoundError: Could not initialize class org.mockito.Mockito
@DisabledInNativeImage
class GuildTest extends TestContainersSuite {

    @Inject
    private Validator validator;

    @Inject
    private CheckInsConfiguration checkInsConfiguration;

    @Inject
    @Named(MailJetFactory.HTML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    private GuildRepository guildsRepo;
    private GuildMemberRepository guildMemberRepo;
    private GuildMemberHistoryRepository guildMemberHistoryRepo;
    private CurrentUserServices currentUserServices;
    private MemberProfileServices memberProfileServices;
    private GuildMemberServices guildMemberServices;
    private Environment environment;
    private GuildServicesImpl guildServices;

    @BeforeEach
    @Tag("mocked")
    void setUp() {
        guildsRepo = Mockito.mock(GuildRepository.class);
        guildMemberRepo = Mockito.mock(GuildMemberRepository.class);
        guildMemberHistoryRepo = Mockito.mock(GuildMemberHistoryRepository.class);
        currentUserServices = Mockito.mock(CurrentUserServices.class);
        memberProfileServices = Mockito.mock(MemberProfileServices.class);
        guildMemberServices = Mockito.mock(GuildMemberServices.class);
        environment = Mockito.mock(Environment.class);

        emailSender.reset();

        guildServices = Mockito.spy(new GuildServicesImpl(
                guildsRepo,
                guildMemberRepo,
                guildMemberHistoryRepo,
                currentUserServices,
                memberProfileServices,
                guildMemberServices,
                emailSender,
                environment,
                checkInsConfiguration)
        );
    }

    @Test
    void testGuildInstantiation() {
        final String name = "name";
        final String description = "description";
        Guild guild = new Guild(name, description, null, false);
        assertEquals(guild.getName(), name);
        assertEquals(guild.getDescription(), description);
    }

    @Test
    void testGuildInstantiation2() {
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        Guild guild = new Guild(id, name, description, link, false);
        assertEquals(guild.getId(), id);
        assertEquals(guild.getName(), name);
        assertEquals(guild.getDescription(), description);

        Set<ConstraintViolation<Guild>> violations = validator.validate(guild);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Guild guild = new Guild(id, name, description, null, false);

        guild.setName("");

        Set<ConstraintViolation<Guild>> violations = validator.validate(guild);
        assertEquals(1, violations.size());
        for (ConstraintViolation<Guild> violation : violations) {
            assertEquals("must not be blank", violation.getMessage());
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        Guild g = new Guild(id, name, description, link, false);
        Guild g2 = new Guild(id, name, description, link, false);

        assertEquals(g, g2);

        g2.setId(null);

        assertNotEquals(g, g2);
    }

    @Test
    void testHash() {
        HashMap<Guild, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        Guild g = new Guild(id, name, description, link, false);

        map.put(g, true);

        assertTrue(map.get(g));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final String name = "name------name";
        final String description = "description------description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        final String isCommunity = "false";
        Guild g = new Guild(id, name, description,link, false);

        String s = g.toString();
        assertTrue(s.contains(name));
        assertTrue(s.contains(id.toString()));
        assertTrue(s.contains(description));
        assertTrue(s.contains(isCommunity));
    }
    @Test
    @Tag("mocked")
    void testEmailGuildLeadersWithValidGuild() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");
        guildLeadersEmails.add("leader2@example.com");

        Guild guild = new Guild();
        guild.setName("Test Guild");

        guildServices.emailGuildLeaders(guildLeadersEmails, guild);

        String[] emails = guildLeadersEmails.toArray(new String[0]);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "You have been assigned as a guild leader of Test Guild", "Congratulations, you have been assigned as a guild leader of Test Guild", String.join(",", emails)),
                emailSender.events.getFirst()
        );
    }

    @Test
    @Tag("mocked")
    void testEmailGuildLeadersWithNullGuild() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        guildServices.emailGuildLeaders(guildLeadersEmails, null);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testEmailGuildLeadersWithNullGuildName() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        Guild guild = new Guild();

        guildServices.emailGuildLeaders(guildLeadersEmails, guild);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testEmailGuildLeadersWithEmptyGuildName() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        Guild guild = new Guild();
        guild.setName("");

        guildServices.emailGuildLeaders(guildLeadersEmails, guild);

        assertEquals(0, emailSender.events.size());
    }


    @Test
    @Tag("mocked")
    void testSaveGuildWithValidData() {
        GuildCreateDTO guildDTO = new GuildCreateDTO();
        guildDTO.setName("Test Guild");
        String link = "http://example.com";
        guildDTO.setLink(link);
        guildDTO.setCommunity(true);

        UUID memberId = UUID.randomUUID();
        GuildCreateDTO.GuildMemberCreateDTO guildMemberCreateDTO = new GuildCreateDTO.GuildMemberCreateDTO();
        guildMemberCreateDTO.setMemberId(memberId);
        guildMemberCreateDTO.setLead(true);

        guildDTO.setGuildMembers(Collections.singletonList(guildMemberCreateDTO));

        MemberProfile memberProfile = new MemberProfile();
        memberProfile.setWorkEmail("test@example.com");

        Guild guild = new Guild(UUID.randomUUID(), "test", "example", link, true);
        when(guildsRepo.search(any(), any())).thenReturn(Collections.emptyList());
        when(guildsRepo.save(any())).thenReturn(guild);
        when(memberProfileServices.getById(any())).thenReturn(memberProfile);
        when(guildMemberRepo.save(any())).thenReturn(new GuildMember(UUID.randomUUID(), guild.getId(), guildMemberCreateDTO.getMemberId(), true));
        GuildResponseDTO response = guildServices.save(guildDTO);

        verify(guildsRepo, times(1)).save(any());

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "You have been assigned as a guild leader of test", "Congratulations, you have been assigned as a guild leader of test", memberProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    @Tag("mocked")
    void testSaveGuildWithExistingName() {
        GuildCreateDTO guildDTO = new GuildCreateDTO();
        guildDTO.setName("Existing Guild");

        when(guildsRepo.search(any(), any())).thenReturn(Collections.singletonList(new Guild()));

        assertThrows(BadArgException.class, () -> guildServices.save(guildDTO));

        verify(guildsRepo, never()).save(any());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSaveGuildWithoutLead() {
        GuildCreateDTO guildDTO = new GuildCreateDTO();
        guildDTO.setName("Test Guild");

        GuildCreateDTO.GuildMemberCreateDTO guildMemberCreateDTO = new GuildCreateDTO.GuildMemberCreateDTO();
        guildMemberCreateDTO.setMemberId(UUID.randomUUID());
        guildMemberCreateDTO.setLead(false);

        guildDTO.setGuildMembers(Collections.singletonList(guildMemberCreateDTO));

        when(guildsRepo.search(any(), any())).thenReturn(Collections.emptyList());

        assertThrows(BadArgException.class, () -> guildServices.save(guildDTO));

        verify(guildsRepo, never()).save(any());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSaveGuildWithNullDTO() {

        GuildResponseDTO response = guildServices.save(null);

        verify(guildsRepo, never()).save(any());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testUpdateGuildWithNewGuildLeaders() {
        UUID guildId = UUID.randomUUID();
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(guildId);
        guildDTO.setLink("http://example.com");

        UUID currentUserId = UUID.randomUUID();
        UUID memberId1 = UUID.randomUUID();
        UUID memberId2 = UUID.randomUUID();

        GuildUpdateDTO.GuildMemberUpdateDTO guildMember1 = new GuildUpdateDTO.GuildMemberUpdateDTO();
        guildMember1.setMemberId(memberId1);
        guildMember1.setLead(true);

        GuildUpdateDTO.GuildMemberUpdateDTO guildMember2 = new GuildUpdateDTO.GuildMemberUpdateDTO();
        guildMember2.setMemberId(memberId2);
        guildMember2.setLead(true);

        guildDTO.setGuildMembers(Arrays.asList(guildMember1, guildMember2));

        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(currentUserId);

        MemberProfile memberProfile1 = new MemberProfile();
        memberProfile1.setWorkEmail("leader1@example.com");

        MemberProfile memberProfile2 = new MemberProfile();
        memberProfile2.setWorkEmail("leader2@example.com");

        Guild existingGuild = new Guild();
        existingGuild.setId(guildId);
        existingGuild.setName("Test Guild");

        GuildMember existingGuildMember = new GuildMember();
        existingGuildMember.setMemberId(memberId1);
        existingGuildMember.setLead(true);

        GuildMember newGuildLeader = new GuildMember();
        newGuildLeader.setMemberId(memberId2);
        newGuildLeader.setLead(true);

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(currentUserServices.isAdmin()).thenReturn(true);
        when(guildsRepo.findById(guildId)).thenReturn(Optional.of(existingGuild));
        when(guildsRepo.update(any())).thenReturn(existingGuild);
        when(memberProfileServices.getById(memberId1)).thenReturn(memberProfile1);
        when(memberProfileServices.getById(memberId2)).thenReturn(memberProfile2);

        Set<GuildMember> initialGuildLeaders = Collections.singleton(existingGuildMember);
        Set<GuildMember> updatedGuildLeaders = new HashSet<>(initialGuildLeaders);
        updatedGuildLeaders.add(newGuildLeader);
        when(guildMemberServices.findByFields(guildId, null, true))
                .thenReturn(initialGuildLeaders)
                .thenReturn(updatedGuildLeaders);

        GuildResponseDTO response = guildServices.update(guildDTO);

        assertEquals(2, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "Membership Changes have been made to the Test Guild guild", "<h3>Changes have been made to the Test Guild guild.</h3><h4>The following members have been added:</h4><ul><li>null null</li><li>null null</li></ul><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.", memberProfile1.getWorkEmail()),
                emailSender.events.get(0)
        );
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "You have been assigned as a guild leader of Test Guild", "Congratulations, you have been assigned as a guild leader of Test Guild", memberProfile2.getWorkEmail()),
                emailSender.events.get(1)
        );
    }

    @Test
    @Tag("mocked")
    void testUpdateGuildWithNoNewGuildLeaders() {
        UUID guildId = UUID.randomUUID();
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(guildId);
        guildDTO.setLink("http://example.com");

        UUID currentUserId = UUID.randomUUID();
        UUID memberId1 = UUID.randomUUID();
        UUID memberId2 = UUID.randomUUID();

        GuildUpdateDTO.GuildMemberUpdateDTO guildMember1 = new GuildUpdateDTO.GuildMemberUpdateDTO();
        guildMember1.setMemberId(memberId1);
        guildMember1.setLead(true);

        GuildUpdateDTO.GuildMemberUpdateDTO guildMember2 = new GuildUpdateDTO.GuildMemberUpdateDTO();
        guildMember2.setMemberId(memberId2);
        guildMember2.setLead(true);

        guildDTO.setGuildMembers(Arrays.asList(guildMember1, guildMember2));

        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(currentUserId);

        MemberProfile memberProfile1 = new MemberProfile();
        memberProfile1.setWorkEmail("leader1@example.com");

        MemberProfile memberProfile2 = new MemberProfile();
        memberProfile2.setWorkEmail("leader2@example.com");

        Guild existingGuild = new Guild();
        existingGuild.setId(guildId);
        existingGuild.setName("Test Guild");

        GuildMember existingGuildMember = new GuildMember();
        existingGuildMember.setMemberId(memberId1);
        existingGuildMember.setLead(true);

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(currentUserServices.isAdmin()).thenReturn(true);
        when(guildsRepo.findById(guildId)).thenReturn(Optional.of(existingGuild));
        when(guildsRepo.update(any())).thenReturn(existingGuild);
        when(memberProfileServices.getById(memberId1)).thenReturn(memberProfile1);
        when(memberProfileServices.getById(memberId2)).thenReturn(memberProfile2);
        when(guildMemberServices.findByFields(guildId, null, true)).thenReturn(Collections.singleton(existingGuildMember));

        guildServices.update(guildDTO);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "Membership Changes have been made to the Test Guild guild", "<h3>Changes have been made to the Test Guild guild.</h3><h4>The following members have been added:</h4><ul><li>null null</li><li>null null</li></ul><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.", memberProfile1.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    @Tag("mocked")
    void testUpdateGuildWithNonExistentGuildId() {
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(UUID.randomUUID());  // Non-existent Guild ID

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(guildsRepo.findById(guildDTO.getId())).thenReturn(Optional.empty());

        assertThrows(BadArgException.class, () -> guildServices.update(guildDTO));

        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testUpdateGuildWithNoGuildLeads() {
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(UUID.randomUUID());
        guildDTO.setGuildMembers(Collections.emptyList());  // No guild members

        Guild existingGuild = new Guild();
        existingGuild.setId(guildDTO.getId());
        existingGuild.setName("Test Guild");

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(guildsRepo.findById(guildDTO.getId())).thenReturn(Optional.of(existingGuild));

        assertThrows(BadArgException.class, () -> guildServices.update(guildDTO));

        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testUpdateGuildWithUnauthorizedUser() {
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(UUID.randomUUID());

        MemberProfile currentUser = new MemberProfile();
        currentUser.setId(UUID.randomUUID());

        when(currentUserServices.getCurrentUser()).thenReturn(currentUser);
        when(currentUserServices.isAdmin()).thenReturn(false);
        when(guildMemberServices.findByFields(guildDTO.getId(), currentUser.getId(), true)).thenReturn(new HashSet<>());

        assertThrows(PermissionException.class, () -> {
            guildServices.update(guildDTO);
        });

        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testUpdateGuildWithInvalidLink() {
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(UUID.randomUUID());
        guildDTO.setLink("invalid-link");  // Invalid link

        Guild existingGuild = new Guild();
        existingGuild.setId(guildDTO.getId());
        existingGuild.setName("Test Guild");

        when(currentUserServices.isAdmin()).thenReturn(true);
        when(guildsRepo.findById(guildDTO.getId())).thenReturn(Optional.of(existingGuild));

        doThrow(new BadArgException("Invalid link")).when(guildServices).validateLink("invalid-link");

        assertThrows(BadArgException.class, () -> guildServices.update(guildDTO));

        assertEquals(0, emailSender.events.size());
    }
}

package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.guild.member.GuildMemberHistoryRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.context.env.Environment;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GuildTest extends TestContainersSuite {

    @Inject
    private Validator validator;
    private GuildRepository guildsRepo;
    private GuildMemberRepository guildMemberRepo;
    private GuildMemberHistoryRepository guildMemberHistoryRepo;
    private CurrentUserServices currentUserServices;
    private MemberProfileServices memberProfileServices;
    private GuildMemberServices guildMemberServices;
    private EmailSender emailSender;
    private Environment environment;
    private GuildServicesImpl guildServices;
    private String webAddress;

    @BeforeEach
    @Tag("mocked")
    void setUp() {
        guildsRepo = Mockito.mock(GuildRepository.class);
        guildMemberRepo = Mockito.mock(GuildMemberRepository.class);
        guildMemberHistoryRepo = Mockito.mock(GuildMemberHistoryRepository.class);
        currentUserServices = Mockito.mock(CurrentUserServices.class);
        memberProfileServices = Mockito.mock(MemberProfileServices.class);
        guildMemberServices = Mockito.mock(GuildMemberServices.class);
        emailSender = Mockito.mock(EmailSender.class);
        environment = Mockito.mock(Environment.class);

        webAddress = "http://example.com";

        guildServices = new GuildServicesImpl(
                guildsRepo,
                guildMemberRepo,
                guildMemberHistoryRepo,
                currentUserServices,
                memberProfileServices,
                guildMemberServices,
                emailSender,
                environment,
                webAddress
        );
    }

    @BeforeEach
    @Tag("mocked")
    void tearDown() {

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
        assertEquals(violations.size(), 1);
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
        verify(emailSender, times(1)).sendEmail(eq("HR"), eq("mckiernanc@objectcomputing.com"),
                eq("You have been assigned as a guild leader of Test Guild"),
                eq("Congratulations, you have been assigned as a guild leader of Test Guild"),
                eq(emails));
    }

    @Test
    @Tag("mocked")
    void testEmailGuildLeadersWithNullGuild() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        guildServices.emailGuildLeaders(guildLeadersEmails, null);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testEmailGuildLeadersWithNullGuildName() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        Guild guild = new Guild();

        guildServices.emailGuildLeaders(guildLeadersEmails, guild);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testEmailGuildLeadersWithEmptyGuildName() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        Guild guild = new Guild();
        guild.setName("");

        guildServices.emailGuildLeaders(guildLeadersEmails, guild);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

}

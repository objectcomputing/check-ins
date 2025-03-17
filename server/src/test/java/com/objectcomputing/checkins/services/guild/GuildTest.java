package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.guild.member.GuildMemberHistoryRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
class GuildTest extends TestContainersSuite
                implements MemberProfileFixture, RoleFixture {

    @Inject
    private Validator validator;

    @Inject
    private CurrentUserServicesReplacement currentUserServices;

    @Inject
    private CheckInsConfiguration checkInsConfiguration;

    @Inject
    @Named(MailJetFactory.HTML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    private GuildServicesImpl guildServices;

    @BeforeEach
    void setUp() {
        createAndAssignRoles();

        currentUserServices.currentUser = createADefaultMemberProfile();
        assignMemberRole(currentUserServices.currentUser);

        emailSender.reset();
    }

    @Test
    void testGuildInstantiation() {
        final String name = "name";
        final String description = "description";
        Guild guild = new Guild(name, description, null, false, true);
        assertEquals(guild.getName(), name);
        assertEquals(guild.getDescription(), description);
    }

    @Test
    void testGuildInstantiation2() {
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        Guild guild = new Guild(id, name, description, link, false, true);
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
        Guild guild = new Guild(id, name, description, null, false, true);

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
        Guild g = new Guild(id, name, description, link, false, true);
        Guild g2 = new Guild(id, name, description, link, false, true);

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
        Guild g = new Guild(id, name, description, link, false, true);

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
        Guild g = new Guild(id, name, description,link, false, true);

        String s = g.toString();
        assertTrue(s.contains(name));
        assertTrue(s.contains(id.toString()));
        assertTrue(s.contains(description));
        assertTrue(s.contains(isCommunity));
    }

    @Test
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
    void testEmailGuildLeadersWithNullGuild() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        guildServices.emailGuildLeaders(guildLeadersEmails, null);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testEmailGuildLeadersWithNullGuildName() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        Guild guild = new Guild();

        guildServices.emailGuildLeaders(guildLeadersEmails, guild);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testEmailGuildLeadersWithEmptyGuildName() {
        Set<String> guildLeadersEmails = new HashSet<>();
        guildLeadersEmails.add("leader1@example.com");

        Guild guild = new Guild();
        guild.setName("");

        guildServices.emailGuildLeaders(guildLeadersEmails, guild);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSaveGuildWithValidData() {
        GuildCreateDTO guildDTO = new GuildCreateDTO();
        guildDTO.setName("Test Guild");
        String link = "http://example.com";
        guildDTO.setLink(link);
        guildDTO.setCommunity(true);

        MemberProfile memberProfile = createASecondDefaultMemberProfile();
        GuildCreateDTO.GuildMemberCreateDTO guildMemberCreateDTO = new GuildCreateDTO.GuildMemberCreateDTO();
        guildMemberCreateDTO.setMemberId(memberProfile.getId());
        guildMemberCreateDTO.setLead(true);

        guildDTO.setGuildMembers(Collections.singletonList(guildMemberCreateDTO));

        GuildResponseDTO response = guildServices.save(guildDTO);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("You have been assigned as a guild leader of %s", guildDTO.getName()),
                        String.format("Congratulations, you have been assigned as a guild leader of %s", guildDTO.getName()),
                        memberProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }


    GuildResponseDTO createGuild(String name, MemberProfile lead) {
        GuildCreateDTO guildDTO = new GuildCreateDTO();
        guildDTO.setName(name);
        GuildCreateDTO.GuildMemberCreateDTO guildMemberCreateDTO = new GuildCreateDTO.GuildMemberCreateDTO();
        guildMemberCreateDTO.setMemberId(lead.getId());
        guildMemberCreateDTO.setLead(true);
        List<GuildCreateDTO.GuildMemberCreateDTO> members = new ArrayList<>();
        members.add(guildMemberCreateDTO);
        guildDTO.setGuildMembers(members);
        return guildServices.save(guildDTO);
    }

    @Test
    void testSaveGuildWithExistingName() {
        MemberProfile memberProfile = createASecondDefaultMemberProfile();
        createGuild("Existing Guild", memberProfile);
        emailSender.reset();

        GuildCreateDTO existingGuildDTO = new GuildCreateDTO();
        existingGuildDTO.setName("Existing Guild");
        GuildCreateDTO.GuildMemberCreateDTO guildMemberCreateDTO = new GuildCreateDTO.GuildMemberCreateDTO();
        guildMemberCreateDTO.setMemberId(memberProfile.getId());
        guildMemberCreateDTO.setLead(true);
        existingGuildDTO.setGuildMembers(Collections.singletonList(guildMemberCreateDTO));

        assertThrows(BadArgException.class, () -> guildServices.save(existingGuildDTO));

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSaveGuildWithoutLead() {
        GuildCreateDTO guildDTO = new GuildCreateDTO();
        guildDTO.setName("Test Guild");

        GuildCreateDTO.GuildMemberCreateDTO guildMemberCreateDTO = new GuildCreateDTO.GuildMemberCreateDTO();
        guildMemberCreateDTO.setMemberId(UUID.randomUUID());
        guildMemberCreateDTO.setLead(false);

        guildDTO.setGuildMembers(Collections.singletonList(guildMemberCreateDTO));

        assertThrows(BadArgException.class, () -> guildServices.save(guildDTO));
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSaveGuildWithNullDTO() {
        GuildResponseDTO response = guildServices.save(null);
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testUpdateGuildWithNewGuildLeaders() {
        // Create members involved.
        MemberProfile memberProfile1 = createASecondDefaultMemberProfile();
        MemberProfile memberProfile2 = createAThirdDefaultMemberProfile();

        // Create the existing guild with member1 as lead.
        GuildResponseDTO existing = createGuild("Test Guild", memberProfile1);
        emailSender.reset();

        // Create an update DTO.
        UUID guildId = existing.getId();
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setName(existing.getName());
        guildDTO.setId(guildId);
        guildDTO.setLink("http://example.com");

        // Set the new guild members
        List<GuildMemberResponseDTO> exMembers = existing.getGuildMembers();
        GuildUpdateDTO.GuildMemberUpdateDTO guildMember1 = new GuildUpdateDTO.GuildMemberUpdateDTO();
        guildMember1.setId(exMembers.get(0).getId());
        guildMember1.setMemberId(exMembers.get(0).getMemberId());
        guildMember1.setLead(exMembers.get(0).getLead());

        GuildUpdateDTO.GuildMemberUpdateDTO guildMember2 = new GuildUpdateDTO.GuildMemberUpdateDTO();
        guildMember2.setMemberId(memberProfile2.getId());
        guildMember2.setLead(true);

        guildDTO.setGuildMembers(Arrays.asList(guildMember1, guildMember2));

        // We need the current user to be logged in and admin.
        assignAdminRole(currentUserServices.currentUser);

        GuildResponseDTO response = guildServices.update(guildDTO);
        assertEquals(2, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        "Membership Changes have been made to the Test Guild guild",
                        String.format("<h3>Changes have been made to the Test Guild guild.</h3><h4>The following members have been added:</h4><ul><li>%s %s</li></ul><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.", memberProfile2.getFirstName(), memberProfile2.getLastName()),
                        memberProfile1.getWorkEmail()),
                emailSender.events.get(0)
        );
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "You have been assigned as a guild leader of Test Guild", "Congratulations, you have been assigned as a guild leader of Test Guild", memberProfile2.getWorkEmail()),
                emailSender.events.get(1)
        );
    }

    @Test
    void testUpdateGuildWithNoNewGuildLeaders() {
        // Create members involved.
        MemberProfile memberProfile1 = createASecondDefaultMemberProfile();
        MemberProfile memberProfile2 = createAThirdDefaultMemberProfile();

        // Create the existing guild with member1 as lead.
        GuildResponseDTO existing = createGuild("Test Guild", memberProfile1);
        emailSender.reset();

        // Create an update DTO.
        UUID guildId = existing.getId();
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setName(existing.getName());
        guildDTO.setId(guildId);
        guildDTO.setLink("http://example.com");

        // Set the new guild members
        List<GuildMemberResponseDTO> exMembers = existing.getGuildMembers();
        GuildUpdateDTO.GuildMemberUpdateDTO guildMember1 = new GuildUpdateDTO.GuildMemberUpdateDTO();
        guildMember1.setId(exMembers.get(0).getId());
        guildMember1.setMemberId(exMembers.get(0).getMemberId());
        guildMember1.setLead(exMembers.get(0).getLead());

        GuildUpdateDTO.GuildMemberUpdateDTO guildMember2 = new GuildUpdateDTO.GuildMemberUpdateDTO();
        guildMember2.setMemberId(memberProfile2.getId());
        guildMember2.setLead(false);

        guildDTO.setGuildMembers(Arrays.asList(guildMember1, guildMember2));

        // We need the current user to be logged in and admin.
        assignAdminRole(currentUserServices.currentUser);

        GuildResponseDTO response = guildServices.update(guildDTO);
        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        "Membership Changes have been made to the Test Guild guild",
                        String.format("<h3>Changes have been made to the Test Guild guild.</h3><h4>The following members have been added:</h4><ul><li>%s %s</li></ul><a href=\"https://checkins.objectcomputing.com/guilds\">Click here</a> to view the changes in the Check-Ins app.", memberProfile2.getFirstName(), memberProfile2.getLastName()),
                        memberProfile1.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testUpdateGuildWithNonExistentGuildId() {
        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(UUID.randomUUID());  // Non-existent Guild ID

        // We need the current user to be logged in and admin.
        assignAdminRole(currentUserServices.currentUser);

        assertThrows(BadArgException.class, () -> guildServices.update(guildDTO));

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testUpdateGuildWithNoGuildLeads() {
        // Create the existing guild with member1 as lead.
        MemberProfile memberProfile1 = createASecondDefaultMemberProfile();
        GuildResponseDTO existing = createGuild("Test Guild", memberProfile1);
        emailSender.reset();

        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(existing.getId());
        guildDTO.setGuildMembers(Collections.emptyList());  // No guild members

        // We need the current user to be logged in and admin.
        assignAdminRole(currentUserServices.currentUser);

        assertThrows(BadArgException.class, () -> guildServices.update(guildDTO));

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testUpdateGuildWithUnauthorizedUser() {
        // Create the existing guild with member1 as lead.
        MemberProfile memberProfile1 = createASecondDefaultMemberProfile();
        GuildResponseDTO existing = createGuild("Test Guild", memberProfile1);
        emailSender.reset();

        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(existing.getId());
        guildDTO.setGuildMembers(Collections.emptyList());  // No guild members

        assertThrows(PermissionException.class, () -> {
            guildServices.update(guildDTO);
        });

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testUpdateGuildWithInvalidLink() {
        // Create the existing guild with member1 as lead.
        MemberProfile memberProfile1 = createASecondDefaultMemberProfile();
        GuildResponseDTO existing = createGuild("Test Guild", memberProfile1);
        emailSender.reset();

        GuildUpdateDTO guildDTO = new GuildUpdateDTO();
        guildDTO.setId(existing.getId());
        guildDTO.setLink("invalid-link");  // Invalid link

        // We need the current user to be logged in and admin.
        assignAdminRole(currentUserServices.currentUser);

        assertThrows(BadArgException.class, () -> guildServices.update(guildDTO));

        assertEquals(0, emailSender.events.size());
    }
}

package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
class MemberProfileTest extends TestContainersSuite
                        implements MemberProfileFixture {

    @Inject
    protected Validator validator;

    @Inject
    @Named(MailJetFactory.HTML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    CurrentUserServicesReplacement currentUserServices;

    @Inject
    private MemberProfileServicesImpl memberProfileServices;

    @BeforeEach
    void setUp() {
        emailSender.reset();
    }

    @Test
    void testMemberProfileInstantiation() {
        String firstName = "Fred";
        String lastName = "Smith";
        String workEmail = "example@email.com";
        MemberProfile memberProfile = new MemberProfile(firstName, null, lastName,
                null, null, null, null, workEmail,
                null, null, null, null,
                null, null, null, null, null);
        assertEquals(firstName, memberProfile.getFirstName());
        assertEquals(lastName, memberProfile.getLastName());
        assertEquals(workEmail, memberProfile.getWorkEmail());
    }

    @Test
    void testConstraintViolation() {
        String firstName = "Fred";
        String lastName = "";
        String workEmail = "example@email.com";
        MemberProfile memberProfile = new MemberProfile(firstName, null, lastName,
                null, null, null, null, workEmail,
                null, null, null, null,
                null, null, null, null, null);
        assertEquals(firstName, memberProfile.getFirstName());
        assertEquals(lastName, memberProfile.getLastName());
        assertEquals(workEmail, memberProfile.getWorkEmail());

        Set<ConstraintViolation<MemberProfile>> violations = validator.validate(memberProfile);
        assertEquals(1, violations.size());
        for (ConstraintViolation<MemberProfile> violation : violations) {
            assertEquals("must not be blank", violation.getMessage());
        }
    }

    @Test
    void testEquals() {
        UUID id = UUID.randomUUID();
        String firstName = "John";
        String middleName = "Doe";
        String lastName = "Smith";
        String suffix = "Jr.";
        String title = "Manager";
        UUID pdlId = UUID.randomUUID();
        String location = "New York";
        String workEmail = "john.smith@example.com";
        String employeeId = "EMP123";
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        String bioText = "Experienced manager with a focus on team building.";
        UUID supervisorId = UUID.randomUUID();
        LocalDate terminationDate = LocalDate.of(2024, 5, 31);
        LocalDate birthDate = LocalDate.of(1985, 10, 15);
        Boolean voluntary = false;
        Boolean excluded = true;
        LocalDate lastSeen = LocalDate.now();

        MemberProfile memberProfile = new MemberProfile(id, firstName, middleName, lastName, suffix, title, pdlId, location, workEmail,
                employeeId, startDate, bioText, supervisorId, terminationDate, birthDate, voluntary, excluded, lastSeen);

        assertEquals(id, memberProfile.getId());
        assertEquals(firstName, memberProfile.getFirstName());
        assertEquals(middleName, memberProfile.getMiddleName());
        assertEquals(lastName, memberProfile.getLastName());
        assertEquals(suffix, memberProfile.getSuffix());
        assertEquals(title, memberProfile.getTitle());
        assertEquals(pdlId, memberProfile.getPdlId());
        assertEquals(location, memberProfile.getLocation());
        assertEquals(workEmail, memberProfile.getWorkEmail());
        assertEquals(employeeId, memberProfile.getEmployeeId());
        assertEquals(startDate, memberProfile.getStartDate());
        assertEquals(bioText, memberProfile.getBioText());
        assertEquals(supervisorId, memberProfile.getSupervisorid());
        assertEquals(terminationDate, memberProfile.getTerminationDate());
        assertEquals(birthDate, memberProfile.getBirthDate());
        assertEquals(voluntary, memberProfile.getVoluntary());
        assertEquals(excluded, memberProfile.getExcluded());
        assertEquals(lastSeen, memberProfile.getLastSeen());
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final String firstName = "John";
        final String middleName = "Doe";
        final String lastName = "Smith";
        final String suffix = "Jr.";
        final String title = "Manager";
        final UUID pdlId = UUID.randomUUID();
        final String location = "New York";
        final String workEmail = "john.smith@example.com";
        final String employeeId = "EMP123";
        final LocalDate startDate = LocalDate.of(2020, 1, 1);
        final String bioText = "Experienced manager with a focus on team building.";
        final UUID supervisorId = UUID.randomUUID();
        final LocalDate terminationDate = LocalDate.of(2024, 5, 31);
        final LocalDate birthDate = LocalDate.of(1985, 10, 15);
        final Boolean voluntary = false;
        final Boolean excluded = true;
        final LocalDate lastSeen = LocalDate.now();

        MemberProfile memberProfile = new MemberProfile(id, firstName, middleName, lastName, suffix, title, pdlId, location, workEmail,
                employeeId, startDate, bioText, supervisorId, terminationDate, birthDate, voluntary, excluded, lastSeen);

        String toString = memberProfile.toString();
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(firstName));
        assertTrue(toString.contains(middleName));
        assertTrue(toString.contains(lastName));
        assertTrue(toString.contains(suffix));
        assertTrue(toString.contains(title));
        assertTrue(toString.contains(pdlId.toString()));
        assertTrue(toString.contains(location));
        assertTrue(toString.contains(workEmail));
        assertTrue(toString.contains(employeeId));
        assertTrue(toString.contains(startDate.toString()));
        assertTrue(toString.contains(bioText));
        assertTrue(toString.contains(supervisorId.toString()));
        assertTrue(toString.contains(terminationDate.toString()));
        assertTrue(toString.contains(birthDate.toString()));
        assertTrue(toString.contains(voluntary.toString()));
        assertTrue(toString.contains(excluded.toString()));
        assertTrue(toString.contains(lastSeen.toString()));
    }

    @Test
    void testSaveProfileWithExistingEmail() {
        MemberProfile existingProfile = createADefaultMemberProfile();
        String workEmail = existingProfile.getWorkEmail();
        MemberProfile newProfile = new MemberProfile(UUID.randomUUID(), "John", null, "Smith", null, null, null, null, workEmail, null, null, null, null, null, null, null, null, null);

        assertThrows(AlreadyExistsException.class, () -> memberProfileServices.saveProfile(newProfile));
    }

    @Test
    void testSaveProfileWithNewEmail() {
        MemberProfile pdlProfile = createADefaultMemberProfile();
        MemberProfile supervisorProfile = createADefaultSupervisor();
        MemberProfile newProfile = new MemberProfile("Charizard", null, "Char",
                null, "Local fire hazard", pdlProfile.getId(),
                "New York, New York",
                "charizard@objectcomputing.com", "local-kaiju",
                LocalDate.now().minusDays(3).minusYears(5),
                "Needs supervision due to building being ultra flammable",
                supervisorProfile.getId(), null, null, null, null,
                LocalDate.now());

        MemberProfile savedProfile = memberProfileServices.saveProfile(newProfile);

        // Assertions to verify the saved profile is not null and is equal to the newProfile
        assertNotNull(savedProfile, "The saved profile should not be null");
        assertEquals(newProfile, savedProfile);

        assertEquals(2, emailSender.events.size());
        assertEquals(List.of(
                        List.of("SEND_EMAIL", "null", "null",
                                String.format("You have been assigned as the PDL of %s %s", newProfile.getFirstName(), newProfile.getLastName()),
                                String.format("%s %s will now report to you as their PDL. Please engage with them: %s", newProfile.getFirstName(), newProfile.getLastName(), newProfile.getWorkEmail()),
                                pdlProfile.getWorkEmail()),
                        List.of("SEND_EMAIL", "null", "null",
                                String.format("You have been assigned as the supervisor of %s %s", newProfile.getFirstName(), newProfile.getLastName()),
                                String.format("%s %s will now report to you as their supervisor. Please engage with them: %s", newProfile.getFirstName(), newProfile.getLastName(), newProfile.getWorkEmail()),
                                supervisorProfile.getWorkEmail())
                ),
                emailSender.events
        );
    }

    @Test
    void testUpdateProfileWithChangedPDL() {
        MemberProfile existingProfile = createADefaultMemberProfile();
        MemberProfile pdlProfile = createASecondDefaultMemberProfile();
        UUID id = existingProfile.getId();
        UUID pdlId = pdlProfile.getId();

        currentUserServices.currentUser = existingProfile;

        MemberProfile updatedProfile = new MemberProfile(id, existingProfile.getFirstName(), null, existingProfile.getLastName(), null, null, pdlId, null, existingProfile.getWorkEmail(), null, null, null, null, null, null, null, null, null);

        MemberProfile result = memberProfileServices.updateProfile(updatedProfile);

        assertEquals(updatedProfile, result);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("You have been assigned as the PDL of %s %s", existingProfile.getFirstName(), existingProfile.getLastName()),
                        String.format("%s %s will now report to you as their PDL. Please engage with them: %s", existingProfile.getFirstName(), existingProfile.getLastName(), existingProfile.getWorkEmail()),
                        pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testUpdateProfileWithChangedSupervisor() {
        MemberProfile existingProfile = createADefaultMemberProfile();
        MemberProfile supervisorProfile = createASecondDefaultMemberProfile();
        UUID id = existingProfile.getId();
        UUID supervisorId = supervisorProfile.getId();

        currentUserServices.currentUser = existingProfile;

        MemberProfile updatedProfile = new MemberProfile(id, existingProfile.getFirstName(), null, existingProfile.getLastName(), null, null, null, null, existingProfile.getWorkEmail(), null, null, null, supervisorId, null, null, null, null, null);

        MemberProfile result = memberProfileServices.updateProfile(updatedProfile);

        assertEquals(updatedProfile, result);
        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("You have been assigned as the supervisor of %s %s", existingProfile.getFirstName(), existingProfile.getLastName()),
                        String.format("%s %s will now report to you as their supervisor. Please engage with them: %s", existingProfile.getFirstName(), existingProfile.getLastName(), existingProfile.getWorkEmail()),
                        supervisorProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testUpdateProfileWithNoChange() {
        MemberProfile existingProfile = createADefaultMemberProfile();

        currentUserServices.currentUser = existingProfile;

        MemberProfile result = memberProfileServices.updateProfile(existingProfile);

        assertEquals(existingProfile, result);
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testEmailAssignmentWithValidPDL() {
        MemberProfile pdlProfile = createADefaultMemberProfile();
        MemberProfile member = createADefaultMemberProfileForPdl(pdlProfile);

        memberProfileServices.emailAssignment(member, true);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("You have been assigned as the PDL of %s %s", member.getFirstName(), member.getLastName()),
                        String.format("%s %s will now report to you as their PDL. Please engage with them: %s", member.getFirstName(), member.getLastName(), member.getWorkEmail()),
                        pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testEmailAssignmentWithValidSupervisor() {
        MemberProfile pdlProfile = createADefaultMemberProfile();
        MemberProfile supervisorProfile = createADefaultSupervisor();
        MemberProfile member = createAProfileWithSupervisorAndPDL(
                                   supervisorProfile, pdlProfile);

        memberProfileServices.emailAssignment(member, false);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("You have been assigned as the supervisor of %s %s", member.getFirstName(), member.getLastName()),
                        String.format("%s %s will now report to you as their supervisor. Please engage with them: %s", member.getFirstName(), member.getLastName(), member.getWorkEmail()),
                        supervisorProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testEmailAssignmentWithValidPdlAndSupervisor() {
        MemberProfile pdlProfile = createADefaultMemberProfile();
        MemberProfile supervisorProfile = createADefaultSupervisor();
        MemberProfile member = createAProfileWithSupervisorAndPDL(
                                   supervisorProfile, pdlProfile);

        memberProfileServices.emailAssignment(member, true); // for PDL
        memberProfileServices.emailAssignment(member, false); // for supervisor

        assertEquals(2, emailSender.events.size());
        assertEquals(List.of(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("You have been assigned as the PDL of %s %s", member.getFirstName(), member.getLastName()),
                        String.format("%s %s will now report to you as their PDL. Please engage with them: %s", member.getFirstName(), member.getLastName(), member.getWorkEmail()),
                        pdlProfile.getWorkEmail()),
                List.of("SEND_EMAIL", "null", "null",
                        String.format("You have been assigned as the supervisor of %s %s", member.getFirstName(), member.getLastName()),
                        String.format("%s %s will now report to you as their supervisor. Please engage with them: %s", member.getFirstName(), member.getLastName(), member.getWorkEmail()),
                        supervisorProfile.getWorkEmail())
                ),
                emailSender.events
        );
    }

    @Test
    void testEmailAssignmentWithInvalidPDL() {
        MemberProfile existingProfile = createADefaultMemberProfile();
        UUID pdlId = UUID.randomUUID();
        MemberProfile member = new MemberProfile(existingProfile.getId(), existingProfile.getFirstName(), null, existingProfile.getLastName(), null, null, pdlId, null, existingProfile.getWorkEmail(), null, null, null, null, null, null, null, null, null);

        memberProfileServices.emailAssignment(member, true);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testEmailAssignmentWithInvalidSupervisor() {
        MemberProfile existingProfile = createADefaultMemberProfile();
        UUID supervisorId = UUID.randomUUID();
        MemberProfile member = new MemberProfile(existingProfile.getId(), existingProfile.getFirstName(), null, existingProfile.getLastName(), null, null, null, null, existingProfile.getWorkEmail(), null, null, null, supervisorId, null, null, null, null, null);

        memberProfileServices.emailAssignment(member, true);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testEmailAssignmentWithInvalidMember() {
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);

        memberProfileServices.emailAssignment(member, true);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testEmailAssignmentWithNullRoleId() {
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, "Smith", null, null, null, null, "john.smith@example.com",
                null, null, null, null, null, null, null, null, null);

        memberProfileServices.emailAssignment(member, true);

        assertEquals(0, emailSender.events.size());
    }
}

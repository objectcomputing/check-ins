package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.objectcomputing.checkins.services.role.RoleServices;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberProfileTest extends TestContainersSuite {
    @Inject
    protected Validator validator;
    private MemberProfileRepository memberRepo;
    private EmailSender emailSender;

    private MemberProfileServicesImpl memberProfileServices;

    @BeforeEach
    @Tag("mocked")
    void setUp() {
        memberRepo = Mockito.mock(MemberProfileRepository.class);
        CurrentUserServices currentUserServices = Mockito.mock(CurrentUserServices.class);
        RoleServices roleServices = Mockito.mock(RoleServices.class);
        CheckInServices checkinServices = Mockito.mock(CheckInServices.class);
        MemberSkillServices memberSkillServices = Mockito.mock(MemberSkillServices.class);
        TeamMemberServices teamMemberServices = Mockito.mock(TeamMemberServices.class);
        emailSender = Mockito.mock(EmailSender.class);

        memberProfileServices = new MemberProfileServicesImpl(
                memberRepo,
                currentUserServices,
                roleServices,
                checkinServices,
                memberSkillServices,
                teamMemberServices,
                emailSender);
    }

    @AfterEach
    @Tag("mocked")
    void tearDown() {
        Mockito.reset(memberRepo, emailSender);
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
    @Tag("mocked")
    void testEmailAssignmentWithValidPDL() {
        UUID pdlId = UUID.randomUUID();
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, "Smith", null, null, pdlId, null, "john.smith@example.com",
                null, null, null, null, null, null, null, null, null);
        MemberProfile pdlProfile = new MemberProfile(UUID.randomUUID(), "Jane", null, "Doe", null, null, null, null, "jane.doe@example.com",
                null, null, null, null, null, null, null, null, null);

        when(memberRepo.findById(pdlId)).thenReturn(Optional.of(pdlProfile));

        memberProfileServices.emailAssignment(member, true);

        verify(emailSender, times(1)).sendEmail(
                any(), // from email
                any(), // reply-to email
                eq("You have been assigned as the PDL of John Smith"), // subject
                eq("John Smith will now report to you as their PDL. Please engage with them: john.smith@example.com"), // body
                eq("jane.doe@example.com")); // recipient
    }

    @Test
    @Tag("mocked")
    void testEmailAssignmentWithValidSupervisor() {
        UUID supervisorId = UUID.randomUUID();
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, "Smith", null, null, null, null, "john.smith@example.com",
                null, null, null, supervisorId, null, null, null, null, null);
        MemberProfile supervisorProfile = new MemberProfile(UUID.randomUUID(), "Jane", null, "Doe", null, null, null, null, "jane.doe@example.com",
                null, null, null, null, null, null, null, null, null);

        when(memberRepo.findById(supervisorId)).thenReturn(Optional.of(supervisorProfile));

        memberProfileServices.emailAssignment(member, false);

        verify(emailSender, times(1)).sendEmail(
                any(), // from email
                any(), // reply-to email
                eq("You have been assigned as the supervisor of John Smith"),
                eq("John Smith will now report to you as their supervisor. Please engage with them: john.smith@example.com"),
                eq("jane.doe@example.com"));
    }

    @Test
    @Tag("mocked")
    void testEmailAssignmentWithValidPdlAndSupervisor() {
        UUID pdlId = UUID.randomUUID();
        UUID supervisorId = UUID.randomUUID();
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, "Smith", null, null, pdlId, null, "john.smith@example.com",
                null, null, null, supervisorId, null, null, null, null, null);
        MemberProfile pdlProfile = new MemberProfile(UUID.randomUUID(), "Jane", null, "Doe", null, null, null, null, "jane.doe@example.com",
                null, null, null, null, null, null, null, null, null);

        MemberProfile supervisorProfile = new MemberProfile(UUID.randomUUID(), "Janine", null, "Doe", null, null, null, null, "janine.doe@example.com",
                null, null, null, null, null, null, null, null, null);

        when(memberRepo.findById(pdlId)).thenReturn(Optional.of(pdlProfile));
        when(memberRepo.findById(supervisorId)).thenReturn(Optional.of(supervisorProfile));

        memberProfileServices.emailAssignment(member, true); // for PDL
        memberProfileServices.emailAssignment(member, false); // for supervisor

        verify(emailSender, times(1)).sendEmail(
                any(), // from email
                any(), // reply-to email
                eq("You have been assigned as the PDL of John Smith"),
                eq("John Smith will now report to you as their PDL. Please engage with them: john.smith@example.com"),
                eq("jane.doe@example.com"));

        verify(emailSender, times(1)).sendEmail(
                any(), // from email
                any(), // reply-to email
                eq("You have been assigned as the supervisor of John Smith"),
                eq("John Smith will now report to you as their supervisor. Please engage with them: john.smith@example.com"),
                eq("janine.doe@example.com"));
    }

    @Test
    @Tag("mocked")
    void testEmailAssignmentWithInvalidPDL() {
        UUID pdlId = UUID.randomUUID();
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, "Smith", null, null, pdlId, null, "john.smith@example.com",
                null, null, null, null, null, null, null, null, null);

        when(memberRepo.findById(pdlId)).thenReturn(Optional.empty());

        memberProfileServices.emailAssignment(member, true);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testEmailAssignmentWithInvalidSupervisor() {
        UUID supervisorId = UUID.randomUUID();
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, "Smith", null, null, null, null, "john.smith@example.com",
                null, null, null, supervisorId, null, null, null, null, null);

        when(memberRepo.findById(supervisorId)).thenReturn(Optional.empty());

        memberProfileServices.emailAssignment(member, true);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testEmailAssignmentWithInvalidMember() {
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);

        memberProfileServices.emailAssignment(member, true);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testEmailAssignmentWithNullRoleId() {
        MemberProfile member = new MemberProfile(UUID.randomUUID(), "John", null, "Smith", null, null, null, null, "john.smith@example.com",
                null, null, null, null, null, null, null, null, null);

        memberProfileServices.emailAssignment(member, true);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }
}

package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
class PulseResponseTest extends TestContainersSuite {

    @Inject
    protected Validator validator;

    @Inject
    @Named(MailJetFactory.HTML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    private MemberProfileServices memberProfileServices;
    private MemberProfileRepository memberRepo;
    private CurrentUserServices currentUserServices;
    private RolePermissionServices rolePermissionServices;
    private PulseResponseServicesImpl pulseResponseService;
    private PulseResponseRepository pulseResponseRepo;

    @BeforeEach
    @Tag("mocked")
    void setUp() {
        pulseResponseRepo = Mockito.mock(PulseResponseRepository.class);
        memberProfileServices = Mockito.mock(MemberProfileServices.class);
        memberRepo = Mockito.mock(MemberProfileRepository.class);
        currentUserServices = Mockito.mock(CurrentUserServices.class);
        rolePermissionServices = Mockito.mock(RolePermissionServices.class);
        emailSender.reset();

        pulseResponseService = Mockito.spy(new PulseResponseServicesImpl(pulseResponseRepo,
                memberProfileServices,
                memberRepo,
                currentUserServices,
                rolePermissionServices,
                emailSender));
    }

    @AfterEach
    @Tag("mocked")
    void tearDown() {
        Mockito.reset(memberRepo, memberProfileServices);
    }

    @Test
    void testPulseResponseInstantiation() {
        LocalDate submissionDate = LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = UUID.randomUUID();
        final String internalFeelings = "exampleId";
        final String externalFeelings = "exampleId2";
        PulseResponse pulseResponse = new PulseResponse(1, 2, submissionDate, teamMemberId, internalFeelings, externalFeelings);
        assertEquals(teamMemberId, pulseResponse.getTeamMemberId());
        assertEquals(internalFeelings, pulseResponse.getInternalFeelings());
        assertEquals(externalFeelings, pulseResponse.getExternalFeelings());
    }

    @Test
    void testConstraintViolation() {
        LocalDate submissionDate = LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = UUID.randomUUID();
        final String internalFeelings = "exampleId";
        final String externalFeelings = "exampleId2";
        final Integer internalScore = 1;
        final Integer externalScore = 2;
        PulseResponse pulseResponse = new PulseResponse(internalScore, externalScore, submissionDate, teamMemberId, internalFeelings, externalFeelings);

        pulseResponse.setInternalScore(null);
        pulseResponse.setExternalFeelings(null);

        Set<ConstraintViolation<PulseResponse>> violations = validator.validate(pulseResponse);
        assertEquals(1, violations.size());
        for (ConstraintViolation<PulseResponse> violation : violations) {
            assertEquals("must not be null", violation.getMessage());
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final Integer internalScore = 1;
        final Integer externalScore = 2;
        LocalDate submissionDate = LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = UUID.randomUUID();
        final String internalFeelings = "exampleId";
        final String externalFeelings = "exampleId2";

        PulseResponse pulseResponse1 = new PulseResponse(id, internalScore, externalScore, submissionDate, teamMemberId, internalFeelings, externalFeelings);
        PulseResponse pulseResponse2 = new PulseResponse(id, internalScore, externalScore, submissionDate, teamMemberId, internalFeelings, externalFeelings);
        assertEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setId(null);
        assertNotEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setId(pulseResponse1.getId());
        assertEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setInternalScore(pulseResponse1.getInternalScore());
        assertEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setExternalScore(pulseResponse1.getExternalScore());
        assertEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setInternalFeelings("exampleId2");
        pulseResponse2.setExternalFeelings("exampleId3");
        assertNotEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setInternalScore(3);
        pulseResponse2.setExternalScore(4);
        assertNotEquals(pulseResponse1, pulseResponse2);
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        LocalDate submissionDate = LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = UUID.randomUUID();
        final Integer internalScore = 1;
        final Integer externalScore = 2;
        final String internalFeelings = "exampleId";
        final String externalFeelings = "exampleId2";
        PulseResponse pulseResponse = new PulseResponse(id, internalScore, externalScore, submissionDate, teamMemberId, internalFeelings, externalFeelings);

        String toString = pulseResponse.toString();
        assertTrue(toString.contains(teamMemberId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(internalFeelings));
        assertTrue(toString.contains(externalFeelings));
    }

    @Test
    @Tag("mocked")
    void testSaveWithValidPulseResponse() {
        UUID currentUserId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate pulseSubDate = LocalDate.now();

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        MemberProfile memberProfile = new MemberProfile(currentUserId, "John", null, "Doe",
                null, null, null, null, "john@oci.com",
                null, null, null, null,
                null, null, null, null, null);

        MemberProfile memberProfile2 = new MemberProfile(memberId, "Jane", null, "Doe",
                null, null, null, null, "jane@oci.com",
                null, null, null, currentUserId,
                null, null, null, null, null);

        when(currentUserServices.getCurrentUser()).thenReturn(memberProfile);
        when(memberRepo.findById(memberId)).thenReturn(Optional.of(memberProfile2));
        when(memberProfileServices.getSubordinatesForId(currentUserId)).thenReturn(Collections.singletonList(memberProfile2));

        PulseResponse savedResponse = new PulseResponse(); // Assuming this is a valid saved response
        when(pulseResponseRepo.save(pulseResponse)).thenReturn(savedResponse);

        PulseResponse result = pulseResponseService.save(pulseResponse);

        assertEquals(savedResponse, result);
        verify(pulseResponseRepo, times(1)).save(pulseResponse);
        verify(pulseResponseService, times(1)).sendPulseLowScoreEmail(savedResponse);
    }

    @Test
    @Tag("mocked")
    void testSaveWithNonNullId() {
        UUID currentUserId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate pulseSubDate = LocalDate.now();

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setId(UUID.randomUUID()); // Non-null ID
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);
        MemberProfile memberProfile = new MemberProfile(currentUserId, "John", null, "Doe",
                null, null, null, null, "john@oci.com",
                null, null, null, null,
                null, null, null, null, null);
        when(currentUserServices.getCurrentUser()).thenReturn(memberProfile);

        BadArgException exception = assertThrows(BadArgException.class, () -> {
            pulseResponseService.save(pulseResponse);
        });

        assertEquals(String.format("Found unexpected id for pulseresponse %s", pulseResponse.getId()), exception.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSaveWithNonExistentMember() {
        UUID currentUserId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate pulseSubDate = LocalDate.now();

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        MemberProfile memberProfile = new MemberProfile(currentUserId, "John", null, "Doe",
                null, null, null, null, "john@oci.com",
                null, null, null, null,
                null, null, null, null, null);
        when(currentUserServices.getCurrentUser()).thenReturn(memberProfile);
        when(memberRepo.findById(memberId)).thenReturn(Optional.empty()); // Member doesn't exist

        BadArgException exception = assertThrows(BadArgException.class, () -> {
            pulseResponseService.save(pulseResponse);
        });

        assertEquals(String.format("Member %s doesn't exists", memberId), exception.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSaveWithInvalidDate() {
        UUID currentUserId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate pulseSubDate = LocalDate.of(0000,1,1);

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        MemberProfile memberProfile = new MemberProfile(currentUserId, "John", null, "Doe",
                null, null, null, null, "john@oci.com",
                null, null, null, null,
                null, null, null, null, null);

        MemberProfile memberProfile2 = new MemberProfile(memberId, "Jane", null, "Doe",
                null, null, null, null, "jane@oci.com",
                null, null, null, currentUserId,
                null, null, null, null, null);
        when(currentUserServices.getCurrentUser()).thenReturn(memberProfile);
        when(memberRepo.findById(memberId)).thenReturn(Optional.of(memberProfile2));
        when(memberProfileServices.getSubordinatesForId(currentUserId)).thenReturn(Collections.singletonList(memberProfile2));

        BadArgException exception = assertThrows(BadArgException.class, () -> {
            pulseResponseService.save(pulseResponse);
        });

        assertEquals(String.format("Invalid date for pulseresponse submission date %s", memberId), exception.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSaveWithoutPermission() {
        UUID currentUserId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate pulseSubDate = LocalDate.now();

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        MemberProfile memberProfile = new MemberProfile(currentUserId, "John", null, "Doe",
                null, null, null, null, "john@oci.com",
                null, null, null, null,
                null, null, null, null, null);

        MemberProfile memberProfile2 = new MemberProfile(memberId, "Jane", null, "Doe",
                null, null, null, null, "jane@oci.com",
                null, null, null, currentUserId,
                null, null, null, null, null);
        when(currentUserServices.getCurrentUser()).thenReturn(memberProfile);
        when(memberRepo.findById(memberId)).thenReturn(Optional.of(memberProfile2));
        when(memberProfileServices.getSubordinatesForId(currentUserId)).thenReturn(Collections.emptyList()); // No subordinates

        BadArgException exception = assertThrows(BadArgException.class, () -> {
            pulseResponseService.save(pulseResponse);
        });

        assertEquals(String.format("User %s does not have permission to create pulse response for user %s", currentUserId, memberId), exception.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_NullPulseResponse() {
        pulseResponseService.sendPulseLowScoreEmail(null);
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_NoLowScores() {
        PulseResponse pulseResponse = new PulseResponse(3, 4, LocalDate.now(), UUID.randomUUID(), "Good", "Great");
        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);
        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_LowInternalScore() {
        UUID teamMemberId = UUID.randomUUID();
        UUID pdlId = UUID.randomUUID();

        PulseResponse pulseResponse = new PulseResponse(1, 3, LocalDate.now(), teamMemberId, "Sad", "Neutral");

        MemberProfile surveyTakerProfile = mock(MemberProfile.class);
        MemberProfile pdlProfile = mock(MemberProfile.class);

        when(memberRepo.existsById(teamMemberId)).thenReturn(true);
        when(memberProfileServices.getById(teamMemberId)).thenReturn(surveyTakerProfile);
        when(surveyTakerProfile.getFirstName()).thenReturn("John");
        when(surveyTakerProfile.getLastName()).thenReturn("Doe");

        when(surveyTakerProfile.getPdlId()).thenReturn(pdlId);
        when(memberRepo.existsById(pdlId)).thenReturn(true);
        when(memberProfileServices.getById(pdlId)).thenReturn(pdlProfile);
        when(pdlProfile.getWorkEmail()).thenReturn("pdl@example.com");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "Internal pulse scores are low for team member John Doe", "Team member John Doe has left low internal pulse scores. Please consider reaching out to this employee at null<br>Internal Feelings: Sad<br>", pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_LowExternalScore() {
        UUID teamMemberId = UUID.randomUUID();
        UUID pdlId = UUID.randomUUID();

        PulseResponse pulseResponse = new PulseResponse(3, 1, LocalDate.now(), teamMemberId, "Neutral", "Sad");

        MemberProfile surveyTakerProfile = mock(MemberProfile.class);
        MemberProfile pdlProfile = mock(MemberProfile.class);

        when(memberRepo.existsById(teamMemberId)).thenReturn(true);
        when(memberProfileServices.getById(teamMemberId)).thenReturn(surveyTakerProfile);
        when(surveyTakerProfile.getFirstName()).thenReturn("John");
        when(surveyTakerProfile.getLastName()).thenReturn("Doe");

        when(surveyTakerProfile.getPdlId()).thenReturn(pdlId);
        when(memberRepo.existsById(pdlId)).thenReturn(true);
        when(memberProfileServices.getById(pdlId)).thenReturn(pdlProfile);
        when(pdlProfile.getWorkEmail()).thenReturn("pdl@example.com");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "External pulse scores are low for team member John Doe", "Team member John Doe has left low external pulse scores. Please consider reaching out to this employee at null<br>External Feelings: Sad<br>", pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_LowInternalAndExternalScore() {
        UUID teamMemberId = UUID.randomUUID();
        UUID pdlId = UUID.randomUUID();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        MemberProfile surveyTakerProfile = mock(MemberProfile.class);
        MemberProfile pdlProfile = mock(MemberProfile.class);

        when(memberRepo.existsById(teamMemberId)).thenReturn(true);
        when(memberProfileServices.getById(teamMemberId)).thenReturn(surveyTakerProfile);
        when(surveyTakerProfile.getFirstName()).thenReturn("John");
        when(surveyTakerProfile.getLastName()).thenReturn("Doe");

        when(surveyTakerProfile.getPdlId()).thenReturn(pdlId);
        when(memberRepo.existsById(pdlId)).thenReturn(true);
        when(memberProfileServices.getById(pdlId)).thenReturn(pdlProfile);
        when(pdlProfile.getWorkEmail()).thenReturn("pdl@example.com");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "Internal and external pulse scores are low for team member John Doe", "Team member John Doe has left low internal and external pulse scores. Please consider reaching out to this employee at null<br>Internal Feelings: Very Sad<br>External Feelings: Very Sad<br>", pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_NoTeamMemberId() {
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), null, "Very Sad", "Very Sad");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_InvalidTeamMemberId() {
        UUID teamMemberId = UUID.randomUUID();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        when(memberRepo.existsById(teamMemberId)).thenReturn(false);

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_WithPdlId() {
        UUID teamMemberId = UUID.randomUUID();
        UUID pdlId = UUID.randomUUID();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        MemberProfile surveyTakerProfile = mock(MemberProfile.class);
        MemberProfile pdlProfile = mock(MemberProfile.class);

        when(memberRepo.existsById(teamMemberId)).thenReturn(true);
        when(memberProfileServices.getById(teamMemberId)).thenReturn(surveyTakerProfile);
        when(surveyTakerProfile.getFirstName()).thenReturn("John");
        when(surveyTakerProfile.getLastName()).thenReturn("Doe");

        when(surveyTakerProfile.getPdlId()).thenReturn(pdlId);
        when(memberRepo.existsById(pdlId)).thenReturn(true);
        when(memberProfileServices.getById(pdlId)).thenReturn(pdlProfile);
        when(pdlProfile.getWorkEmail()).thenReturn("pdl@example.com");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "Internal and external pulse scores are low for team member John Doe", "Team member John Doe has left low internal and external pulse scores. Please consider reaching out to this employee at null<br>Internal Feelings: Very Sad<br>External Feelings: Very Sad<br>", pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_WithSupervisorId() {
        UUID teamMemberId = UUID.randomUUID();
        UUID supervisorId = UUID.randomUUID();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        MemberProfile surveyTakerProfile = mock(MemberProfile.class);
        MemberProfile supervisorProfile = mock(MemberProfile.class);

        when(memberRepo.existsById(teamMemberId)).thenReturn(true);
        when(memberProfileServices.getById(teamMemberId)).thenReturn(surveyTakerProfile);
        when(surveyTakerProfile.getFirstName()).thenReturn("John");
        when(surveyTakerProfile.getLastName()).thenReturn("Doe");

        when(surveyTakerProfile.getSupervisorid()).thenReturn(supervisorId);
        when(memberRepo.existsById(supervisorId)).thenReturn(true);
        when(memberProfileServices.getById(supervisorId)).thenReturn(supervisorProfile);
        when(supervisorProfile.getWorkEmail()).thenReturn("supervisor@example.com");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);


        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null", "Internal and external pulse scores are low for team member John Doe", "Team member John Doe has left low internal and external pulse scores. Please consider reaching out to this employee at null<br>Internal Feelings: Very Sad<br>External Feelings: Very Sad<br>", supervisorProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }
}

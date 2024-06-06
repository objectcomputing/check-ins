package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PulseResponseTest extends TestContainersSuite {
    @Inject
    protected Validator validator;
    private MemberProfileServices memberProfileServices;
    private MemberProfileRepository memberRepo;
    private EmailSender emailSender;

    private PulseResponseServicesImpl pulseResponseService;

    @BeforeEach
    @Tag("mocked")
    void setUp() {
        PulseResponseRepository pulseResponseRepo = Mockito.mock(PulseResponseRepository.class);
        memberProfileServices = Mockito.mock(MemberProfileServices.class);
        memberRepo = Mockito.mock(MemberProfileRepository.class);
        CurrentUserServices currentUserServices = Mockito.mock(CurrentUserServices.class);
        RolePermissionServices rolePermissionServices = Mockito.mock(RolePermissionServices.class);
        emailSender = Mockito.mock(EmailSender.class);

        pulseResponseService = new PulseResponseServicesImpl(pulseResponseRepo,
                memberProfileServices,
                memberRepo,
                currentUserServices,
                rolePermissionServices,
                emailSender);
    }

    @AfterEach
    @Tag("mocked")
    void tearDown() {
        Mockito.reset(memberRepo, memberProfileServices, emailSender);
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
    void testSendPulseLowScoreEmail_NullPulseResponse() {
        pulseResponseService.sendPulseLowScoreEmail(null);
        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_NoLowScores() {
        PulseResponse pulseResponse = new PulseResponse(3, 4, LocalDate.now(), UUID.randomUUID(), "Good", "Great");
        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);
        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
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

        verify(emailSender, times(1)).sendEmail(any(), any(), eq("Internal pulse scores are low for team member John Doe"), any(), eq("pdl@example.com"));
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

        verify(emailSender, times(1)).sendEmail(any(), any(), eq("External pulse scores are low for team member John Doe"), any(), eq("pdl@example.com"));
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

        verify(emailSender, times(1)).sendEmail(any(), any(), eq("Internal and external pulse scores are low for team member John Doe"), any(), eq("pdl@example.com"));
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_NoTeamMemberId() {
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), null, "Very Sad", "Very Sad");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("mocked")
    void testSendPulseLowScoreEmail_InvalidTeamMemberId() {
        UUID teamMemberId = UUID.randomUUID();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        when(memberRepo.existsById(teamMemberId)).thenReturn(false);

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        verify(emailSender, never()).sendEmail(any(), any(), any(), any(), any());
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

        verify(emailSender, times(1)).sendEmail(
                any(), any(), eq("Internal and external pulse scores are low for team member John Doe"),
                contains("Team member John Doe has left low internal and external pulse scores."),
                eq(new String[]{"pdl@example.com"})
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

        verify(emailSender, times(1)).sendEmail(
                any(), any(), eq("Internal and external pulse scores are low for team member John Doe"),
                contains("Team member John Doe has left low internal and external pulse scores."),
                eq(new String[]{"supervisor@example.com"})
        );
    }
}

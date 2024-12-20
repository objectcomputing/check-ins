package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;
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
import org.junit.jupiter.api.condition.DisabledInNativeImage;

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

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
class PulseResponseTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    protected Validator validator;

    @Inject
    @Named(MailJetFactory.HTML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    CurrentUserServicesReplacement currentUserServices;

    @Inject
    private PulseResponseServicesImpl pulseResponseService;

    private MemberProfile regular;
    private MemberProfile another;
    private MemberProfile pdlProfile;
    private MemberProfile supervisorProfile;

    @BeforeEach
    void setUp() {
        createAndAssignRoles();

        pdlProfile = createADefaultMemberProfile();
        regular = createADefaultMemberProfileForPdl(pdlProfile);
        supervisorProfile = createADefaultSupervisor();
        another = createAProfileWithSupervisorAndPDL(supervisorProfile,
                                                     pdlProfile);
        currentUserServices.currentUser = regular;
        emailSender.reset();
    }

    @Test
    void testPulseResponseInstantiation() {
        LocalDate submissionDate = LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = regular.getId();
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
        final UUID teamMemberId = regular.getId();
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
        final UUID teamMemberId = regular.getId();
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
        final UUID teamMemberId = regular.getId();
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
    void testSaveWithValidPulseResponse() {
        UUID memberId = regular.getId();
        LocalDate pulseSubDate = LocalDate.now();

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        PulseResponse savedResponse = new PulseResponse();
        PulseResponse result = pulseResponseService.save(pulseResponse);

        assertTrue(result.getId() != null);
        assertEquals(memberId, result.getTeamMemberId());
        assertEquals(LocalDate.now(), result.getSubmissionDate());
    }

    @Test
    void testSaveWithNonNullId() {
        UUID memberId = regular.getId();
        LocalDate pulseSubDate = LocalDate.now();

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setId(UUID.randomUUID()); // Non-null ID
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        BadArgException exception = assertThrows(BadArgException.class, () -> {
            pulseResponseService.save(pulseResponse);
        });

        assertEquals(String.format("Found unexpected id for pulseresponse %s", pulseResponse.getId()), exception.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSaveWithNonExistentMember() {
        UUID memberId = UUID.randomUUID();
        LocalDate pulseSubDate = LocalDate.now();

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        BadArgException exception = assertThrows(BadArgException.class, () -> {
            pulseResponseService.save(pulseResponse);
        });

        assertEquals(String.format("Member %s doesn't exists", memberId), exception.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSaveWithInvalidDate() {
        UUID memberId = regular.getId();
        LocalDate pulseSubDate = LocalDate.of(0000,1,1);

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        BadArgException exception = assertThrows(BadArgException.class, () -> {
            pulseResponseService.save(pulseResponse);
        });

        assertEquals(String.format("Invalid date for pulseresponse submission date %s", memberId), exception.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSaveWithoutPermission() {
        UUID currentUserId = regular.getId();
        UUID memberId = another.getId();
        LocalDate pulseSubDate = LocalDate.now();

        PulseResponse pulseResponse = new PulseResponse();
        pulseResponse.setTeamMemberId(memberId);
        pulseResponse.setSubmissionDate(pulseSubDate);

        BadArgException exception = assertThrows(BadArgException.class, () -> {
            pulseResponseService.save(pulseResponse);
        });

        assertEquals(String.format("User %s does not have permission to create pulse response for user %s", currentUserId, memberId), exception.getMessage());
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSendPulseLowScoreEmail_NullPulseResponse() {
        pulseResponseService.sendPulseLowScoreEmail(null);
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSendPulseLowScoreEmail_NoLowScores() {
        PulseResponse pulseResponse = new PulseResponse(3, 4, LocalDate.now(), regular.getId(), "Good", "Great");
        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSendPulseLowScoreEmail_LowInternalScore() {
        UUID teamMemberId = regular.getId();
        UUID pdlId = pdlProfile.getId();

        PulseResponse pulseResponse = new PulseResponse(1, 3, LocalDate.now(), teamMemberId, "Sad", "Neutral");


        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("Internal pulse scores are low for team member %s %s", regular.getFirstName(), regular.getLastName()),
                        String.format("Team member %s %s has left low internal pulse scores. Please consider reaching out to this employee at %s<br>Internal Feelings: Sad<br>", regular.getFirstName(), regular.getLastName(), regular.getWorkEmail()),
                        pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testSendPulseLowScoreEmail_LowExternalScore() {
        UUID teamMemberId = regular.getId();
        UUID pdlId = pdlProfile.getId();

        PulseResponse pulseResponse = new PulseResponse(3, 1, LocalDate.now(), teamMemberId, "Neutral", "Sad");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("External pulse scores are low for team member %s %s", regular.getFirstName(), regular.getLastName()),
                        String.format("Team member %s %s has left low external pulse scores. Please consider reaching out to this employee at %s<br>External Feelings: Sad<br>", regular.getFirstName(), regular.getLastName(), regular.getWorkEmail()),
                        pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testSendPulseLowScoreEmail_LowInternalAndExternalScore() {
        UUID teamMemberId = regular.getId();
        UUID pdlId = pdlProfile.getId();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("Internal and external pulse scores are low for team member %s %s", regular.getFirstName(), regular.getLastName()),
                        String.format("Team member %s %s has left low internal and external pulse scores. Please consider reaching out to this employee at %s<br>Internal Feelings: Very Sad<br>External Feelings: Very Sad<br>", regular.getFirstName(), regular.getLastName(), regular.getWorkEmail()),
                        pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testSendPulseLowScoreEmail_NoTeamMemberId() {
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), null, "Very Sad", "Very Sad");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSendPulseLowScoreEmail_InvalidTeamMemberId() {
        UUID teamMemberId = UUID.randomUUID();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testSendPulseLowScoreEmail_WithPdlId() {
        UUID teamMemberId = regular.getId();
        UUID pdlId = pdlProfile.getId();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);

        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("Internal and external pulse scores are low for team member %s %s", regular.getFirstName(), regular.getLastName()),
                        String.format("Team member %s %s has left low internal and external pulse scores. Please consider reaching out to this employee at %s<br>Internal Feelings: Very Sad<br>External Feelings: Very Sad<br>", regular.getFirstName(), regular.getLastName(), regular.getWorkEmail()),
                        pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }

    @Test
    void testSendPulseLowScoreEmail_WithSupervisorId() {
        UUID teamMemberId = another.getId();
        UUID supervisorId = supervisorProfile.getId();
        PulseResponse pulseResponse = new PulseResponse(1, 1, LocalDate.now(), teamMemberId, "Very Sad", "Very Sad");

        pulseResponseService.sendPulseLowScoreEmail(pulseResponse);


        assertEquals(1, emailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL", "null", "null",
                        String.format("Internal and external pulse scores are low for team member %s %s", another.getFirstName(), another.getLastName()),
                        String.format("Team member %s %s has left low internal and external pulse scores. Please consider reaching out to this employee at %s<br>Internal Feelings: Very Sad<br>External Feelings: Very Sad<br>", another.getFirstName(), another.getLastName(), another.getWorkEmail()),
                        supervisorProfile.getWorkEmail() +
                        "," + pdlProfile.getWorkEmail()),
                emailSender.events.getFirst()
        );
    }
}

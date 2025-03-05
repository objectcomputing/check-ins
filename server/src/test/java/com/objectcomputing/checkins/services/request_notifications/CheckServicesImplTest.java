package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.pulse.PulseServices;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;
import com.objectcomputing.checkins.services.SlackReaderReplacement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
@Property(name = "replace.slackreader", value = StringUtils.TRUE)
class CheckServicesImplTest extends TestContainersSuite
                            implements FeedbackTemplateFixture, FeedbackRequestFixture, MemberProfileFixture, RoleFixture {
    @Inject
    CurrentUserServicesReplacement currentUserServices;

    @Inject
    private SlackReaderReplacement slackReader;

    @Inject
    @Named(MailJetFactory.MJML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    private CheckServicesImpl checkServices;

    @BeforeEach
    void resetTest() {
        currentUserServices.currentUser = createAThirdDefaultMemberProfile();
        createAndAssignAdminRole(currentUserServices.currentUser);
        emailSender.reset();
    }

    @Test
    void sendScheduledEmails() {
        // Create a pending feedback request.
        MemberProfile pdlMemberProfile = createADefaultMemberProfile();
        MemberProfile employeeMemberProfile = createADefaultMemberProfileForPdl(pdlMemberProfile);
        MemberProfile recipient = createADefaultRecipient();

        FeedbackTemplate template = createFeedbackTemplate(pdlMemberProfile.getId());
        getFeedbackTemplateRepository().save(template);
        FeedbackRequest retrievedRequest =
            saveSampleFeedbackRequest(pdlMemberProfile,
                                      employeeMemberProfile,
                                      recipient, template.getId());

        // Send emails for today
        checkServices.sendScheduledEmails();

        // One for the feedback request and, possibly, one for the pulse email.
        assertTrue(emailSender.events.size() > 0);
        assertEquals(pdlMemberProfile.getWorkEmail(),
                     emailSender.events.get(0).get(2));
        assertEquals("Feedback request",
                     emailSender.events.get(0).get(3));
    }
}

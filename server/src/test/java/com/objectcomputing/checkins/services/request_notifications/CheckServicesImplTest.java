package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.pulse.PulseServices;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
class CheckServicesImplTest extends TestContainersSuite
                            implements FeedbackTemplateFixture, FeedbackRequestFixture, MemberProfileFixture {

    @Inject
    @Named(MailJetFactory.MJML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    private CheckServicesImpl checkServices;

    @BeforeEach
    void resetTest() {
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

        // One for the feedback request and one for the pulse email.
        assertEquals(2, emailSender.events.size());
        assertEquals(pdlMemberProfile.getWorkEmail(),
                     emailSender.events.get(0).get(2));
        assertEquals("Feedback request",
                     emailSender.events.get(0).get(3));
        System.out.println(emailSender.events.get(0));
    }
}

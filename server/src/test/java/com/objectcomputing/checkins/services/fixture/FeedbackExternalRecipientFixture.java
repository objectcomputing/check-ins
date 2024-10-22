package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_external_recipient.FeedbackExternalRecipient;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDate;
import java.util.Map;

public interface FeedbackExternalRecipientFixture extends RepositoryFixture {

    default FeedbackExternalRecipient createADefaultFeedbackExternalRecipient() {
        String email = "externalRecipient01@example.com";
        String firstName = "External-01";
        String lastName = "Recipient";
        String companyName = "Company Name";

        return getFeedbackExternalRecipientRepository().save(new FeedbackExternalRecipient(
            email, firstName, lastName, companyName
        ));
    }

    default FeedbackExternalRecipient createASecondDefaultFeedbackExternalRecipient() {
        String email = "externalRecipient02@example.com";
        String firstName = "External-02";
        String lastName = "Recipient";
        String companyName = "Company Name";

        return getFeedbackExternalRecipientRepository().save(new FeedbackExternalRecipient(
                email, firstName, lastName, companyName
        ));
    }

    default FeedbackExternalRecipient createAThirdDefaultFeedbackExternalRecipient() {
        String email = "externalRecipient03@example.com";
        String firstName = "External-03";
        String lastName = "Recipient";
        String companyName = "Company Name";

        return getFeedbackExternalRecipientRepository().save(new FeedbackExternalRecipient(
                email, firstName, lastName, companyName
        ));
    }

}

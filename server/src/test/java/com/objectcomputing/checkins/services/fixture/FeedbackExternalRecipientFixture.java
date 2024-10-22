package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_external_recipient.FeedbackExternalRecipient;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDate;
import java.util.Map;

public interface FeedbackExternalRecipientFixture extends RepositoryFixture {

    default FeedbackExternalRecipient createADefaultFeedbackExternalRecipient() {
        String email = "externalRecipient@example.com";
        String firstName = "External-01";
        String lastName = "Recipient";
        String companyName = "Company Name";

        return getFeedbackExternalRecipientRepository().save(new FeedbackExternalRecipient(
            email, firstName, lastName, companyName
        ));
    }

}

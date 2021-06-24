package com.objectcomputing.checkins.services.fixture;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import java.time.LocalDate;

import java.util.UUID;

public interface FeedbackRequestFixture extends RepositoryFixture {

    /**
     * Creates a sample feedback request
     * @param creator The {@link MemberProfile} of the creator of the feedback request
     * @param recipient The {@link MemberProfile} of the member giving feedback
     * @param requestee The {@link MemberProfile} of the requestee of the feedback request
     * @return The saved {@link FeedbackRequest}
     */
    default FeedbackRequest createFeedbackRequest(MemberProfile creator, MemberProfile recipient, MemberProfile requestee) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return getFeedbackRequestRepository().save(new FeedbackRequest(UUID.randomUUID(), creator.getId(), recipient.getId(), requestee.getId(), UUID.randomUUID(), testDate, null, "pending", null, null));
    }

}

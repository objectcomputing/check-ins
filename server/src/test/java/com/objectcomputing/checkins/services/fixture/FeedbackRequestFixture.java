package com.objectcomputing.checkins.services.fixture;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import java.time.LocalDate;

import java.util.UUID;

public interface FeedbackRequestFixture extends RepositoryFixture {

    /**
     * Creates a sample feedback request
     * @param creator The {@link MemberProfile} of the creator of the feedback request
     * @param requestee The {@link MemberProfile} of the requestee of the feedback request
     * @return The saved {@link FeedbackRequest}
     */
    default FeedbackRequest createFeedbackRequest(MemberProfile creator, MemberProfile requestee) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return getFeedbackRequestRepository().save(new FeedbackRequest(UUID.randomUUID(), requestee.getId(), creator.getId(), UUID.randomUUID(), testDate, null, "pending"));
    }

}

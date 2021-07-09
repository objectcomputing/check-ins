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
    default FeedbackRequest createFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return getFeedbackRequestRepository().save(new FeedbackRequest(UUID.randomUUID(), creator.getId(), requestee.getId(), recipient.getId(), UUID.randomUUID(), testDate, null, "pending", null));
    }

    default MemberProfile createADefaultRecipient() {
        return getMemberProfileRepository().save(new MemberProfile("Ron", null, "Swanson",
                null, "Parks Director", null, "Pawnee, Indiana",
                "ron@objectcomputing.com", "mr-ron-swanson",
                LocalDate.now(), "enjoys woodworking, breakfast meats, and saxophone jazz",
                null, null,null));
    }

    default MemberProfile createASecondDefaultRecipient() {
        return getMemberProfileRepository().save(new MemberProfile("Leslie", null, "Knope",
                null, "Parks Deputy Director", null, "Pawnee, Indiana",
                "leslie@objectcomputing.com", "ms-leslie-knope",
                LocalDate.now(), "proud member of numerous action committees",
                null, null, null));
    }

}

package com.objectcomputing.checkins.services.fixture;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;

import java.time.LocalDate;

import java.util.UUID;

public interface FeedbackRequestFixture extends RepositoryFixture {

    /**
     * Creates a sample feedback request
     * @param creator The {@link MemberProfile} of the creator of the feedback request
     * @param requestee The {@link MemberProfile} of the requestee of the feedback request
     * @param recipient The {@link MemberProfile} of the member giving feedback
     * @param templateId The UUID of the FeedbackTemplate
     * @return The created {@link FeedbackRequest}
     */
    default FeedbackRequest createSampleFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, "pending", null, null);
    }

    /**
     * Creates a sample feedback request
     * @param creator The {@link MemberProfile} of the creator of the feedback request
     * @param requestee The {@link MemberProfile} of the requestee of the feedback request
     * @param recipient The {@link MemberProfile} of the member giving feedback
     * @param templateId The UUID of the FeedbackTemplate
     * @param reviewPeriod the {@link ReviewPeriod} that this feedback request is associated with
     * @return The created {@link FeedbackRequest}
     */
    default FeedbackRequest createSampleFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId, ReviewPeriod reviewPeriod) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, "pending", null, reviewPeriod.getId());
    }

    /**
     * Saves a sample feedback request
     * @param creator The {@link MemberProfile} of the creator of the feedback request
     * @param recipient The {@link MemberProfile} of the member giving feedback
     * @param requestee The {@link MemberProfile} of the requestee of the feedback request
     * @param templateId The UUID of the FeedbackTemplate
     * @return The saved {@link FeedbackRequest}
     */
    default FeedbackRequest saveSampleFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return getFeedbackRequestRepository().save(new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, "pending", null, null));
    }

    /**
     * Saves a sample feedback request
     * @param creator The {@link MemberProfile} of the creator of the feedback request
     * @param recipient The {@link MemberProfile} of the member giving feedback
     * @param requestee The {@link MemberProfile} of the requestee of the feedback request
     * @param templateId The UUID of the FeedbackTemplate
     * @return The saved {@link FeedbackRequest}
     */
    default FeedbackRequest saveSampleFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId, ReviewPeriod reviewPeriod) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return getFeedbackRequestRepository().save(new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, "pending", null, reviewPeriod.getId()));
    }

    default FeedbackRequest saveSampleFeedbackRequestWithStatus(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId, String status) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return getFeedbackRequestRepository().save(new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, status, null, null));
    }

    default MemberProfile createADefaultRecipient() {
        return getMemberProfileRepository().save(new MemberProfile("Ron", null, "Swanson",
                null, "Parks Director", null, "Pawnee, Indiana",
                "ron@objectcomputing.com", "mr-ron-swanson",
                LocalDate.now(), "enjoys woodworking, breakfast meats, and saxophone jazz",
                null, null, null, false, false));
    }

    default MemberProfile createASecondDefaultRecipient() {
        return getMemberProfileRepository().save(new MemberProfile("Leslie", null, "Knope",
                null, "Parks Deputy Director", null, "Pawnee, Indiana",
                "leslie@objectcomputing.com", "ms-leslie-knope",
                LocalDate.now(), "proud member of numerous action committees",
                null, null, null, false, false));
    }

}

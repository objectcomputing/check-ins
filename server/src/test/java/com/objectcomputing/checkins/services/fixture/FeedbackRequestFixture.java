package com.objectcomputing.checkins.services.fixture;
import com.objectcomputing.checkins.services.feedback_external_recipient.FeedbackExternalRecipient;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;

import java.time.LocalDate;

import java.util.UUID;
import java.util.List;

public interface FeedbackRequestFixture extends RepositoryFixture, FeedbackTemplateFixture {

    /**
     * Creates a sample feedback request
     * @param creator The {@link MemberProfile} of the creator of the feedback request
     * @param requestee The {@link MemberProfile} of the requestee of the feedback request
     * @param recipient The {@link MemberProfile} of the member giving feedback
     * @param templateId The UUID of the FeedbackTemplate
     * @return The created {@link FeedbackRequest}
     */
    default FeedbackRequest createSampleFeedbackRequestWithRecipient(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, "pending", null, null, null);
    }

    /**
     * Creates a sample feedback request
     * @param creator The {@link MemberProfile} of the creator of the feedback request
     * @param requestee The {@link MemberProfile} of the requestee of the feedback request
     * @param externalRecipient The {@link MemberProfile} of the member giving feedback
     * @param templateId The UUID of the FeedbackTemplate
     * @return The created {@link FeedbackRequest}
     */
    default FeedbackRequest createSampleFeedbackRequestWithExternalRecipient(MemberProfile creator, MemberProfile requestee, FeedbackExternalRecipient externalRecipient, UUID templateId) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return new FeedbackRequest(creator.getId(), requestee.getId(), null, templateId, testDate, null, "pending", null, null, externalRecipient.getId());
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
    default FeedbackRequest createSampleFeedbackRequestWithRecipient(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId, ReviewPeriod reviewPeriod) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, "pending", null, reviewPeriod.getId(), null); /** TODO Luch reciplientg **/
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
        return getFeedbackRequestRepository().save(new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, "pending", null, null, null)); /** TODO Luch recipient **/
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
        return getFeedbackRequestRepository().save(new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, "pending", null, reviewPeriod.getId(), null)); /** TODO Luch recipient **/
    }

    default FeedbackRequest saveSampleFeedbackRequestWithStatus(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId, String status) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return getFeedbackRequestRepository().save(new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, testDate, null, status, null, null, null)); /** TODO Luch recipient **/
    }

    default MemberProfile createADefaultRecipient() {
        return getMemberProfileRepository().save(new MemberProfile("Ron", null, "Swanson",
                null, "Parks Director", null, "Pawnee, Indiana",
                "ron@objectcomputing.com", "mr-ron-swanson",
                LocalDate.now(), "enjoys woodworking, breakfast meats, and saxophone jazz",
                null, null, null, false, false, null));
    }

    default MemberProfile createASecondDefaultRecipient() {
        return getMemberProfileRepository().save(new MemberProfile("Leslie", null, "Knope",
                null, "Parks Deputy Director", null, "Pawnee, Indiana",
                "leslie@objectcomputing.com", "ms-leslie-knope",
                LocalDate.now(), "proud member of numerous action committees",
                null, null, null, false, false, null));
    }

    default FeedbackRequest createFeedbackRequestWithRecipient(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        FeedbackTemplate template = createFeedbackTemplate(creator.getId());
        getFeedbackTemplateRepository().save(template);
        return createSampleFeedbackRequestWithRecipient(creator, requestee, recipient, template.getId());
    }

    default FeedbackRequest createFeedbackRequestWithExternalRecipient(MemberProfile creator, MemberProfile requestee, FeedbackExternalRecipient externalRecipient) {
        FeedbackTemplate template = createFeedbackTemplate(creator.getId());
        getFeedbackTemplateRepository().save(template);
        return createSampleFeedbackRequestWithExternalRecipient(creator, requestee, externalRecipient, template.getId());
    }

    default FeedbackRequest createFeedbackRequestWithRecipient(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, ReviewPeriod reviewPeriod) {
        FeedbackTemplate template = createAnotherFeedbackTemplate(creator.getId());
        getFeedbackTemplateRepository().save(template);
        return createSampleFeedbackRequestWithRecipient(creator, requestee, recipient, template.getId(), reviewPeriod);
    }

    default FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        final FeedbackRequest feedbackRequest = createFeedbackRequestWithRecipient(creator, requestee, recipient);
        return saveSampleFeedbackRequest(creator, requestee, recipient, feedbackRequest.getTemplateId());
    }

    default FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, ReviewPeriod reviewPeriod) {
        final FeedbackRequest feedbackRequest = createFeedbackRequestWithRecipient(creator, requestee, recipient, reviewPeriod);
        return saveSampleFeedbackRequest(creator, requestee, recipient, feedbackRequest.getTemplateId(), reviewPeriod);
    }

    default FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, LocalDate sendDate) {
        final FeedbackRequest feedbackRequest = createFeedbackRequestWithRecipient(creator, requestee, recipient);
        feedbackRequest.setSendDate(sendDate);
        return getFeedbackRequestRepository().save(feedbackRequest);
    }

    default List<FeedbackRequest> getFeedbackRequests(MemberProfile recipient) {
        return getFeedbackRequestRepository()
                 .findByValues(null, null, recipient.getId().toString(), null, null, null, null); /** TODO Luch recipient **/
    }
}

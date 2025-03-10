package com.objectcomputing.checkins.services.fixture;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import jnr.constants.platform.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public interface FeedbackRequestFixture extends RepositoryFixture, FeedbackTemplateFixture {

    static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestFixture.class);

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

    default LocalDate getRandomLocalDateTime(LocalDateTime start, LocalDateTime end) {
        if(start.isEqual(end)) return end.toLocalDate();

        LocalDate startDate = start.toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(startDate, end.toLocalDate());
        Random random = new Random();
        long randomDays = daysBetween > 0 ? random.nextLong(daysBetween) : 0;

        return startDate.plusDays(randomDays);
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
        return saveSampleFeedbackRequest(creator, requestee, recipient, templateId, reviewPeriod, "pending");
    }

    default FeedbackRequest saveSampleFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, UUID templateId, ReviewPeriod reviewPeriod, String status) {
        LocalDate submitDate = getRandomLocalDateTime(reviewPeriod.getPeriodStartDate(), reviewPeriod.getCloseDate());
        LOG.info("Period start date: {} Generated Submit Date: {}", reviewPeriod.getPeriodStartDate(), submitDate.atStartOfDay());
        if(submitDate.atStartOfDay().isAfter(LocalDateTime.now())) submitDate = LocalDateTime.now().toLocalDate();
        LocalDate sendDate = getRandomLocalDateTime(reviewPeriod.getPeriodStartDate(), submitDate.atStartOfDay());
        return getFeedbackRequestRepository().save(new FeedbackRequest(creator.getId(), requestee.getId(), recipient.getId(), templateId, sendDate, null, status, submitDate, reviewPeriod.getId()));
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
                null, null, null, false, false, null, false));
    }

    default MemberProfile createASecondDefaultRecipient() {
        return getMemberProfileRepository().save(new MemberProfile("Leslie", null, "Knope",
                null, "Parks Deputy Director", null, "Pawnee, Indiana",
                "leslie@objectcomputing.com", "ms-leslie-knope",
                LocalDate.now(), "proud member of numerous action committees",
                null, null, null, false, false, null, false));
    }

    default FeedbackRequest createFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        FeedbackTemplate template = createFeedbackTemplate(creator.getId());
        getFeedbackTemplateRepository().save(template);
        return createSampleFeedbackRequest(creator, requestee, recipient, template.getId());
    }

    default FeedbackRequest createFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, ReviewPeriod reviewPeriod) {
        FeedbackTemplate template = createAnotherFeedbackTemplate(creator.getId());
        getFeedbackTemplateRepository().save(template);
        return createSampleFeedbackRequest(creator, requestee, recipient, template.getId(), reviewPeriod);
    }

    default FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient) {
        final FeedbackRequest feedbackRequest = createFeedbackRequest(creator, requestee, recipient);
        return saveSampleFeedbackRequest(creator, requestee, recipient, feedbackRequest.getTemplateId());
    }

    default FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, ReviewPeriod reviewPeriod) {
        final FeedbackRequest feedbackRequest = createFeedbackRequest(creator, requestee, recipient, reviewPeriod);
        return saveSampleFeedbackRequest(creator, requestee, recipient, feedbackRequest.getTemplateId(), reviewPeriod);
    }

    default FeedbackRequest saveFeedbackRequest(MemberProfile creator, MemberProfile requestee, MemberProfile recipient, LocalDate sendDate) {
        final FeedbackRequest feedbackRequest = createFeedbackRequest(creator, requestee, recipient);
        feedbackRequest.setSendDate(sendDate);
        return getFeedbackRequestRepository().save(feedbackRequest);
    }

    default List<FeedbackRequest> getFeedbackRequests(MemberProfile recipient) {
        return getFeedbackRequestRepository()
                 .findByValues(null, null, recipient.getId().toString(), null, null, null);
    }
}

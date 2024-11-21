package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.reviews.ReviewStatus;

import java.time.temporal.TemporalUnit;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface ReviewPeriodFixture extends RepositoryFixture {

    default ReviewPeriod createADefaultReviewPeriod() {
        LocalDateTime launchDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(2);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(2);
        LocalDateTime startDate = launchDate.minusDays(31);
        LocalDateTime endDate = launchDate.minusDays(1);
        return getReviewPeriodRepository().save(
          new ReviewPeriod("Period of Time", ReviewStatus.OPEN, null, null,
                           launchDate, selfReviewCloseDate, closeDate,
                           startDate, endDate));
    }

    default ReviewPeriod createADefaultReviewPeriod(ReviewStatus reviewStatus) {
        return createADefaultReviewPeriod(reviewStatus, null);
    }

    default ReviewPeriod createADefaultReviewPeriod(ReviewStatus reviewStatus, UUID templateId) {
        LocalDateTime launchDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(2);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(2);
        LocalDateTime startDate = launchDate.minusDays(31);
        LocalDateTime endDate = launchDate.minusDays(1);
        return getReviewPeriodRepository().save(
          new ReviewPeriod("Period of Time", reviewStatus, templateId, null,
                           launchDate, selfReviewCloseDate, closeDate,
                           startDate, endDate));
    }

    default ReviewPeriod createADefaultReviewPeriod(
                             LocalDateTime launchDate,
                             ReviewStatus reviewStatus,
                             UUID templateId, UUID selfReviewTemplateId) {
        launchDate = launchDate.truncatedTo(ChronoUnit.DAYS);

        LocalDateTime selfReviewCloseDate = launchDate.plusDays(4);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(2);
        LocalDateTime startDate = launchDate.minusDays(31);
        LocalDateTime endDate = launchDate.minusDays(1);
        return getReviewPeriodRepository().save(
          new ReviewPeriod("Specific Launch Date", reviewStatus, templateId,
                           selfReviewTemplateId,
                           launchDate, selfReviewCloseDate, closeDate,
                           startDate, endDate));
    }

    default ReviewPeriod createASecondaryReviewPeriod() {
        LocalDateTime launchDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(2);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(2);
        LocalDateTime startDate = launchDate.minusDays(31);
        LocalDateTime endDate = launchDate.minusDays(1);
        return getReviewPeriodRepository().save(
          new ReviewPeriod("Period of Play", ReviewStatus.OPEN, null, null,
                           launchDate, selfReviewCloseDate, closeDate,
                           startDate, endDate));
    }

    default ReviewPeriod createAClosedReviewPeriod() {
        LocalDateTime launchDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startDate = launchDate.minusDays(31);
        LocalDateTime endDate = launchDate.minusDays(1);
        return createAClosedReviewPeriod(startDate, endDate);
    }

    default ReviewPeriod createAClosedReviewPeriod(
                           LocalDateTime periodStart, LocalDateTime periodEnd) {
        LocalDateTime launchDate = periodEnd.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(3);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(7);
        return getReviewPeriodRepository().save(
          new ReviewPeriod(
                 "Period of Closure", ReviewStatus.CLOSED, null, null,
                 launchDate, selfReviewCloseDate, closeDate,
                 periodStart, periodEnd));
    }
}

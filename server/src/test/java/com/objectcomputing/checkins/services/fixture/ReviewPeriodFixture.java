package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.reviews.ReviewStatus;

import java.util.UUID;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface ReviewPeriodFixture extends RepositoryFixture {

    default ReviewPeriod createADefaultReviewPeriod() {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1)
                                                .truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.minusDays(1);
        return getReviewPeriodRepository().save(
          new ReviewPeriod("Period of Time", ReviewStatus.OPEN, null, null,
                           launchDate, selfReviewCloseDate, closeDate,
                           startDate, endDate));
    }

    default ReviewPeriod createADefaultReviewPeriod(ReviewStatus reviewStatus) {
        return createADefaultReviewPeriod(reviewStatus, null);
    }

    default ReviewPeriod createADefaultReviewPeriod(ReviewStatus reviewStatus, UUID templateId) {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1)
                                                .truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.minusDays(1);
        return getReviewPeriodRepository().save(
          new ReviewPeriod("Period of Time", reviewStatus, templateId, null,
                           launchDate, selfReviewCloseDate, closeDate,
                           startDate, endDate));
    }

    default ReviewPeriod createASecondaryReviewPeriod() {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1)
                                                .truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.minusDays(1);
        return getReviewPeriodRepository().save(
          new ReviewPeriod("Period of Play", ReviewStatus.OPEN, null, null,
                           launchDate, selfReviewCloseDate, closeDate,
                           startDate, endDate));
    }

    default ReviewPeriod createAClosedReviewPeriod() {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1)
                                                .truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        LocalDateTime startDate = launchDate.minusDays(30);
        LocalDateTime endDate = closeDate.minusDays(1);
        return createAClosedReviewPeriod(startDate, endDate);
    }

    default ReviewPeriod createAClosedReviewPeriod(
                           LocalDateTime periodStart, LocalDateTime periodEnd) {
        LocalDateTime launchDate = LocalDateTime.now().plusMinutes(1)
                                                .truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime selfReviewCloseDate = launchDate.plusDays(1);
        LocalDateTime closeDate = selfReviewCloseDate.plusDays(1);
        return getReviewPeriodRepository().save(
          new ReviewPeriod(
                 "Period of Closure", ReviewStatus.CLOSED, null, null,
                 launchDate, selfReviewCloseDate, closeDate,
                 periodStart, periodEnd));
    }
}

package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.reviews.ReviewStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface ReviewPeriodFixture extends RepositoryFixture {

    default ReviewPeriod createADefaultReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Time", ReviewStatus.OPEN.name(), null, null,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS)));
    }

    default ReviewPeriod createASecondaryReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Play", ReviewStatus.OPEN.name(), null, null,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS)));
    }

    default ReviewPeriod createAClosedReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Closure", ReviewStatus.CLOSED.name(), null, null,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS)));
    }
}

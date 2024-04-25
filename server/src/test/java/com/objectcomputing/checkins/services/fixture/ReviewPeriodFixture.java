package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.reviews.ReviewPeriod;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface ReviewPeriodFixture extends RepositoryFixture {

    default ReviewPeriod createADefaultReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Time", true, null, null,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS)));
    }

    default ReviewPeriod createASecondaryReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Play", true, null, null,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS)));
    }

    default ReviewPeriod createAClosedReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Closure", false, null, null,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS)));
    }
}

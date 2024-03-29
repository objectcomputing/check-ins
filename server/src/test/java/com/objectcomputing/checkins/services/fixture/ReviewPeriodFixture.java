package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.skills.Skill;

public interface ReviewPeriodFixture extends RepositoryFixture {

    default ReviewPeriod createADefaultReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Time", true, null, null));
    }

    default ReviewPeriod createASecondaryReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Play", true, null, null));
    }

    default ReviewPeriod createAClosedReviewPeriod() {
        return getReviewPeriodRepository().save(new ReviewPeriod("Period of Closure", false, null, null));
    }
}

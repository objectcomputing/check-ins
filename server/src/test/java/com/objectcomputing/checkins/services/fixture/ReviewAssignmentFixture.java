package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.reviews.ReviewAssignment;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.reviews.ReviewStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public interface ReviewAssignmentFixture extends RepositoryFixture {


    default ReviewAssignment createADefaultReviewAssignment() {

        return getReviewAssignmentRepository().save(new ReviewAssignment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false));

    }

}

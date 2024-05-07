package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Nullable;

import java.util.Set;
import java.util.UUID;

public interface ReviewAssignmentServices {
    ReviewAssignment save(ReviewAssignment reviewAssignment);
    ReviewAssignment findById(UUID id);

    ReviewAssignment update(ReviewAssignment reviewAssignment);

    void delete(UUID id);
    Set<ReviewAssignment> findAllByReviewPeriodIdAndReviewerId(UUID reviewPeriodId, @Nullable UUID reviewerId);

}

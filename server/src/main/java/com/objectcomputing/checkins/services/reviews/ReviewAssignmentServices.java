package com.objectcomputing.checkins.services.reviews;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ReviewAssignmentServices {
    ReviewAssignment save(ReviewAssignment reviewAssignment);
    List<ReviewAssignment> saveAll(UUID reviewPeriodId, List<ReviewAssignment> reviewAssignments, Boolean deleteExisting);
    ReviewAssignment findById(UUID id);

    ReviewAssignment update(ReviewAssignment reviewAssignment);

    void delete(UUID id);
    Set<ReviewAssignment> findAllByReviewPeriodIdAndReviewerId(UUID reviewPeriodId, @Nullable UUID reviewerId);

}

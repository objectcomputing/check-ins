package com.objectcomputing.checkins.services.reviews;

import java.util.UUID;

public interface ReviewAssignmentServices {
    ReviewAssignment save(ReviewAssignment reviewAssignment);
    ReviewAssignment findById(UUID id);

}

package com.objectcomputing.checkins.services.reviews;

import java.util.Set;
import java.util.UUID;

public interface ReviewPeriodServices {

    ReviewPeriod save(ReviewPeriod reviewPeriod);

    ReviewPeriod update(ReviewPeriod reviewPeriod);

    ReviewPeriod findById(UUID id);

    Set<ReviewPeriod> findByValue(String name, ReviewStatus reviewStatus);

    void delete(UUID id);
}

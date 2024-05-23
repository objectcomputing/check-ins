package com.objectcomputing.checkins.services.reviews;

@FunctionalInterface
interface ReviewStatusTransitionValidator {

    boolean isValid(ReviewStatus from, ReviewStatus to);
}

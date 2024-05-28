package com.objectcomputing.checkins.services.reviews;

import jakarta.inject.Singleton;

@Singleton
class ReviewStatusTransitionValidatorImpl implements ReviewStatusTransitionValidator {

    @Override
    public boolean isValid(ReviewStatus from, ReviewStatus to) {
        return from == to || switch (from) {
            case PLANNING -> to == ReviewStatus.AWAITING_APPROVAL;
            case AWAITING_APPROVAL, CLOSED -> to == ReviewStatus.OPEN;
            case OPEN -> to == ReviewStatus.CLOSED;
            case UNKNOWN -> to == ReviewStatus.PLANNING;
        };
    }
}

package com.objectcomputing.checkins.services.feedback_request;

public enum FeedbackRequestStatus {
    SENT,
    SUBMITTED,
    CANCELED;

    @Override
    public String toString() {
        return name();
    }
}

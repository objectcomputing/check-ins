package com.objectcomputing.checkins.services.feedback_request;

public enum FeedbackRequestStatus {
    SENT("sent"),
    PENDING("pending"),
    SUBMITTED("submitted"),
    CANCELED("canceled");

    private final String text;

    FeedbackRequestStatus(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

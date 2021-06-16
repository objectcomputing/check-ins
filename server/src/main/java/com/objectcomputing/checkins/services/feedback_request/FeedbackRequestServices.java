package com.objectcomputing.checkins.services.feedback_request;

import java.util.Set;
import java.util.UUID;

public interface FeedbackRequestServices {
    FeedbackRequest save(FeedbackRequest feedbackRequest);

    FeedbackRequest update(FeedbackRequest feedbackRequest);

    Boolean delete(UUID id);

    FeedbackRequest getById(UUID id);

    Set<FeedbackRequest> findByValue(UUID creatorId);
}

package com.objectcomputing.checkins.services.feedback_request;

import java.util.List;
import java.util.UUID;

public interface FeedbackRequestServices {
    FeedbackRequest save(FeedbackRequest feedbackRequest);

    FeedbackRequest update(FeedbackRequest feedbackRequest);

    Boolean delete(UUID id);

    FeedbackRequest getById(UUID id);

    List<FeedbackRequest> findByRequesteeId (UUID requesteeId);

    List<FeedbackRequest> findByCreatorId (UUID creatorId);
}

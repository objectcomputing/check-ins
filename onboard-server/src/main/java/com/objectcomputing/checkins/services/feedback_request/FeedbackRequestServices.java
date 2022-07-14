package com.objectcomputing.checkins.services.feedback_request;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public interface FeedbackRequestServices {
    FeedbackRequest save(FeedbackRequest feedbackRequest);

    FeedbackRequest update(FeedbackRequestUpdateDTO feedbackRequestUpdateDTO);

    Boolean delete(UUID id);

    FeedbackRequest getById(UUID id);

    List<FeedbackRequest> findByValues(UUID creatorId, UUID requesteeId, UUID recipientId, LocalDate oldestDate);
}
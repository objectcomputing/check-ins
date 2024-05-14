package com.objectcomputing.checkins.services.feedback_request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedbackRequestServices {
    FeedbackRequest save(FeedbackRequest feedbackRequest);

    FeedbackRequest update(FeedbackRequestUpdateDTO feedbackRequestUpdateDTO);

    void delete(UUID id);

    FeedbackRequest getById(UUID id);

    List<FeedbackRequest> findByValues(UUID creatorId, UUID requesteeId, UUID recipientId, LocalDate oldestDate, UUID reviewPeriodId, UUID templateId, List<UUID> requesteeIds);
}
package com.objectcomputing.checkins.services.feedback_answer;

import java.util.List;
import java.util.UUID;

public interface FeedbackAnswerServices {

    FeedbackAnswer save(FeedbackAnswer feedbackAnswer);

    FeedbackAnswer update(FeedbackAnswer feedbackAnswer);

    FeedbackAnswer getById(UUID id);

    List<FeedbackAnswer> findByValues(UUID questionId, UUID requestId);
}

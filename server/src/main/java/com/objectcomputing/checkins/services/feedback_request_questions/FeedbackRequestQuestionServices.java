package com.objectcomputing.checkins.services.feedback_request_questions;

import java.util.List;
import java.util.UUID;

public interface FeedbackRequestQuestionServices {
    FeedbackRequestQuestion save(FeedbackRequestQuestion feedbackRequestQuestion);

    FeedbackRequestQuestion update(FeedbackRequestQuestion feedbackRequestQuestion);

    Boolean delete(UUID id);

    FeedbackRequestQuestion getById(UUID id);

    List<FeedbackRequestQuestion> findByValues(UUID requestId);

}

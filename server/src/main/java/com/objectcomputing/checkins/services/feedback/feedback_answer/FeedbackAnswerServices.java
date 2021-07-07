package com.objectcomputing.checkins.services.feedback.feedback_answer;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedbackAnswerServices {
    FeedbackAnswer save(FeedbackAnswer feedbackAnswer);

    FeedbackAnswer update(FeedbackRequest feedbackAnswer);

    Boolean delete(UUID id);

    FeedbackAnswer getById(UUID id);

}

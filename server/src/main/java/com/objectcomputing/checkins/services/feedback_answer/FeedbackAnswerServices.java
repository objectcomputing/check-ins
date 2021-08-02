package com.objectcomputing.checkins.services.feedback_answer;

import java.util.UUID;
import java.util.List;
import javax.annotation.Nullable;

public interface FeedbackAnswerServices {

    FeedbackAnswer save(FeedbackAnswer feedbackAnswer);

    FeedbackAnswer update(FeedbackAnswer feedbackAnswer);

    FeedbackAnswer getById(UUID id);

    List<FeedbackAnswer> findByValues(@Nullable UUID questionId, @Nullable UUID requestId);
}

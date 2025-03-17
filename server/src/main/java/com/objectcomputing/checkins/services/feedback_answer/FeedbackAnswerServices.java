package com.objectcomputing.checkins.services.feedback_answer;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface FeedbackAnswerServices {

    FeedbackAnswer save(FeedbackAnswer feedbackAnswer);

    FeedbackAnswer update(FeedbackAnswer feedbackAnswer);

    FeedbackAnswer getById(UUID id);

    List<FeedbackAnswer> findByValues(@Nullable UUID questionId, @Nullable UUID requestId);

    boolean getIsPermitted(FeedbackRequest feedbackRequest);
}

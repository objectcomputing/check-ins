package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_question.FeedbackQuestion;

import java.util.UUID;

public interface FeedbackQuestionFixture extends RepositoryFixture {

    default FeedbackQuestion createDefaultFeedbackQuestion(UUID templateId) {
        return new FeedbackQuestion("How are you doing today?", templateId);
    }

}

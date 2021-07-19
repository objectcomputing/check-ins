package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_request_questions.FeedbackRequestQuestion;

import java.util.UUID;

public interface FeedbackRequestQuestionFixture extends RepositoryFixture {
    default FeedbackRequestQuestion createDefaultFeedbackRequestQuestion(UUID requestId) {
        return getFeedbackRequestQuestionRepository().save(new FeedbackRequestQuestion(
                "How are you?",
                requestId,
                1));
    }

    default FeedbackRequestQuestion createAnotherDefaultFeedbackRequestQuestion(UUID requestId) {
        return getFeedbackRequestQuestionRepository().save(new FeedbackRequestQuestion(
                "Do you like opossums more than other animals?",
                requestId,
                2));
    }


}

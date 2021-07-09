package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_request_questions.FeedbackRequestQuestion;

import java.util.UUID;

public interface FeedbackRequestQuestionFixture extends RepositoryFixture {
    default FeedbackRequestQuestion createDefaultFeedbackRequestQuestion(UUID requestId) {
        return getFeedbackRequestQuestionRepository().save(new FeedbackRequestQuestion(
                requestId,
                "How are you?",
                1));
    }

    default FeedbackRequestQuestion createAnotherDefaultFeedbackRequestQuestion(UUID requestId) {
        return getFeedbackRequestQuestionRepository().save(new FeedbackRequestQuestion(
                requestId,
                "Do you like opossums more than other animals?",
                2));
    }


}

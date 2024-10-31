package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;

import java.util.UUID;

public interface FeedbackAnswerFixture extends RepositoryFixture {

    default FeedbackAnswer createSampleFeedbackAnswer(UUID questionId, UUID requestId) {
        return new FeedbackAnswer("I am doing just fine", questionId, requestId, 0.5);
    }

    default FeedbackAnswer saveSampleFeedbackAnswer(UUID questionId, UUID requestId) {
        return getFeedbackAnswerRepository().save(new FeedbackAnswer("I am doing just fine", questionId, requestId, 0.6));
    }

}

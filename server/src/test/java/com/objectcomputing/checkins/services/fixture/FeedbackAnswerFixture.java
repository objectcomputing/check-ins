package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;

import java.util.UUID;

public interface FeedbackAnswerFixture extends RepositoryFixture {

    default FeedbackAnswer createFeedbackAnswer(UUID questionId) {
        return getFeedbackAnswerRepository().save(new FeedbackAnswer("I am doing just fine", questionId, 0.5));
    }

}

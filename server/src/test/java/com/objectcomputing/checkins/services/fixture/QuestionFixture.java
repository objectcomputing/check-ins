package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.questions.Question;

public interface QuestionFixture extends RepositoryFixture {

    default Question createADefaultQuestion() {
        return getQuestionRepository().save(new Question("How do you feel about Sluggo?"));
    }
}

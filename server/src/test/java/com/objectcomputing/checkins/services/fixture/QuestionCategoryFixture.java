package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.question_category.QuestionCategory;

public interface QuestionCategoryFixture extends RepositoryFixture {
    default QuestionCategory createADefaultQuestionCategory() {
        return getQuestionCategoryRepository().save(new QuestionCategory("Serious"));
    }

    default QuestionCategory createASecondaryQuestionCategory() {
        return getQuestionCategoryRepository().save(new QuestionCategory("Personal"));
    }
}

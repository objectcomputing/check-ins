package com.objectcomputing.checkins.services.questions;

import java.util.Set;
import java.util.UUID;

public interface QuestionServices {
    Question saveQuestion(Question question);
    Set<Question> readAllQuestions();
    Question findById(UUID skillId);
    Question update(Question question);
    Set<Question> findByText(String text);
    Set<Question> findByCategoryId(UUID categoryId);
}

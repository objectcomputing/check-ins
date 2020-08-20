package com.objectcomputing.checkins.services.questions;

import java.util.Set;
import java.util.UUID;

public interface QuestionServices {
    void setQuestionRepository(QuestionRepository questionRepository);
    Question saveQuestion(Question question);
    Set<Question> readAllQuestions();
    Question findByQuestionId(UUID skillId);
    Question update(Question question);
    Set<Question> findByText(String text);
}

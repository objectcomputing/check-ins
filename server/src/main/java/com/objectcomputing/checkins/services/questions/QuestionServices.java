package com.objectcomputing.checkins.services.questions;

import java.util.List;
import java.util.UUID;

public interface QuestionServices {
    void setQuestionRepository(QuestionRepository questionRepository);
    Question saveQuestion(Question question);
    List<Question> readAllQuestions();
    Question findByQuestionId(UUID skillId);
    Question update(Question question);
    List<Question> findByText(String text);
}

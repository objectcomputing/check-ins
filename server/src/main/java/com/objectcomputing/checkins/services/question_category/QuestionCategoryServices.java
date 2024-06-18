package com.objectcomputing.checkins.services.question_category;

import java.util.Set;
import java.util.UUID;

public interface QuestionCategoryServices {
    QuestionCategory saveQuestionCategory(QuestionCategory questionCategory);
    QuestionCategory findById(UUID id);
    QuestionCategory update(QuestionCategory questionCategory);
    QuestionCategory findByName(String name);
    Set<QuestionCategory> findByValue(String name, UUID id);
    boolean delete(UUID id);

}

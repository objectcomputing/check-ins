package com.objectcomputing.checkins.services.questions;

import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends CrudRepository<Question, UUID> {

    Question findByQuestionId(UUID skillid);
    List<Question> findByText(String name);
    List<Question> findAll();

}

package com.objectcomputing.checkins.services.questions;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface QuestionRepository extends CrudRepository<Question, UUID> {

    Set<Question> findByText(String name);
    Set<Question> findByTextIlike(String name);
    Set<Question> findByCategoryId(UUID categoryId);

}

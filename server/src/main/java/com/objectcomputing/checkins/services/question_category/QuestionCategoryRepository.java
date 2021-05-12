package com.objectcomputing.checkins.services.question_category;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface QuestionCategoryRepository extends CrudRepository<QuestionCategory, UUID> {
    Set<QuestionCategory> findAll();
    Optional<QuestionCategory> findById(@NotNull UUID id);
    Optional<QuestionCategory> findByName(String name);
    Set<QuestionCategory> findByNameIlike(String name);


}

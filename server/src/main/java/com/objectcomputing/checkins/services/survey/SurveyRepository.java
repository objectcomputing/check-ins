package com.objectcomputing.checkins.services.survey;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SurveyRepository extends CrudRepository<Survey, UUID> {

    Set<Survey> findByCreatedBy(@NotNull UUID createdBy);
}
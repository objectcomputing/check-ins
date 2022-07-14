package com.objectcomputing.checkins.services.survey;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

import com.objectcomputing.checkins.services.questions.Question;
import com.objectcomputing.checkins.services.survey.Survey;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SurveyRepository extends CrudRepository<Survey, UUID> {

    List<Survey> findByName(@NotBlank String name);
    List<Survey> findByCreatedBy(@NotBlank UUID createdBy);
    Set<Survey> findAll();
}
package com.objectcomputing.checkins.services.opportunities;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

import com.objectcomputing.checkins.services.questions.Question;
import com.objectcomputing.checkins.services.opportunities.Opportunities;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OpportunitiesRepository extends CrudRepository<Opportunities, UUID> {

    List<Opportunities> findByName(@NotBlank String name);
    List<Opportunities> findBySubmittedBy(@NotBlank UUID submittedBy);
    Set<Opportunities> findAll();
}
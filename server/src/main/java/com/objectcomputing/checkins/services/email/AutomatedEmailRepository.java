package com.objectcomputing.checkins.services.email;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AutomatedEmailRepository extends CrudRepository<AutomatedEmail, String> {

    @Override
    <S extends AutomatedEmail> S save(@Valid @NotNull S entity);

}

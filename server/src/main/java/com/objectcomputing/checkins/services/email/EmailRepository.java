package com.objectcomputing.checkins.services.email;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface EmailRepository extends CrudRepository<Email, UUID> {

    @Override
    <S extends Email> S save(@Valid @NotNull S entity);

}

package com.objectcomputing.checkins.services.frozen_template;


import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.reactivex.annotations.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FrozenTemplateRepository extends CrudRepository<FrozenTemplate, UUID> {

    @Override
    <S extends FrozenTemplate> S save(@Valid @NotNull @NonNull S entity);

    @Query("SELECT * from frozen_templates WHERE requestId = :requestId")
    FrozenTemplate findByRequestId(String requestId);
}

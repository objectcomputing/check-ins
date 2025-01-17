package com.objectcomputing.checkins.services.checkins;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CheckInRepository extends CrudRepository<CheckIn,UUID>{

    List<CheckIn> findByTeamMemberId(@NotNull UUID teamMemberId);
    List<CheckIn> findByPdlId(@NotNull UUID pdlId);
    List<CheckIn> findByCompleted(@NotNull Boolean completed);
    List<CheckIn> findAll();

    @Override
    <S extends CheckIn> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends CheckIn> S save(@Valid @NotNull @NonNull S entity);

}

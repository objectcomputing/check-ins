package com.objectcomputing.checkins.services.team;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TeamRepository extends CrudRepository<Team, UUID> {

    Optional<Team> findByName(String name);

    Optional<Team> findById(UUID id);

    List<Team> findByNameIlike(String name);

    @Override
    <S extends Team> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Team> S save(@Valid @NotNull @NonNull S entity);
}

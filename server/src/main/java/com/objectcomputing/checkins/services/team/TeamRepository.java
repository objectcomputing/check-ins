package com.objectcomputing.checkins.services.team;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TeamRepository extends CrudRepository<Team, UUID> {

    Optional<Team> findByName(String name);

    Optional<Team> findById(UUID id);

    @Override
    <S extends Team> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Team> S save(@Valid @NotNull @NonNull S entity);

    @Query(value = "SELECT t_.id, PGP_SYM_DECRYPT(cast(t_.name as bytea),'${aes.key}') as name, PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description, is_active " +
            "FROM team t_ " +
            "LEFT JOIN team_member tm_ " +
            "   ON t_.id = tm_.teamid " +
            "WHERE (:name IS NULL OR PGP_SYM_DECRYPT(cast(t_.name as bytea),'${aes.key}') = :name) " +
            "AND (:memberid IS NULL OR tm_.memberid = :memberid) ")
    List<Team> search(@Nullable String name, @Nullable String memberid);

}

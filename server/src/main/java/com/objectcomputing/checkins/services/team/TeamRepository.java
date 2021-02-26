package com.objectcomputing.checkins.services.team;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.reactivex.annotations.NonNull;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    @Query("SELECT * " +
            "FROM team t_ " +
            "LEFT JOIN team_member tm_ " +
            "   ON t_.id = tm_.teamid " +
            "WHERE (:name IS NULL OR t_.name = :name) " +
            "AND (:memberid IS NULL OR tm_.memberid = :memberid) ")
    List<Team> search(@Nullable String name, @Nullable String memberid);

}

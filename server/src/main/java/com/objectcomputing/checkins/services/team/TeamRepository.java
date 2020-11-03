package com.objectcomputing.checkins.services.team;

import nu.studer.sample.tables.pojos.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


//@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TeamRepository {//} extends CrudRepository<Team, UUID> {

    Optional<Team> findById(@NotNull UUID id);

    Optional<Team> findByName(@NotNull String name);

    Team save(@Nonnull Team saveMe);

    Team update(@NotNull Team updateMe);

    List<Team> search(@Nullable String name, @Nullable UUID memberId);

    void deleteById(@NotNull UUID id);

    /*Optional<Team> findByName(String name);

    Optional<Team> findById(UUID id);

    List<Team> findByNameIlike(String name);

    @Override
    <S extends Team> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Team> S save(@Valid @NotNull @NonNull S entity);*/
}

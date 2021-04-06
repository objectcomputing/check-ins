package com.objectcomputing.checkins.services.guild;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GuildRepository extends CrudRepository<Guild, UUID> {

    Optional<Guild> findByName(String name);

    @Query("SELECT * " +
            "FROM guilds guild " +
            "WHERE (:name IS NULL OR guild.name LIKE :name) " +
            "AND (:memberid IS NULL OR guild.id IN " +
            "(SELECT guildid FROM guildmembers gm " +
            "WHERE(:memberid IS NULL OR  gm.memberid = :memberid)))")
    Set<Guild> search(@Nullable String name, @Nullable String memberid);

    @Override
    <S extends Guild> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Guild> S save(@Valid @NotNull @NonNull S entity);

    @Override
    void deleteById(@NotNull UUID id);
}

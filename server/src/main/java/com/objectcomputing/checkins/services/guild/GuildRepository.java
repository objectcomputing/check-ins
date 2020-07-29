package com.objectcomputing.checkins.services.guild;

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
public interface GuildRepository extends CrudRepository<Guild, UUID> {

    Guild findByGuildid(UUID guildId);

    Optional<Guild> findByName(String name);

    List<Guild> findByNameIlike(String name);

    @Override
    <S extends Guild> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Guild> S save(@Valid @NotNull @NonNull S entity);
}

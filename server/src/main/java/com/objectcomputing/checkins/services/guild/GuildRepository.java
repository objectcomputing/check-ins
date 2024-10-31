package com.objectcomputing.checkins.services.guild;

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
public interface GuildRepository extends CrudRepository<Guild, UUID> {

    Optional<Guild> findByName(String name);

    Optional<Guild> findById(UUID id);

    @Override
    <S extends Guild> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Guild> S save(@Valid @NotNull @NonNull S entity);

    @Query(value = "SELECT t_.id, PGP_SYM_DECRYPT(cast(t_.name as bytea),'${aes.key}') as name, " +
            "PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description, " +
            "PGP_SYM_DECRYPT(cast(link as bytea),'${aes.key}') as link, " +
            "t_.community as community " +
            "FROM guild t_ " +
            "LEFT JOIN guild_member tm_ " +
            "   ON t_.id = tm_.guildid " +
            "WHERE (:name IS NULL OR PGP_SYM_DECRYPT(cast(t_.name as bytea),'${aes.key}') = :name) " +
            "AND (:memberid IS NULL OR tm_.memberid = :memberid) ")
    List<Guild> search(@Nullable String name, @Nullable String memberid);

}

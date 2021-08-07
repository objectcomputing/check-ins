package com.objectcomputing.checkins.services.rale;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.reactivex.annotations.NonNull;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RaleRepository extends CrudRepository<Rale, UUID> {

    Optional<Rale> findByRale(RaleType rale);

    Optional<Rale> findById(UUID id);

    @Override
    <S extends Rale> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends Rale> S save(@Valid @NotNull @NonNull S entity);

    @Query(value = "SELECT t_.id, PGP_SYM_DECRYPT(cast(t_.rale as bytea),'${aes.key}') as rale, PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description  " +
            "FROM rale t_ " +
            "LEFT JOIN rale_member tm_ " +
            "   ON t_.id = tm_.raleid " +
            "WHERE (:rale IS NULL OR PGP_SYM_DECRYPT(cast(t_.rale as bytea),'${aes.key}') = :rale) " +
            "AND (:memberid IS NULL OR tm_.memberid = :memberid) ")
    List<Rale> search(@Nullable RaleType rale, @Nullable String memberid);

}

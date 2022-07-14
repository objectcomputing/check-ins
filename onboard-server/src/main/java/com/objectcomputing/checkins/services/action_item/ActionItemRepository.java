package com.objectcomputing.checkins.services.action_item;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.core.annotation.NonNull;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ActionItemRepository extends CrudRepository<ActionItem, UUID> {

    @Query(value = "select id, checkinId, createdById, PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description, priority " +
            "FROM action_items item " +
            "WHERE (:checkinId IS NULL OR item.checkinid = :checkinId) " +
            "AND (:createdById IS NULL OR item.createdbyid = :createdById) " +
            "ORDER BY priority", nativeQuery = true)
    List<ActionItem> search(@Nullable String checkinId, @Nullable String createdById);

    @Override
    <S extends ActionItem> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends ActionItem> S save(@Valid @NotNull @NonNull S entity);

    Optional<Double> findMaxPriorityByCheckinid(UUID checkinid);
}


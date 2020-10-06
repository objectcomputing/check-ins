package com.objectcomputing.checkins.services.agenda_item;

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
import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AgendaItemRepository extends CrudRepository<AgendaItem, UUID> {

    @Query("SELECT * " +
            "FROM agenda_items item " +
            "WHERE (:checkinId IS NULL OR item.checkinid = :checkinId) " +
            "AND (:createdById IS NULL OR item.createdbyid = :createdById)")
    Set<AgendaItem> search(@Nullable String checkinId, @Nullable String createdById);

    @Override
    <S extends AgendaItem> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends AgendaItem> S save(@Valid @NotNull @NonNull S entity);

    Optional<Double> findMaxPriorityByCheckinid(UUID checkinid);

}

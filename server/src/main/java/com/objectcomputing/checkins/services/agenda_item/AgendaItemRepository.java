package com.objectcomputing.checkins.services.agenda_item;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AgendaItemRepository extends CrudRepository<AgendaItem, UUID> {

    List<AgendaItem> findByCheckinid(UUID checkinid);

    List<AgendaItem> findByCreatedbyid(UUID uuid);

    @Override
    <S extends AgendaItem> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends AgendaItem> S save(@Valid @NotNull @NonNull S entity);

}
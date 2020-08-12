package com.objectcomputing.checkins.services.checkinnotes;

import java.util.List;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CheckinNoteRepository extends CrudRepository<CheckinNote, UUID> {
    List<CheckinNote> findByCheckinid(UUID checkinid);

    List<CheckinNote> findByCreatedbyid(UUID createbyid);

    @Override
    <S extends CheckinNote> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends CheckinNote> S save(@Valid @NotNull @Nonnull S entity);
}
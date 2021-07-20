package com.objectcomputing.checkins.services.checkin_notes;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CheckinNoteRepository extends CrudRepository<CheckinNote, UUID> {

    @Query(value = "SELECT id, checkinid, createdById, " +
            "PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description " +
            "FROM checkin_notes cn " +
            "WHERE (:checkinid  IS NULL OR cn.checkinid = :checkinid) " +
            "AND (:createdById  IS NULL OR cn.createdByid= :createdById) ")
    Set<CheckinNote> search(@Nullable String checkinid, @Nullable String createdById);

    @Override
    <S extends CheckinNote> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends CheckinNote> S save(@Valid @NotNull @NonNull S entity);
}
package com.objectcomputing.checkins.services.private_notes;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PrivateNoteRepository extends CrudRepository<PrivateNote, UUID> {

    @Query(" SELECT * " +
            "FROM private_notes pn " +
            "WHERE (:checkinid  IS NULL OR cn.checkinId= :checkinid) " +
            "AND (:createdById  IS NULL OR cn.createdByid= :createdById) ")
    Set<PrivateNote> search(@Nullable String checkinid, @Nullable String createdById);

    @Override
    <S extends PrivateNote> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends PrivateNote> S save(@Valid @NotNull @Nonnull S entity);
}
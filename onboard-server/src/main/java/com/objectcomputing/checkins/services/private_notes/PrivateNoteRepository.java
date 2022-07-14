package com.objectcomputing.checkins.services.private_notes;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import io.micronaut.core.annotation.Nullable;
import java.util.Set;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PrivateNoteRepository extends CrudRepository<PrivateNote, UUID> {

    @Query(" SELECT id,checkinid,createdbyid, PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description " +
            "FROM private_notes cn " +
            "WHERE (:checkinid  IS NULL OR cn.checkinid= :checkinid) " +
            "AND (:createdById  IS NULL OR cn.createdbyid= :createdById) ")
    Set<PrivateNote> search(@Nullable String checkinid, @Nullable String createdById);

}
package com.objectcomputing.checkins.services.private_notes;

import com.objectcomputing.checkins.services.checkin_notes.CheckinNote;
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

    @Query(" SELECT id,checkinid,createdbyid, PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description " +
            "FROM private_notes cn " +
            "WHERE (:checkinid  IS NULL OR cn.checkinid= :checkinid) " +
            "AND (:createdById  IS NULL OR cn.createdbyid= :createdById) ")
    Set<PrivateNote> search(@Nullable String checkinid, @Nullable String createdById);

}
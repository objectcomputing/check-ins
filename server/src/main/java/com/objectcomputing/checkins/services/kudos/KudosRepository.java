package com.objectcomputing.checkins.services.kudos;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface KudosRepository extends CrudRepository<Kudos, UUID> {

    @Query(value = "SELECT " +
            "id, " +
            "PGP_SYM_DECRYPT(cast(message as bytea), '${aes.key}') as message, " +
            "senderid, recipientid, datecreated, dateapproved " +
            "FROM kudos " +
            "WHERE (:senderId IS NULL OR senderid = :senderId) " +
            "AND (:recipientId IS NULL OR recipientid = :recipientId) " +
            "AND (:includePending OR dateapproved IS NOT NULL)"
    )
    List<Kudos> search(@Nullable String senderId, @Nullable String recipientId, boolean includePending);

}

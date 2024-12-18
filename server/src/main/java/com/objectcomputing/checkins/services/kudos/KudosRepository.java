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

        @Query(value = """
                SELECT
                    id,
                    PGP_SYM_DECRYPT(cast(message as bytea), '${aes.key}') as message,
                    senderid, teamid, datecreated, dateapproved, publiclyVisible
                FROM kudos
                WHERE dateapproved > current_date - interval '1' month
                  AND publiclyVisible IS TRUE
                ORDER BY datecreated DESC""", nativeQuery = true)
        List<Kudos> getRecentPublic();

        @Query(value = """
                SELECT
                    id,
                    PGP_SYM_DECRYPT(cast(message as bytea), '${aes.key}') as message,
                    senderid, teamid, datecreated, dateapproved, publiclyVisible
                FROM kudos
                WHERE dateapproved IS NULL
                  AND publiclyVisible IS TRUE
                ORDER BY datecreated DESC""", nativeQuery = true)
        List<Kudos> getAllPending();

        @Query(value = """
                SELECT
                    id,
                    PGP_SYM_DECRYPT(cast(message as bytea), '${aes.key}') as message,
                    senderid, teamid, datecreated, dateapproved, publiclyVisible
                FROM kudos
                WHERE dateapproved IS NOT NULL
                  AND publiclyVisible IS TRUE""", nativeQuery = true)
        List<Kudos> getAllApproved();

        @Query(value = """
                SELECT
                    id,
                    PGP_SYM_DECRYPT(cast(message as bytea), '${aes.key}') as message,
                    senderid, teamid, datecreated, dateapproved, publiclyVisible
                FROM kudos
                WHERE (:senderId IS NULL OR senderid = :senderId)
                  AND (:includePending OR dateapproved IS NOT NULL)""", nativeQuery = true)
        List<Kudos> search(@Nullable String senderId, boolean includePending);

}

package com.objectcomputing.checkins.services.slack.kudos;

import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.annotation.Query;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AutomatedKudosRepository extends CrudRepository<AutomatedKudos, UUID> {
    @Query(value = """
           SELECT
             id, requested,
             PGP_SYM_DECRYPT(cast(message as bytea), '${aes.key}') as message,
             PGP_SYM_DECRYPT(cast(externalid as bytea), '${aes.key}') as externalid,
             senderid, recipientids
           FROM automated_kudos
           WHERE requested IS FALSE""", nativeQuery = true)
        List<AutomatedKudos> getUnrequested(); 
}

package com.objectcomputing.checkins.services.document;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DocumentRepository extends CrudRepository<Document, UUID> {

    @Query("SELECT DISTINCT documents.id, " +
            "PGP_SYM_DECRYPT(cast(documents.name as bytea), '${aes.key}') as name, " +
            "PGP_SYM_DECRYPT(cast(documents.description as bytea), '${aes.key}') as description, " +
            "PGP_SYM_DECRYPT(cast(documents.url as bytea), '${aes.key}') as url " +
            "FROM documents " +
            "WHERE LOWER(PGP_SYM_DECRYPT(cast(documents.name as bytea), '${aes.key}')) = LOWER(:name)"
    )
    Optional<Document> findByName(String name);
}

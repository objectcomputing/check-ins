package com.objectcomputing.checkins.services.document;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DocumentRepository extends CrudRepository<Document, UUID> {

    @Query(
            value = """
                    SELECT
                        d.document_id
                      , PGP_SYM_DECRYPT(cast(d.name as bytea), '${aes.key}') as name
                      , PGP_SYM_DECRYPT(cast(d.url as bytea), '${aes.key}') as url
                      , PGP_SYM_DECRYPT(cast(d.description as bytea), '${aes.key}') as description
                      FROM document d
                      JOIN role_documentation rd USING (document_id)
                      WHERE rd.role_id::uuid = :roleId
                      ORDER BY rd.display_order""",
            nativeQuery = true
    )
    List<Document> findByRoleId(UUID roleId);

    @Query(
            value = """
                    SELECT
                        d.document_id
                      , PGP_SYM_DECRYPT(cast(d.name as bytea), '${aes.key}') as name
                      , PGP_SYM_DECRYPT(cast(d.url as bytea), '${aes.key}') as url
                      , PGP_SYM_DECRYPT(cast(d.description as bytea), '${aes.key}') as description
                      FROM document d
                      ORDER BY name""",
            nativeQuery = true
    )
    List<Document> findAllOrderByNameAndUrl();

    @Query(
            value = """
                    SELECT
                        d.document_id
                      , PGP_SYM_DECRYPT(cast(d.name as bytea), '${aes.key}') as name
                      , PGP_SYM_DECRYPT(cast(d.url as bytea), '${aes.key}') as url
                      , PGP_SYM_DECRYPT(cast(d.description as bytea), '${aes.key}') as description
                      FROM document d
                      WHERE PGP_SYM_DECRYPT(cast(d.name as bytea), '${aes.key}') = :name""",
            nativeQuery = true
    )
    Optional<Document> findByName(String name);

    @Query(
            value = """
                    SELECT
                        d.document_id
                      , PGP_SYM_DECRYPT(cast(d.name as bytea), '${aes.key}') as name
                      , PGP_SYM_DECRYPT(cast(d.url as bytea), '${aes.key}') as url
                      , PGP_SYM_DECRYPT(cast(d.description as bytea), '${aes.key}') as description
                      FROM document d
                      WHERE PGP_SYM_DECRYPT(cast(d.url as bytea), '${aes.key}') = :url""",
            nativeQuery = true
    )
    Optional<Document> findByUrl(String url);
}

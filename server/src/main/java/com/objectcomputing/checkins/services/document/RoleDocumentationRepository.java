package com.objectcomputing.checkins.services.document;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RoleDocumentationRepository extends CrudRepository<RoleDocumentation, RoleDocumentationId> {

    @Query(
            value = """
                    SELECT EXISTS(
                        SELECT 1
                          FROM role_documentation
                          WHERE document_id = :documentId
                    )""",
            nativeQuery = true
    )
    boolean documentIsStillReferenced(UUID documentId);

    @Query(
            value = """
                    DELETE
                      FROM role_documentation
                      WHERE role_id = :roleId""",
            nativeQuery = true
    )
    void deleteByRoleId(UUID roleId);
}

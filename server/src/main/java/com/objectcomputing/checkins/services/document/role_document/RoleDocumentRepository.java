package com.objectcomputing.checkins.services.document.role_document;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RoleDocumentRepository extends CrudRepository<RoleDocument, RoleDocumentId> {

    @Query(value =
            "SELECT documents.id, " +
                    "PGP_SYM_DECRYPT(cast(documents.name as bytea), '${aes.key}') as name, " +
                    "PGP_SYM_DECRYPT(cast(documents.description as bytea), '${aes.key}') as description, " +
                    "PGP_SYM_DECRYPT(cast(documents.url as bytea), '${aes.key}') as url, " +
                    "role_documents.documentnumber " +
            "FROM documents JOIN role_documents " +
            "ON role_documents.documentid = documents.id AND role_documents.roleid = :roleId " +
            "ORDER BY role_documents.documentnumber"
    )
    List<DocumentResponseDTO> findDocumentsByRoleId(UUID roleId);
}

package com.objectcomputing.checkins.services.opportunities;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OpportunitiesRepository extends CrudRepository<Opportunities, UUID> {

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(name as bytea),'${aes.key}') as name, " +
            "PGP_SYM_DECRYPT(cast(description as bytea),'${aes.key}') as description, " +
            "PGP_SYM_DECRYPT(cast(url as bytea),'${aes.key}') as url, " +
            "expiresOn, submittedOn, submittedBy, pending " +
            "FROM opportunities op " +
            "WHERE (:name IS NULL OR PGP_SYM_DECRYPT(cast(op.name as bytea),'${aes.key}') = :name) " +
            "AND (:description IS NULL OR PGP_SYM_DECRYPT(cast(op.description as bytea),'${aes.key}') = :description) " +
            "AND (:submittedBy IS NULL OR op.submittedBy = :submittedBy)", nativeQuery = true)
    List<Opportunities> searchByValues( @Nullable String name,
                                   @Nullable String description,
                                  @Nullable String submittedBy);

}
package com.objectcomputing.checkins.services.onboard.background_information;


import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface BackgroundInformationRepository extends CrudRepository<BackgroundInformation, UUID> {

    List<BackgroundInformation> findAll();

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.userId as bytea),'${aes.key}') as userId," +
            "stepComplete " +
            "FROM \"background_information\" mp " +
            "WHERE (:userId IS NULL OR PGP_SYM_DECRYPT(cast(mp.userId as bytea), '${aes.key}') = :userId) " +
            "AND  (:stepComplete IS NULL OR stepComplete = :stepComplete) ",
            nativeQuery = true)
    List<BackgroundInformation> search(
            @Nullable String id,
            @Nullable String userId,
            @Nullable Boolean stepComplete
    );
}

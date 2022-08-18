package com.objectcomputing.checkins.services.workingenvironment;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WorkingEnvironmentRepository extends ReactorCrudRepository<WorkingEnvironment, UUID> {

    Flux<WorkingEnvironment> findAll();

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.workLocation as bytea),'${aes.key}') as workLocation," +
            "PGP_SYM_DECRYPT(cast(mp.keyType as bytea),'${aes.key}') as keyType," +
            "PGP_SYM_DECRYPT(cast(mp.osType as bytea),'${aes.key}') as osType," +
            "PGP_SYM_DECRYPT(cast(mp.accessories as bytea),'${aes.key}') as accessories," +
            "PGP_SYM_DECRYPT(cast(mp.otherAccessories as bytea),'${aes.key}') as otherAccessories " +
            "FROM \"working_environment\" mp " +
            "WHERE (:workLocation IS NULL OR PGP_SYM_DECRYPT(cast(mp.workLocation as bytea), '${aes.key}') = :workLocation) "
            +
            "AND  (:keyType IS NULL OR PGP_SYM_DECRYPT(cast(mp.keyType as bytea), '${aes.key}') = :keyType) " +
            "AND  (:osType IS NULL OR PGP_SYM_DECRYPT(cast(mp.osType as bytea), '${aes.key}') = :osType) " +
            "AND  (:accessories IS NULL OR PGP_SYM_DECRYPT(cast(mp.accessories as bytea),'${aes.key}') = :accessories) "
            +
            "AND  (:otherAccessories IS NULL OR PGP_SYM_DECRYPT(cast(mp.otherAccessories as bytea),'${aes.key}') = :otherAccessories) ", nativeQuery = true)
    Mono<WorkingEnvironment> search(
            @Nullable UUID id,
            @Nullable String workLocation,
            @Nullable String keyType,
            @Nullable String osType,
            @Nullable String accessories,
            @Nullable String otherAccessories);
}

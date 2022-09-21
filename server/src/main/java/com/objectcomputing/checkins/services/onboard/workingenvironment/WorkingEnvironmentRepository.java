package com.objectcomputing.checkins.services.onboard.workingenvironment;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WorkingEnvironmentRepository extends CrudRepository<WorkingEnvironment, UUID> {

    List<WorkingEnvironment> findAll();

    @Query(value = "SELECT working_environment_id, " +
            //this decodes the column names and both fields here need to match the column names in the SQL table
            "PGP_SYM_DECRYPT(cast(ev.work_location as bytea),'${aes.key}') as work_location," +
            "PGP_SYM_DECRYPT(cast(ev.key_type as bytea),'${aes.key}') as key_type," +
            "PGP_SYM_DECRYPT(cast(ev.os_type as bytea),'${aes.key}') as os_type," +
            "PGP_SYM_DECRYPT(cast(ev.accessories as bytea),'${aes.key}') as accessories," +
            "PGP_SYM_DECRYPT(cast(ev.other_accessories as bytea),'${aes.key}') as other_accessories " +
           // below should be the name of the table and then we call it "ev" so we don't write it over and over again
            //"ev" can be any name of a variable that makes sense
            "FROM \"working_environment\" ev " +
            //workLocation is the variable that is searched below
            // looks to see if the parameter is null or if it exists in a column name
            //if its null or if it's present, it continues to the next search term because of the AND
            "WHERE (:workLocation IS NULL OR PGP_SYM_DECRYPT(cast(ev.work_location as bytea), '${aes.key}') = :workLocation) "
            +
            "AND  (:keyType IS NULL OR PGP_SYM_DECRYPT(cast(ev.key_type as bytea), '${aes.key}') = :keyType) " +
            "AND  (:osType IS NULL OR PGP_SYM_DECRYPT(cast(ev.os_type as bytea), '${aes.key}') = :osType) " +
            "AND  (:accessories IS NULL OR PGP_SYM_DECRYPT(cast(ev.accessories as bytea),'${aes.key}') = :accessories) "
            +
            //nativeQuery = true tells to trust the query that it's ok
            "AND  (:otherAccessories IS NULL OR PGP_SYM_DECRYPT(cast(mp.otherAccessories as bytea),'${aes.key}') = :otherAccessories) ", nativeQuery = true)
    List<WorkingEnvironment> search(
            @Nullable UUID workingEnvironmentId,
            @Nullable String workLocation,
            @Nullable String keyType,
            @Nullable String osType,
            @Nullable String accessories,
            @Nullable String otherAccessories);
}

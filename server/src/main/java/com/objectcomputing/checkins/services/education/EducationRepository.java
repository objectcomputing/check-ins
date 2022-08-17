package com.objectcomputing.checkins.services.education;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface EducationRepository extends CrudRepository<Education, UUID>{
    @Query(value = "SELECT id, " +
    "PGP_SYM_DECRYPT(cast(mp.highestDegree as bytea),'${aes.key}') as highestDegree, " +
    "PGP_SYM_DECRYPT(cast(mp.institution as bytea),'${aes.key}') as institution," +
    "PGP_SYM_DECRYPT(cast(mp.location as bytea),'${aes.key}') as location," +
    "PGP_SYM_DECRYPT(cast(mp.degree as bytea),'${aes.key}') as degree," +
    "PGP_SYM_DECRYPT(cast(mp.major as bytea),'${aes.key}') as major," +
    "PGP_SYM_DECRYPT(cast(mp.completionDate as bytea),'${aes.key}') as completionDate," +
    "PGP_SYM_DECRYPT(cast(mp.additionalInfo as bytea), '${aes.key}') as additionalInfo," +
    "FROM \"education\" mp " +
    "WHERE  (:highestDegree IS NULL OR PGP_SYM_DECRYPT(cast(mp.highestDegree as bytea), '${aes.key}') = :highestDegree) " +
            "AND  (:institution IS NULL OR PGP_SYM_DECRYPT(cast(mp.institution as bytea),'${aes.key}') = :institution) " +
            "AND  (:location IS NULL OR PGP_SYM_DECRYPT(cast(mp.location as bytea),'${aes.key}') = :location) " +
            "AND  (:degree IS NULL OR PGP_SYM_DECRYPT(cast(mp.degree as bytea),'${aes.key}') = :degree) " +
            "AND  (:major IS NULL OR PGP_SYM_DECRYPT(cast(mp.major as bytea),'${aes.key}') = :major) " +
            "AND  (CAST(:completionDate as date) IS NULL OR PGP_SYM_DECRYPT(cast(mp.completionDate as bytea),'${aes.key}') = :completionDate) " +
            "AND  (:additionalInfo IS NULL OR PGP_SYM_DECRYPT(cast(mp.additionalInfo as bytea),'${aes.key}') = :additionalInfo) " ,
    nativeQuery = true)
    List<Education> search(
        @Nullable UUID id,
        @Nullable String highestDegree,
        @Nullable String institution,
        @Nullable String location,
        @Nullable String degree,
        @Nullable String major,
        @Nullable LocalDate completionDate,
        @Nullable String additionalInfo
    );
}

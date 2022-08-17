package com.objectcomputing.checkins.services.employmenthistory;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository (dialect = Dialect.POSTGRES)
public interface EmploymentHistoryRepository extends CrudRepository<EmploymentHistory, UUID> {
    @Query(value = "SELECT id, " +
    "PGP_SYM_DECRYPT(cast(mp.company as bytea),'${aes.key}') as company, " +
    "PGP_SYM_DECRYPT(cast(mp.companyAddress as bytea),'${aes.key}') as companyAddress," +
    "PGP_SYM_DECRYPT(cast(mp.jobTitle as bytea),'${aes.key}') as jobTitle," +
    "PGP_SYM_DECRYPT(cast(mp.startDate as bytea),'${aes.key}') as startDate," +
    "PGP_SYM_DECRYPT(cast(mp.endDate as bytea),'${aes.key}') as endDate," +
    "PGP_SYM_DECRYPT(cast(mp.reason as bytea),'${aes.key}') as reason," +
    "FROM \"employment_history\" mp " +
    "WHERE  (:company IS NULL OR PGP_SYM_DECRYPT(cast(mp.company as bytea), '${aes.key}') = :company) " +
            "AND  (:companyAddress IS NULL OR PGP_SYM_DECRYPT(cast(mp.companyAddress as bytea),'${aes.key}') = :companyAddress) " +
            "AND  (:jobTitle IS NULL OR PGP_SYM_DECRYPT(cast(mp.jobTitle as bytea),'${aes.key}') = :jobTitle) " +
            "AND  (CAST(:startDate as date) IS NULL OR PGP_SYM_DECRYPT(cast(mp.startDate as bytea),'${aes.key}') = :startDate) " +
            "AND  (CAST(:endDate as date) IS NULL OR PGP_SYM_DECRYPT(cast(mp.endDate as bytea),'${aes.key}') = :endDate) " +
            "AND  (:reason IS NULL OR PGP_SYM_DECRYPT(cast(mp.reason as bytea),'${aes.key}') = :reason) " ,
    nativeQuery = true)
    List<EmploymentHistory> search(
        @Nullable UUID id,
        @Nullable String company,
        @Nullable String companyAddress,
        @Nullable String jobTitle,
        @Nullable LocalDate startDate,
        @Nullable LocalDate endDate,
        @Nullable String reason
    );
}

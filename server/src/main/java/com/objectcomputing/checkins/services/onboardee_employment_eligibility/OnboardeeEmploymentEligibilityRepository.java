package com.objectcomputing.checkins.services.onboardee_employment_eligibility;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OnboardeeEmploymentEligibilityRepository extends CrudRepository<OnboardeeEmploymentEligibility, UUID> {

    List<OnboardeeEmploymentEligibility> findAll();
    @Query(value = "SELECT id, " +
    "PGP_SYM_DECRYPT(cast(mp.ageLegal as bytea),'${aes.key}') as ageLegal, " +
    "PGP_SYM_DECRYPT(cast(mp.usCitizen as bytea),'${aes.key}') as usCitizen, " +
    "PGP_SYM_DECRYPT(cast(mp.visaStatus as bytea),'${aes.key}') as visaStatus, " +
    "PGP_SYM_DECRYPT(cast(mp.expirationDate as bytea),'${aes.key}') as expirationDate, " +
    "PGP_SYM_DECRYPT(cast(mp.felonyStatus as bytea),'${aes.key}') as felonyStatus, " +
    "PGP_SYM_DECRYPT(cast(mp.felonyExplanation as bytea),'${aes.key}') as felonyExplanation " +
    "FROM \"onboardee-employment-eligibility\" mp " +
    "WHERE (:ageLegal IS NULL OR PGP_SYM_DECRYPT(cast(mp.ageLegal as bytea), '${aes.key}') = :ageLegal) " +
            "AND  (:usCitizen IS NULL OR PGP_SYM_DECRYPT(cast(mp.usCitizen as bytea), '${aes.key}') = :usCitizen) " +
            "AND  (:visaStatus IS NULL OR PGP_SYM_DECRYPT(cast(mp.visaStatus as bytea), '${aes.key}') = :visaStatus) " +
            "AND  (:expirationDate IS NULL OR PGP_SYM_DECRYPT(cast(mp.expirationDate as bytea), '${aes.key}') = :expirationDate) " +
            "AND  (:felonyStatus IS NULL OR PGP_SYM_DECRYPT(cast(mp.felonyStatus as bytea), '${aes.key}') = :felonyStatus) " +
            "AND  (:felonyExplanation IS NULL OR PGP_SYM_DECRYPT(cast(mp.felonyExplanation as bytea), '${aes.key}') = :felonyExplanation) ",
    nativeQuery = true)
    List<OnboardeeEmploymentEligibility> search(
            @Nullable String id,
            Boolean ageLegal,
            Boolean usCitizen,
            @Nullable String visaStatus,
            @Nullable LocalDate expirationDate,
            Boolean felonyStatus,
            @Nullable String felonyExplanation
    );
}

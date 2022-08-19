package com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility;

import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OnboardeeEmploymentEligibilityRepository extends ReactorCrudRepository<OnboardeeEmploymentEligibility, UUID> {

    Flux<OnboardeeEmploymentEligibility> findAll();

    @Query(value = "SELECT id, " +
            "ageLegal, " +
            "usCitizen," +
            "PGP_SYM_DECRYPT(cast(mp.visaStatus as bytea),'${aes.key}') as visaStatus," +
            "PGP_SYM_DECRYPT(cast(mp.expirationDate as bytea),'${aes.key}') as expirationDate," +
            "felonyStatus, " +
            "PGP_SYM_DECRYPT(cast(mp.felonyExplanation as bytea),'${aes.key}') as felonyExplanation, " +
            "FROM \"onboardee_employment_eligibility\" mp " +
            "WHERE (:ageLegal IS NULL OR ageLegal = :ageLegal) " +
            "AND  (:usCitizen IS NULL OR usCitizen = :usCitizen) " +
            "AND  (:visaStatus IS NULL OR PGP_SYM_DECRYPT(cast(mp.visaStatus as bytea),'${aes.key}') = :visaStatus) " +
            "AND  (CAST(:expirationDate as date) IS NULL OR CAST(PGP_SYM_DECRYPT(cast(mp.expirationDate as bytea),'${aes.key}') as date) = :expirationDate) " +
            "AND  (:felonyStatus IS NULL OR felonyStatus = :felonyStatus) " +
            "AND  (:felonyExplanation IS NULL OR PGP_SYM_DECRYPT(cast(mp.felonyExplanation as bytea), '${aes.key}') = :felonyExplanation) ",
            nativeQuery = true)
    Mono<BackgroundInformation> search(
            @Nullable String id,
            @Nullable Boolean ageLegal,
            @Nullable Boolean usCitizen,
            @Nullable String visaStatus,
            @Nullable LocalDate expirationDate,
            @Nullable Boolean felonyStatus,
            @Nullable String felonyExplanation
    );
}

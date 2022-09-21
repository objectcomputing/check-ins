package com.objectcomputing.checkins.services.onboard.onboardeeprofile;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository (dialect = Dialect.POSTGRES)
public interface OnboardingProfileRepository extends CrudRepository<OnboardingProfile, UUID> {

    Optional<OnboardingProfile> findBySocialSecurityNumber(@NotNull String socialSecurityNumber);

    List<OnboardingProfile> findAll();

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') as firstName, " +
            "PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') as middleName," +
            "PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') as lastName," +
            "PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea),'${aes.key}') as socialSecurityNumber," +
            "PGP_SYM_DECRYPT(cast(mp.birthDate as bytea),'${aes.key}') as birthDate," +
            "PGP_SYM_DECRYPT(cast(mp.currentAddress as bytea),'${aes.key}') as currentAddress," +
            "PGP_SYM_DECRYPT(cast(mp.previousAddress as bytea),'${aes.key}') as previousAddress," +
            "PGP_SYM_DECRYPT(cast(mp.phoneNumber as bytea),'${aes.key}') as phoneNumber," +
            "PGP_SYM_DECRYPT(cast(mp.secondPhoneNumber as bytea),'${aes.key}') as secondPhoneNumber," +
            "PGP_SYM_DECRYPT(cast(mp.personalEmail as bytea),'${aes.key}') as personalEmail, " +
            "FROM \"onboard_profile\" mp " +
            "WHERE  (:socialSecurityNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea), '${aes.key}') = :socialSecurityNumber) " +
                    "AND  (:firstName IS NULL OR PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') = :firstName) " +
                    "AND  (:middleName IS NULL OR PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') = :middleName) " +
                    "AND  (:lastName IS NULL OR PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') = :lastName) " +
                    "AND  (CAST(:birthDate as date) IS NULL OR CAST(PGP_SYM_DECRYPT(cast(mp.birthDate as bytea),'${aes.key}') as date) = :birthDate) " +
                    "AND  (:currentAddress IS NULL OR PGP_SYM_DECRYPT(cast(mp.currentAddress as bytea),'${aes.key}') = :currentAddress) " +
                    "AND  (:previousAddress IS NULL OR PGP_SYM_DECRYPT(cast(mp.previousAddress as bytea),'${aes.key}') = :previousAddress) " +
                    "AND  (:phoneNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.phoneNumber as bytea),'${aes.key}') = :phoneNumber) " +
                    "AND  (:secondPhoneNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.secondPhoneNumber as bytea),'${aes.key}') = :secondPhoneNumber) " +
                    "AND  (:personalEmail IS NULL OR PGP_SYM_DECRYPT(cast(mp.personalEmail as bytea),'${aes.key}') = :personalEmail) ",
            nativeQuery = true)
    List<OnboardingProfile> search(
            @Nullable String id,
            @Nullable String firstName,
            @Nullable String middleName,
            @Nullable String lastName,
            @Nullable String socialSecurityNumber,
            @Nullable LocalDate birthDate,
            @Nullable String currentAddress,
            @Nullable String previousAddress,
            @Nullable String phoneNumber,
            @Nullable String secondPhoneNumber,
            @Nullable String personalEmail
    );
}
package com.objectcomputing.checkins.services.employmentpreferences;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface EmploymentDesiredAvailabilityRepository extends CrudRepository<EmploymentDesiredAvailability, UUID> {

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.desiredPosition as bytea),'${aes.key}') as desiredPosition, " +
            "PGP_SYM_DECRYPT(cast(mp.desiredStartDate as bytea),'${aes.key}') as desiredStartDate, " +
            "PGP_SYM_DECRYPT(cast(mp.desiredSalary as bytea),'${aes.key}') as desiredSalary, " +
            "PGP_SYM_DECRYPT(cast(mp.currentlyEmployed as bytea),'${aes.key}') as currentlyEmployed, " +
            "PGP_SYM_DECRYPT(cast(mp.contactCurrentEmployer as bytea),'${aes.key}') as contactCurrentEmployer, " +
            "PGP_SYM_DECRYPT(cast(mp.previousEmploymentOCI as bytea),'${aes.key}') as previousEmploymentOCI, " +
            "PGP_SYM_DECRYPT(cast(mp.noncompeteAgreement as bytea),'${aes.key}') as noncompeteAgreement, " +
            "PGP_SYM_DECRYPT(cast(mp.noncompeteExpirationDate as bytea),'${aes.key}') as noncompeteExpirationDate, " +
            "FROM \"employment_desired_availability\" mp ",
            nativeQuery = true)
    Optional<EmploymentDesiredAvailability> findByPosition(@NotNull String desiredPosition);
    List<EmploymentDesiredAvailability> findAll();
    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.desiredPosition as bytea),'${aes.key}') as desiredPosition, " +
            "PGP_SYM_DECRYPT(cast(mp.desiredStartDate as bytea),'${aes.key}') as desiredStartDate," +
            "PGP_SYM_DECRYPT(cast(mp.desiredSalary as bytea),'${aes.key}') as desiredSalary," +
            "PGP_SYM_DECRYPT(cast(mp.currentlyEmployed as bytea),'${aes.key}') as currentlyEmployed," +
            "PGP_SYM_DECRYPT(cast(mp.contactCurrentEmployer as bytea),'${aes.key}') as contactCurrentEmployer," +
            "PGP_SYM_DECRYPT(cast(mp.previousEmploymentOCI as bytea),'${aes.key}') as previousEmploymentOCI," +
            "PGP_SYM_DECRYPT(cast(mp.noncompeteAgreement as bytea),'${aes.key}') as noncompeteAgreement," +
            "PGP_SYM_DECRYPT(cast(mp.noncompeteExpirationDate as bytea),'${aes.key}') as noncompeteExpirationDate," +
            "FROM \"employment_desired_availability\" mp " +
            "AND  (:desiredPosition IS NULL OR PGP_SYM_DECRYPT(cast(mp.desiredPosition as bytea),'${aes.key}') = :desiredPosition) " +
            "AND  (:desiredStartDate IS NULL OR PGP_SYM_DECRYPT(cast(mp.desiredStartDate as bytea),'${aes.key}') = :desiredStartDate) " +
            "AND  (:desiredSalary IS NULL OR PGP_SYM_DECRYPT(cast(mp.desiredSalary as bytea),'${aes.key}') = :desiredSalary) " +
            "AND  (CAST(:currentlyEmployed as date) IS NULL OR CAST(PGP_SYM_DECRYPT(cast(mp.currentlyEmployed as bytea),'${aes.key}') as currentlyEmployed) = :birthDate) " +
            "AND  (:contactCurrentEmployer IS NULL OR PGP_SYM_DECRYPT(cast(mp.contactCurrentEmployer as bytea),'${aes.key}') = :contactCurrentEmployer) " +
            "AND  (:previousEmploymentOCI IS NULL OR PGP_SYM_DECRYPT(cast(mp.previousEmploymentOCI as bytea),'${aes.key}') = :previousEmploymentOCI) " +
            "AND  (:noncompeteAgreement IS NULL OR PGP_SYM_DECRYPT(cast(mp.noncompeteAgreement as bytea),'${aes.key}') = :noncompeteAgreement) " +
            "AND  (:noncompeteExpirationDate IS NULL OR PGP_SYM_DECRYPT(cast(mp.noncompeteExpirationDate as bytea),'${aes.key}') = :noncompeteExpirationDate) ",
            nativeQuery = true)
    List<EmploymentDesiredAvailability> search(
            @Nullable String id,
            @Nullable String desiredPosition,
            @Nullable LocalDate desiredStartDate,
            @Nullable String desiredSalary,
            @Nullable Boolean currentlyEmployed,
            @Nullable Boolean contactCurrentEmployer,
            @Nullable Boolean previousEmploymentOCI,
            @Nullable Boolean noncompeteAgreement,
            @Nullable LocalDate noncompeteExpirationDate);
}

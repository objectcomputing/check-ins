package com.objectcomputing.checkins.services.onboardeeprofile;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository (dialect = Dialect.POSTGRES)
public interface OnboardingProfileRepository extends CrudRepository<Onboarding_profile, UUID> {



    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') as firstName, " +
            "PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') as middleName," +
            "PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') as lastName," +
            "PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea),'${aes.key}') as ," +
            "PGP_SYM_DECRYPT(cast(mp.birthDate as bytea),'${aes.key}') as birthDate," +
            "PGP_SYM_DECRYPT(cast(mp.currentAddress as bytea),'${aes.key}') as currentAddress," +
            "PGP_SYM_DECRYPT(cast(mp.previousAddress as bytea),'${aes.key}') as previousAddress," +
            "PGP_SYM_DECRYPT(cast(mp.phoneNumber as bytea),'${aes.key}') as phoneNumber," +
            "PGP_SYM_DECRYPT(cast(mp.secondPhoneNumber as bytea),'${aes.key}') as secondPhoneNumber," +
            "FROM \"onboarding_profile\" mp " +
            "WHERE  (:socialSecurityNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea), '${aes.key}') = :socialSecurityNumber) ",
            nativeQuery = true)
    Optional<Onboarding_profile> findBySocial(@NotNull Integer socialSecurityNumber);
    List<Onboarding_profile> findAll();
    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') as firstName, " +
            "PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') as middleName," +
            "PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') as lastName," +
            "PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea),'${aes.key}') as ," +
            "PGP_SYM_DECRYPT(cast(mp.birthDate as bytea),'${aes.key}') as birthDate," +
            "PGP_SYM_DECRYPT(cast(mp.currentAddress as bytea),'${aes.key}') as currentAddress," +
            "PGP_SYM_DECRYPT(cast(mp.previousAddress as bytea),'${aes.key}') as previousAddress," +
            "PGP_SYM_DECRYPT(cast(mp.phoneNumber as bytea),'${aes.key}') as phoneNumber," +
            "PGP_SYM_DECRYPT(cast(mp.secondPhoneNumber as bytea),'${aes.key}') as secondPhoneNumber," +
            "FROM \"onboarding_profile\" mp " +
            "WHERE  (:socialSecurityNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea), '${aes.key}') = :socialSecurityNumber) ",
            "AND  (:firstName IS NULL OR PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') as firstName, " +
                    "AND  (:middleName IS NULL OR PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') as middleName," +
                    "AND  (:lastName IS NULL OR PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') as lastName," +
                    "AND  (:socialSecurityNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea),'${aes.key}') as ," +
                    "AND  (:birthDate IS NULL OR PGP_SYM_DECRYPT(cast(mp.birthDate as bytea),'${aes.key}') as birthDate," +
                    "AND  (:currentAddress IS NULL OR PGP_SYM_DECRYPT(cast(mp.currentAddress as bytea),'${aes.key}') as currentAddress," +
                    "AND  (:previousAddress IS NULL OR PGP_SYM_DECRYPT(cast(mp.previousAddress as bytea),'${aes.key}') as previousAddress," +
                    "AND  (:phoneNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.phoneNumber as bytea),'${aes.key}') as phoneNumber," +
                    "OR  (:secondPhoneNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.secondPhoneNumber as bytea),'${aes.key}') as secondPhoneNumber," ,
            nativeQuery = true)
    List<Onboarding_profile> search(
            @Nullable UUID id,
            @Nullable String firstName,
            @Nullable String middleName,
            @Nullable String lastName,
            @Nullable Integer socialSecurityNumber,
            @Nullable Date birthDate,
            @Nullable String currentAddress,
            @Nullable String previousAddress,
            @Nullable Integer phoneNumber,
            @Nullable Integer secondPhoneNumber
    );

}

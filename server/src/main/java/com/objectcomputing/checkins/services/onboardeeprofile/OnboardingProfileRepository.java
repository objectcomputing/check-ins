package com.objectcomputing.checkins.services.onboardeeprofile;
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

@JdbcRepository (dialect = Dialect.POSTGRES)
public interface OnboardingProfileRepository extends CrudRepository<OnboardingProfile, UUID> {



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
    Optional<OnboardingProfile> findBySocial(@NotNull Integer socialSecurityNumber);
    List<OnboardingProfile> findAll();
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
            "WHERE  (:socialSecurityNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea), '${aes.key}') = :socialSecurityNumber) " +
            "AND  (:firstName IS NULL OR PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'${aes.key}') = :firstName, " +
                    "AND  (:middleName IS NULL OR PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'${aes.key}') = : middleName," +
                    "AND  (:lastName IS NULL OR PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'${aes.key}') = : lastName," +
                    "AND  (:socialSecurityNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.socialSecurityNumber as bytea),'${aes.key}') = :socialSecurityNumber)" +
                    "AND  (:birthDate IS NULL OR PGP_SYM_DECRYPT(cast(mp.birthDate as bytea),'${aes.key}') = : birthDate," +
                    "AND  (:currentAddress IS NULL OR PGP_SYM_DECRYPT(cast(mp.currentAddress as bytea),'${aes.key}') = : currentAddress," +
                    "AND  (:previousAddress IS NULL OR PGP_SYM_DECRYPT(cast(mp.previousAddress as bytea),'${aes.key}') = : previousAddress," +
                    "AND  (:phoneNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.phoneNumber as bytea),'${aes.key}') =: phoneNumber," +
                    "OR  (:secondPhoneNumber IS NULL OR PGP_SYM_DECRYPT(cast(mp.secondPhoneNumber as bytea),'${aes.key}') as secondPhoneNumber," ,
            nativeQuery = true)
    List<OnboardingProfile> search(
            @Nullable String id,
            @Nullable String firstName,
            @Nullable String middleName,
            @Nullable String lastName,
            @Nullable Integer socialSecurityNumber,
            @Nullable LocalDate birthDate,
            @Nullable String currentAddress,
            @Nullable String previousAddress,
            @Nullable String phoneNumber,
            @Nullable String secondPhoneNumber
    );

}

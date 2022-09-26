package com.objectcomputing.checkins.services.onboard.onboardee_about;

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
public interface OnboardeeAboutRespository extends CrudRepository<OnboardeeAbout, UUID> {
    List<OnboardeeAbout> findAll();

    @Query(value = "SELECT about_you_id, " +
            "PGP_SYM_DECRYPT(cast(mp.tshirt_size as bytea),'${aes.key}') as tshirt_size," +
            "PGP_SYM_DECRYPT(cast(mp.google_training as bytea),'${aes.key}') as google_training," +
            "PGP_SYM_DECRYPT(cast(mp.introduction as bytea),'${aes.key}') as introduction," +
            "vaccine_status," +
            "vaccine_Two_weeks," +
            "PGP_SYM_DECRYPT(cast(mp.other_training as bytea),'${aes.key}') as other_training," +
            "PGP_SYM_DECRYPT(cast(mp.additional_skills as bytea),'${aes.key}') as additional_skills," +
            "PGP_SYM_DECRYPT(cast(mp.certifications as bytea),'${aes.key}') as certifications " +
            "FROM \"onboardee_about\" mp " +
            "WHERE (:tshirtSize IS NULL OR PGP_SYM_DECRYPT(cast(mp.tshirt_size as bytea), '${aes.key}') = :tshirtSize) "
            +
            "AND  (:googleTraining IS NULL OR PGP_SYM_DECRYPT(cast(mp.google_training as bytea), '${aes.key}') = :googleTraining) "
            +
            "AND  (:introduction IS NULL OR PGP_SYM_DECRYPT(cast(mp.introduction as bytea), '${aes.key}') = :introduction) "
            +
            "AND  (:vaccineStatus IS NULL OR vaccine_status = :vaccineStatus) " +
            "AND  (:vaccineTwoWeeks IS NULL OR vaccine_two_weeks = :vaccineTwoWeeks) " +
            "AND  (:otherTraining IS NULL OR PGP_SYM_DECRYPT(cast(mp.other_training as bytea),'${aes.key}') = :otherTraining) "
            +
            "AND  (:additionalSkills IS NULL OR PGP_SYM_DECRYPT(cast(mp.additional_skills as bytea),'${aes.key}') = :additionalSkills) "
            +
            "AND  (:certifications IS NULL OR PGP_SYM_DECRYPT(cast(mp.certifications as bytea), '${aes.key}') = :certifications) ", nativeQuery = true)
    List<OnboardeeAbout> search(
            @Nullable UUID about_you_id,
            @Nullable String tshirtSize,
            @Nullable String googleTraining,
            @Nullable String introduction,
            @Nullable Boolean vaccineStatus,
            @Nullable Boolean vaccineTwoWeeks,
            @Nullable String otherTraining,
            @Nullable String additionalSkills,
            @Nullable String certifications);
}

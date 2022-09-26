package com.objectcomputing.checkins.services.onboardee_about;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OnboardeeAboutRespository extends ReactorCrudRepository<OnboardeeAbout, UUID> {

    Flux<OnboardeeAbout> findAll();

    @Query(value = "SELECT about_you_id, " +
            "PGP_SYM_DECRYPT(cast(mp.tshirtSize as bytea),'${aes.key}') as tshirtSize," +
            "PGP_SYM_DECRYPT(cast(mp.googleTraining as bytea),'${aes.key}') as googleTraining," +
            "PGP_SYM_DECRYPT(cast(mp.introduction as bytea),'${aes.key}') as introduction," +
            "vaccineStatus," +
            "vaccineTwoWeeks," +
            "PGP_SYM_DECRYPT(cast(mp.otherTraining as bytea),'${aes.key}') as otherTraining," +
            "PGP_SYM_DECRYPT(cast(mp.additionalSkills as bytea),'${aes.key}') as additionalSkills," +
            "PGP_SYM_DECRYPT(cast(mp.certifications as bytea),'${aes.key}') as certifications " +
            "FROM \"onboardee_about\" mp " +
            "WHERE (:tshirtSize IS NULL OR PGP_SYM_DECRYPT(cast(mp.tshirtSize as bytea), '${aes.key}') = :tshirtSize) "
            +
            "AND  (:googleTraining IS NULL OR PGP_SYM_DECRYPT(cast(mp.googleTraining as bytea), '${aes.key}') = :googleTraining) "
            +
            "AND  (:introduction IS NULL OR PGP_SYM_DECRYPT(cast(mp.introduction as bytea), '${aes.key}') = :introduction) "
            +
            "AND  (:vaccineStatus IS NULL OR vaccineStatus = :vaccineStatus) " +
            "AND  (:vaccineTwoWeeks IS NULL OR vaccineTwoWeeks = :vaccineTwoWeeks) " +
            "AND  (:otherTraining IS NULL OR PGP_SYM_DECRYPT(cast(mp.otherTraining as bytea),'${aes.key}') = :otherTraining) "
            +
            "AND  (:additionalSkills IS NULL OR PGP_SYM_DECRYPT(cast(mp.additionalSkills as bytea),'${aes.key}') = :additionalSkills) "
            +
            "AND  (:certifications IS NULL OR PGP_SYM_DECRYPT(cast(mp.certifications as bytea), '${aes.key}') = :certifications) ", nativeQuery = true)
    Mono<OnboardeeAbout> search(
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

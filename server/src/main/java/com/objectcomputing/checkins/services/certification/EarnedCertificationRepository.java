package com.objectcomputing.checkins.services.certification;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
interface EarnedCertificationRepository extends CrudRepository<EarnedCertification, UUID> {

    List<EarnedCertification> findAllOrderByEarnedDateDesc();

    List<EarnedCertification> findByCertificationId(@NotNull UUID certificationId);

    List<EarnedCertification> findByCertificationIdOrderByEarnedDateDesc(@NotNull UUID certificationId);

    List<EarnedCertification> findByMemberIdOrderByEarnedDateDesc(@NotNull UUID memberId);

    List<EarnedCertification> findByMemberIdAndCertificationIdOrderByEarnedDateDesc(@NotNull UUID memberId, @NotNull UUID certificationId);
}

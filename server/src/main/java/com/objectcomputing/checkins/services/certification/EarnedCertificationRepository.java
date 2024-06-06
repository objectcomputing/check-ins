package com.objectcomputing.checkins.services.certification;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
interface EarnedCertificationRepository extends CrudRepository<EarnedCertification, UUID> {

    List<EarnedCertification> findByCertificationId(@NotNull UUID certificationId);

    List<EarnedCertification> findByMemberId(@NotNull UUID memberId);

    List<EarnedCertification> findByMemberIdAndCertificationId(@NotNull UUID memberId, @NotNull UUID certificationId);
}

package com.objectcomputing.checkins.services.certification;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface EarnedCertificationRepository extends CrudRepository<EarnedCertification, UUID> {

    @Query("""
            SELECT earned.*
                FROM earned_certification AS earned
                    LEFT JOIN certification AS cert USING(certification_id)
                WHERE cert.is_active = TRUE OR :includeDeactivated = TRUE
                ORDER BY earned.earned_date DESC""")
    List<EarnedCertification> findAllOrderByEarnedDateDesc(boolean includeDeactivated);

    List<EarnedCertification> findByCertificationId(@NotNull UUID certificationId);

    @Query("""
            SELECT earned.*
                FROM earned_certification AS earned
                    LEFT JOIN certification AS cert USING(certification_id)
                WHERE earned.certification_id = :certificationId
                  AND (cert.is_active = TRUE OR :includeDeactivated = TRUE)
                ORDER BY earned.earned_date DESC""")
    List<EarnedCertification> findByCertificationIdOrderByEarnedDateDesc(@NotNull UUID certificationId, boolean includeDeactivated);

    @Query("""
            SELECT earned.*
                FROM earned_certification AS earned
                    LEFT JOIN certification AS cert USING(certification_id)
                WHERE earned.member_id = :memberId
                  AND (cert.is_active = TRUE OR :includeDeactivated = TRUE)
                ORDER BY earned.earned_date DESC""")
    List<EarnedCertification> findByMemberIdOrderByEarnedDateDesc(@NotNull UUID memberId, boolean includeDeactivated);

    @Query("""
            SELECT earned.*
                FROM earned_certification AS earned
                    LEFT JOIN certification AS cert USING(certification_id)
                WHERE earned.certification_id = :certificationId
                  AND earned.member_id = :memberId
                  AND (cert.is_active = TRUE OR :includeDeactivated = TRUE)
                ORDER BY earned.earned_date DESC""")
    List<EarnedCertification> findByMemberIdAndCertificationIdOrderByEarnedDateDesc(@NotNull UUID memberId, @NotNull UUID certificationId, boolean includeDeactivated);
}

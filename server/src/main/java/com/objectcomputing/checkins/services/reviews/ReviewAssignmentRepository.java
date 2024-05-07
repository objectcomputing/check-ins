package com.objectcomputing.checkins.services.reviews;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReviewAssignmentRepository extends CrudRepository<ReviewAssignment, UUID> {

    Set<ReviewAssignment> findByReviewPeriodId(UUID reviewPeriodId);

    Set<ReviewAssignment> findByReviewPeriodIdAndReviewerId(UUID reviewPeriodId, UUID reviewerId);

    void deleteByReviewPeriodId(UUID reviewPeriodId);
}

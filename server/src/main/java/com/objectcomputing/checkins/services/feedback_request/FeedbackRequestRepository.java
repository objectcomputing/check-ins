package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackRequestRepository extends CrudRepository<FeedbackRequest, UUID> {

    @Override
    <S extends FeedbackRequest> S save(@Valid @NotNull @Nonnull S entity);
    @Override
    <S extends FeedbackRequest> S update(@NotNull @Nonnull S entity);

    @Query(value = "SELECT * " +
            "FROM feedback_requests " +
            "WHERE (:creatorId IS NULL OR creatorId = CAST(:creatorId as varchar)) " +
            "AND (:requesteeId IS NULL OR requesteeId = CAST(:requesteeId as varchar)) " +
            "AND (:templateId IS NULL OR templateId = CAST(:templateId as varchar)) " +
            "AND (:oldestDate IS NULL OR sendDate >= :oldestDate)")
    List<FeedbackRequest> findByValues(@Nullable UUID creatorId, @Nullable UUID requesteeId, @Nullable UUID templateId, @Nullable LocalDate oldestDate);
}

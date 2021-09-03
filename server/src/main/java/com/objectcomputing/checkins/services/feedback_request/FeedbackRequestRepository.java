package com.objectcomputing.checkins.services.feedback_request;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackRequestRepository extends CrudRepository<FeedbackRequest, UUID> {

    @Override
    <S extends FeedbackRequest> S save(@Valid @NotNull @NonNull S entity);
    @Override
    <S extends FeedbackRequest> S update(@NotNull @NonNull S entity);

    @Query(value = "SELECT * " +
            "FROM feedback_requests " +
            "WHERE (:creatorId IS NULL OR creator_id = :creatorId) " +
            "AND (:requesteeId IS NULL OR requestee_id = :requesteeId) " +
            "AND (:recipientId IS NULL OR recipient_id = :recipientId) " +
            "AND (CAST(:oldestDate as date) IS NULL OR send_date >= :oldestDate) "
            ,nativeQuery = true)
    List<FeedbackRequest> findByValues(@Nullable String creatorId, @Nullable String requesteeId, @Nullable String recipientId, @Nullable LocalDate oldestDate);

    List<FeedbackRequest> findBySendDateAfter(@Nullable LocalDate sendDate);
}


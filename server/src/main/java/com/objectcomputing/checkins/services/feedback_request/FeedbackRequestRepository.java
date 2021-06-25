package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.guild.Guild;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackRequestRepository extends CrudRepository<FeedbackRequest, UUID> {

    @Override
    <S extends FeedbackRequest> S save(@Valid @NotNull @Nonnull S entity);

    List<FeedbackRequest> findByCreatorId(@NotNull UUID creatorId);

    @Query(value = "UPDATE feedback_requests SET status = :status where id =:id")
    FeedbackRequest updateStatus(@NotNull String status, UUID id);

    @Query(value = "UPDATE feedback_requests SET dueDate = :dueDate where id =:id")
    FeedbackRequest updateDueDate(@NotNull LocalDate dueDate, UUID id);

    @Query(value = "UPDATE feedback_requests SET submitDate = :submitDate, status = :status where id =:id")
    FeedbackRequest updateStatusAndSubmitDate(@NotNull LocalDate submitDate, @NotNull String status, UUID id);






}

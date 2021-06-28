package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.guild.Guild;
import io.micronaut.data.annotation.Id;
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


//    FeedbackRequest updateDueDate(@NotNull @Id UUID id, @NotNull LocalDate dueDate);

    @Query(value = "UPDATE feedback_requests SET dueDate = :dueDate where id = CAST(:id as varchar)")
    <S extends FeedbackRequest> S updateDueDate(@NotNull @Id UUID id, LocalDate dueDate );

    void update(@NotNull @Id UUID id, String status, LocalDate submitDate );


    @Query(value = "UPDATE feedback_requests SET submitDate = :submitDate, status = :status where id = CAST(:id as varchar)")
    FeedbackRequest updateStatusAndSubmitDate(@NotNull FeedbackRequest request);

    @Query(value = "SELECT * FROM feedback_requests WHERE requesteeId=CAST(:requesteeId as varchar) AND templateId = CAST(:templateId as varchar)")
    List<FeedbackRequest> findByRequesteeIdAndTemplateId(@NotNull UUID requesteeId, @NotNull UUID templateId);





}

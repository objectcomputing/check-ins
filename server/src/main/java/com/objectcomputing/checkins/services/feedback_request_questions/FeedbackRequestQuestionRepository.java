package com.objectcomputing.checkins.services.feedback_request_questions;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackRequestQuestionRepository extends CrudRepository<FeedbackRequestQuestion, UUID> {

    @Override
    <S extends FeedbackRequestQuestion> S save(@Valid @NotNull @Nonnull S entity);
    @Override
    <S extends FeedbackRequestQuestion> S update(@NotNull @Nonnull S entity);

    @Query(value = "SELECT id, requestId, PGP_SYM_DECRYPT(cast(questionContent as bytea), '${aes.key}') as questionContent, orderNum from feedback_request_questions WHERE requestId = :requestId ORDER BY orderNum", nativeQuery = true)
    List<FeedbackRequestQuestion> findByRequestId(@NotNull String requestId);
}

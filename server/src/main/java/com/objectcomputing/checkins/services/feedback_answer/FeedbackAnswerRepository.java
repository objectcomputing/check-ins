package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackAnswerRepository extends CrudRepository<FeedbackAnswer, UUID> {

    @Override
    <S extends FeedbackAnswer> S save(@NotNull @Valid S entity);

    @Override
    <S extends FeedbackAnswer> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends FeedbackAnswer> S update(@NotNull @Valid S entity);

    @Query("SELECT id, PGP_SYM_DECRYPT(cast(answer as bytea), '${aes.key}') as answer, question_id, request_id, sentiment " +
            "FROM feedback_answers WHERE (:questionId IS NULL OR question_id = :questionId) AND (request_id = :requestId)")
    List<FeedbackAnswer> getByQuestionIdAndRequestId(@Nullable String questionId, @Nullable String requestId);

}

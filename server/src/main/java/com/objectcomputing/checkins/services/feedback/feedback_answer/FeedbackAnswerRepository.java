package com.objectcomputing.checkins.services.feedback.feedback_answer;


import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackAnswerRepository extends CrudRepository<FeedbackAnswer, UUID> {

    @Override
    <S extends FeedbackAnswer> S save(@Valid @NotNull @Nonnull S entity);
    @Override
    <S extends FeedbackAnswer> S update(@NotNull @Nonnull S entity);
}

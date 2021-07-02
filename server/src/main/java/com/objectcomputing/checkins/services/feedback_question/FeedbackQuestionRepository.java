package com.objectcomputing.checkins.services.feedback_question;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackQuestionRepository extends CrudRepository<FeedbackQuestion, UUID> {

    @Override
    <S extends FeedbackQuestion> S save(@NotNull @Valid @NonNull S entity);

    @Override
    Optional<FeedbackQuestion> findById(UUID id);
}

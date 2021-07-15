package com.objectcomputing.checkins.services.feedback_template;


import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.reactivex.annotations.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackTemplateRepository extends CrudRepository<FeedbackTemplate, UUID> {

    List<FeedbackTemplate> findByTitleLike(String title);

    List<FeedbackTemplate> findByCreatedBy(UUID id);

    List<FeedbackTemplate> findByActive(Boolean active);

    @Override
    <S extends FeedbackTemplate> S save(@Valid @NotNull @NonNull S entity);

    @Override
    <S extends FeedbackTemplate> S update(@Valid @NotNull @NonNull S entity);

    Optional<FeedbackTemplate> findById(UUID id);

}

package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.data.annotation.Query;
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

    List<FeedbackTemplate> findByTitleLikeAndActive(String title, Boolean active);

    List<FeedbackTemplate> findByCreatorIdAndActive(UUID id, Boolean active);

    @Query(value = "UPDATE feedback_templates SET active = false WHERE id = :id")
    Optional<FeedbackTemplate> softDeleteById(@NotNull String id);

    @Override
    <S extends FeedbackTemplate> S save(@Valid @NotNull @NonNull S entity);

    @Override
    <S extends FeedbackTemplate> S update(@Valid @NotNull @NonNull S entity);

    Optional<FeedbackTemplate> findById(@NonNull UUID id);

}

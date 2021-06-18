package com.objectcomputing.checkins.services.feedback_template;


import com.objectcomputing.checkins.services.feedback.Feedback;
import com.objectcomputing.checkins.services.guild.Guild;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.reactivex.annotations.NonNull;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackTemplateRepository extends CrudRepository<FeedbackTemplate, UUID> {

    @Query(value = "SELECT * " +
            "FROM feedback_template " +
            "WHERE (:title IS NULL OR title = :title OR title LIKE '%:title%') "
            , nativeQuery = true)
    List<FeedbackTemplate> findByTitle(@NotNull String title);

    List<FeedbackTemplate> findByCreatedBy(@NotNull UUID createdBy);

    @Query(value = "")
    List<FeedbackTemplate> search(@Nullable String createdBy, @Nullable String title);

    @Override
    <S extends FeedbackTemplate> S save(@Valid @NotNull @NonNull S entity);

    Optional<FeedbackTemplate> findById(UUID id);

}

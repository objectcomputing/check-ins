package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackTemplateRepository extends CrudRepository<FeedbackTemplate, UUID> {

    @Query(value = "UPDATE feedback_templates SET active = false WHERE id = :id")
    Optional<FeedbackTemplate> softDeleteById(@NotNull String id);

    @Query("UPDATE feedback_templates SET active = false WHERE is_ad_hoc = true AND creator_id = :creatorId")
    void setAdHocInactiveByCreator(@Nullable String creatorId);

    @Override
    <S extends FeedbackTemplate> S save(@Valid @NotNull @NonNull S entity);

    @Override
    <S extends FeedbackTemplate> S update(@Valid @NotNull @NonNull S entity);

    Optional<FeedbackTemplate> findById(@NonNull UUID id);

    @Query(value = "SELECT * " +
            "FROM feedback_templates " +
            "WHERE (active = true)" +
            "AND (:creatorId IS NULL OR creator_id = :creatorId) " +
            "AND (:title IS NULL OR title LIKE CONCAT('%',:title,'%'))"
            , nativeQuery = true)
    List<FeedbackTemplate> searchByValues(@Nullable String creatorId, @Nullable String title);



}

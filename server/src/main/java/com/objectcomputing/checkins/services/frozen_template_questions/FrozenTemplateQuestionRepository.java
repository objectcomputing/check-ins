package com.objectcomputing.checkins.services.frozen_template_questions;

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
public interface FrozenTemplateQuestionRepository extends CrudRepository<FrozenTemplateQuestion, UUID> {

    @Override
    <S extends FrozenTemplateQuestion> S save(@Valid @NotNull @Nonnull S entity);

    @Override
    <S extends FrozenTemplateQuestion> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends FrozenTemplateQuestion> S update(@NotNull @Nonnull S entity);

    @Query(value = "SELECT id, frozen_template_id, PGP_SYM_DECRYPT(cast(question as bytea), '${aes.key}') as question, question_number from frozen_template_questions WHERE frozen_template_id = :frozenTemplateId ORDER BY question_number", nativeQuery = true)
    List<FrozenTemplateQuestion> findByFrozenTemplateId(@NotNull String frozenTemplateId);
}

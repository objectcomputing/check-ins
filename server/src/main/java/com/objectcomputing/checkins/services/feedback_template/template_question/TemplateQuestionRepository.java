package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TemplateQuestionRepository extends CrudRepository<TemplateQuestion, UUID> {

    @Override
    <S extends TemplateQuestion> S save(@NotNull @Valid @NonNull S entity);

    @Override
    <S extends TemplateQuestion> S update(@Valid @NotNull @NonNull S entity);

    @Override
    Optional<TemplateQuestion> findById(UUID id);

    @Query(value = "SELECT * from template_questions WHERE templateId = :templateId ORDER BY orderNum")
    List<TemplateQuestion> findByTemplateId(String templateId);

}

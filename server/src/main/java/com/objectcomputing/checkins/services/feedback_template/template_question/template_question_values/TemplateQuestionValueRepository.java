package com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TemplateQuestionValueRepository extends CrudRepository<TemplateQuestionValue, UUID> {

    @Override
    <S extends TemplateQuestionValue> S save(@NotNull @Valid @NonNull S entity);

    @Override
    <S extends TemplateQuestionValue> S update(@Valid @NotNull @NonNull S entity);

    @Override
    Optional<TemplateQuestionValue> findById(@NonNull UUID id);

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(option_text as bytea),'${aes.key}') as option_text, " +
            "question_id, " +
            "option_number " +
            "FROM template_question_values " +
            "WHERE (question_id = :questionId) " +
            "ORDER BY option_number",
            nativeQuery = true)
    List<TemplateQuestionValue> findByQuestionId(String questionId);

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(option_text as bytea),'${aes.key}') as option_text, " +
            "question_id, " +
            "option_number " +
            "FROM template_question_values" +
            "WHERE (option_number = :optionNumber) " +
            "AND (question_id= :questionId)", nativeQuery = true)
    List<TemplateQuestionValue> search(@NotNull String questionId, @NotNull Integer optionNumber);

}

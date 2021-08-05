package com.objectcomputing.checkins.services.feedback_template.template_question;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
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
public interface TemplateQuestionRepository extends CrudRepository<TemplateQuestion, UUID> {

    @Override
    <S extends TemplateQuestion> S save(@NotNull @Valid @NonNull S entity);

    @Override
    <S extends TemplateQuestion> S update(@Valid @NotNull @NonNull S entity);

    @Override
    Optional<TemplateQuestion> findById(@NonNull UUID id);

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(question as bytea),'${aes.key}') as question, " +
            "template_id," +
            "question_number " +
            "FROM template_questions " +
            "WHERE  (:templateId ORDER BY question_number) ",
            nativeQuery = true)
    List<TemplateQuestion> findByTemplateId(String templateId);

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(question as bytea),'${aes.key}') as question, " +
            "template_id," +
            "question_number " +
            "FROM template_questions " +
            "WHERE (question_number = :questionNumber) " +
            "AND (template_id = :templateId)", nativeQuery = true)
    List<TemplateQuestion> search(@NotNull String templateId, @NotNull Integer questionNumber);
}

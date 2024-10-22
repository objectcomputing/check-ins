package com.objectcomputing.checkins.services.feedback_external_recipient;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.DataType;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackExternalRecipientRepository extends CrudRepository<FeedbackExternalRecipient, UUID> {

    @Override
    <S extends FeedbackExternalRecipient> S save(@Valid @NotNull @NonNull S entity);
    @Override
    <S extends FeedbackExternalRecipient> S update(@NotNull @NonNull S entity);
}


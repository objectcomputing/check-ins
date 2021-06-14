package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackRequestRepository extends CrudRepository<FeedbackRequest, UUID> {

    @Override
    <S extends FeedbackRequest> S save(@Valid @NotNull @Nonnull S entity);
    List<FeedbackRequest> findByRequesteeId(@NotNull UUID requesteeId);
    List<FeedbackRequest> findByCreatorId(@NotNull UUID creatorId);
}

package com.objectcomputing.checkins.services.feedback;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackRepository extends CrudRepository<Feedback, UUID> {
    List<Feedback> searchByValues(@Nullable String sentBy,
                                  @Nullable String sentTo,
                                  @Nullable Boolean confidential);
}

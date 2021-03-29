package com.objectcomputing.checkins.services.feedback;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedbackRepository extends CrudRepository<Feedback, UUID> {

    @Query(value = "SELECT id, " +
            "PGP_SYM_DECRYPT(cast(content as bytea), '${aes.key}') as content, " +
            "sentTo, sentBy, confidential, createdOn, updatedOn " +
            "FROM feedback " +
            "WHERE (:sentBy IS NULL OR sentBy = :sentBy) " +
            "AND (:sentTo IS NULL OR sentTo = :sentTo) " +
            "AND (:confidential IS NULL OR confidential = :confidential)", nativeQuery = true)
    List<Feedback> searchByValues(@Nullable String sentBy,
                                  @Nullable String sentTo,
                                  @Nullable Boolean confidential);
}

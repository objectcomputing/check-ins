package com.objectcomputing.checkins.services.reviews;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReviewPeriodRepository extends CrudRepository<ReviewPeriod, UUID> {

    Optional<ReviewPeriod> findByName(String name);

    List<ReviewPeriod> findByNameIlike(String name);

    List<ReviewPeriod> findByOpen(boolean open);

}

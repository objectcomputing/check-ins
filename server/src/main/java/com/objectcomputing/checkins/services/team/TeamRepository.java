package com.objectcomputing.checkins.services.team;

import java.util.List;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TeamRepository extends CrudRepository<Team, UUID> {
    List<Team> findByName(String name);
    List<Team> findAll();
}

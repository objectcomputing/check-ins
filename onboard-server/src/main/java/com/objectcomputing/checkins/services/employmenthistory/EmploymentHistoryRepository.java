package com.objectcomputing.checkins.services.employmenthistory;

import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository (dialect = Dialect.POSTGRES)
public interface EmploymentHistoryRepository extends CrudRepository<EmploymentHistory, UUID> {
    
}

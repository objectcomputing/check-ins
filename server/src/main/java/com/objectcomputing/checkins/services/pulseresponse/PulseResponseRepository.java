package com.objectcomputing.checkins.services.pulseresponse;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PulseResponseRepository extends CrudRepository<PulseResponse, UUID> {
    
    List<PulseResponse> findByTeamMemberId(UUID teamMemberId);
    List<PulseResponse> findBySubmissionDateBetween(Date d1, Date d2);
}
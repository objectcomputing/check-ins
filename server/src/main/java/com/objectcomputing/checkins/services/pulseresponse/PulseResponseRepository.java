package com.objectcomputing.checkins.services.pulseresponse;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PulseResponseRepository extends CrudRepository<PulseResponse, UUID> {

    List<PulseResponse> findByTeamMemberId(@NotNull UUID teamMemberId);
    List<PulseResponse> findBySubmissionDateBetween(@NotNull LocalDate dateFrom, @NotNull LocalDate dateTo);
}
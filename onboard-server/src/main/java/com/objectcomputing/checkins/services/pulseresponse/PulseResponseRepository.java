package com.objectcomputing.checkins.services.pulseresponse;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PulseResponseRepository extends CrudRepository<PulseResponse, UUID> {

    List<PulseResponse> findByTeamMemberId(@NotBlank UUID teamMemberId);
    List<PulseResponse> findBySubmissionDateBetween(@NotBlank LocalDate dateFrom, @NotBlank LocalDate dateTo);
}
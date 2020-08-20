package com.objectcomputing.checkins.services.pulseresponse;

import java.util.Set;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PulseResponseRepository extends CrudRepository<PulseResponse, UUID> {
    
    Set<PulseResponse> findByTeamMemberId(@NotBlank UUID teamMemberId);
    Optional<PulseResponse> findByInternalFeelings(@NotBlank String internalFeelings);
    Optional<PulseResponse> findByExternalFeelings(@NotBlank String externalFeelings);
    Boolean existsByTeamMemberId(@NotBlank UUID teamMemberId);
    void deleteByTeamMemberId(@NotBlank UUID teamMemberId);
}
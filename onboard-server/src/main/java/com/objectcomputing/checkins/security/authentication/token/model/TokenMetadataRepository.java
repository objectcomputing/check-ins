package com.objectcomputing.checkins.security.authentication.token.model;

import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;

import java.util.UUID;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface TokenMetadataRepository extends ReactorCrudRepository<TokenMetadata, UUID> {
}

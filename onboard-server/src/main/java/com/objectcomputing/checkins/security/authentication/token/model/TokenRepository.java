package com.objectcomputing.checkins.security.authentication.token.model;

import com.objectcomputing.geoai.core.accessor.AccessorSource;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface TokenRepository extends ReactorCrudRepository<Token, UUID> {

    @Join(value = "policies", type = Join.Type.LEFT_FETCH)
    @Join(value = "meta", type = Join.Type.LEFT_FETCH)
    Mono<Token> findByAccessorIdAndAccessorSourceAndType(UUID accessorId, AccessorSource accessorSource, TokenType type);

    Mono<Long> updateTouches(@NotNull @Id UUID tokenId, long touches);
}

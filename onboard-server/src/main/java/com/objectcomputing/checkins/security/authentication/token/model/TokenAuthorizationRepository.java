package com.objectcomputing.checkins.security.authentication.token.model;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface TokenAuthorizationRepository extends ReactorCrudRepository<TokenAuthorization, UUID> {

    @Join("token")
    Mono<TokenAuthorization> findById(UUID id);

    @SuppressWarnings("SqlResolve")
    @Join(value = "token", alias = "token_")
    @Query(value=
            "SELECT token_authorization_.\"token_authorization_id\", " +
            " token_authorization_.\"token_id\", " +
            " token_authorization_.\"created_instant\", " +
            " token_authorization_.\"issued_instant\", " +
            " token_authorization_.\"lease\", " +
            " token_authorization_.\"not_before_time\", " +
            " token_authorization_token_.\"parent_token_id\"    AS token_parent_token_id, " +
            " token_authorization_token_.\"accessor_id\"        AS token_accessor_id, " +
            " token_authorization_token_.\"accessor_source\"    AS token_accessor_source, " +
            " token_authorization_token_.\"role_name\"          AS token_role_name, " +
            " token_authorization_token_.\"display_name\"       AS token_display_name, " +
            " token_authorization_token_.\"renewable\"          AS token_renewable, " +
            " token_authorization_token_.\"time_to_live\"       AS token_time_to_live, " +
            " token_authorization_token_.\"type\"               AS token_type, " +
            " token_authorization_token_.\"created_instant\"    AS token_created_instant, " +
            " token_authorization_token_.\"not_before_instant\" AS token_not_before_instant, " +
            " token_authorization_token_.\"max_number_of_uses\" AS token_max_number_of_uses, " +
            " token_authorization_token_.\"max_time_to_live\"   AS token_max_time_to_live, " +
            " token_authorization_token_.\"touches\"            AS token_touches " +
            "FROM ( " +
            "    SELECT inner_ta.*, rank() OVER ( ORDER BY inner_ta.issued_instant desc ) " +
            "    FROM token_authorization inner_ta " +
            "    WHERE(inner_ta.token_id = :tokenId " +
            "       AND (inner_ta.not_before_time is null or inner_ta.not_before_time < now())) " +
            ")  token_authorization_ " +
            "INNER JOIN \"token\" token_authorization_token_ " +
            "    ON token_authorization_.\"token_id\" = token_authorization_token_.\"token_id\" " +
            "WHERE(token_authorization_.rank = 1)", nativeQuery = true)
    Mono<TokenAuthorization> findActiveAuthorizationTokenId(UUID tokenId);

    Mono<Long> updateIssuedInstant(@NotNull @Id UUID id, Instant issuedInstant);
}

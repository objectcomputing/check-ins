package com.objectcomputing.checkins.newhire.model;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface NewHireAuthorizationCodeRepository extends ReactorCrudRepository<NewHireAuthorizationCodeEntity, UUID> {

    Flux<NewHireAuthorizationCodeEntity> findAllByNewHireAccountId(UUID id);

    @SuppressWarnings("SqlResolve")
    @Query(value = "SELECT uac.new_hire_authorization_code_id, uac.new_hire_id, uac.salt, uac.verifier, uac.purpose, uac.source, uac.issued_instant, uac.time_to_live, uac.consumed_instant " +
            "FROM new_hire_authorization_code as uac " +
            "WHERE(" +
            "(uac.user_account_id = :newHireAccountId)" +
            " AND " +
            "(uac.consumed_instant is null)" +
            " AND " +
            "((uac.issued_instant + (uac.time_to_live * cast('1 millisecond' as interval))) >= now())" +
            ")", nativeQuery = true)
    Flux<NewHireAuthorizationCodeEntity> findAllActiveUserAuthorizationCodesByUserAccountId(UUID newHireAccountId);

    @SuppressWarnings("SqlResolve")
    @Query(value = "SELECT uac.user_authorization_code_id, uac.user_account_id, uac.salt, uac.verifier, uac.purpose, uac.source, uac.issued_instant, uac.time_to_live, uac.consumed_instant " +
            "FROM user_authorization_code as uac " +
            "WHERE(" +
            "(uac.user_account_id = :newHireAccountId AND purpose = :purpose)" +
            " AND " +
            "(uac.consumed_instant is null)" +
            " AND " +
            "((uac.issued_instant + (uac.time_to_live * cast('1 millisecond' as interval))) >= now())" +
            ") " +
            "ORDER BY uac.issued_instant " +
            "LIMIT 1", nativeQuery = true)
    Mono<NewHireAuthorizationCodeEntity> findActiveUserAuthorizationCodesByUserAccountIdAndPurpose(UUID newHireAccountId, AuthorizationPurpose purpose);

    @SuppressWarnings("SqlResolve")
    @Query(value = "SELECT exists(SELECT 1 " +
            "FROM user_authorization_code as uac " +
            "WHERE(" +
            "(uac.user_account_id = :newHireAccountId AND purpose = :purpose)" +
            " AND " +
            "(uac.consumed_instant is null)" +
            " AND " +
            "((uac.issued_instant + (uac.time_to_live * cast('1 millisecond' as interval))) >= now())" +
            "))", nativeQuery = true)
    Mono<Boolean> hasAnActiveUserAuthorizationCodesByUserAccountIdAndPurpose(UUID newHireAccountId, AuthorizationPurpose purpose);

    @SuppressWarnings("SqlResolve")
    @Query(value = "SELECT exists(SELECT 1 " +
            "FROM new_hire_authorization_code as uac " +
            "WHERE(" +
            "(uac.new_hire_id = :newHireAccountId AND purpose = :purpose)" +
            " AND " +
            "(uac.consumed_instant is null)" +
            " AND " +
            "((uac.issued_instant + (uac.time_to_live * cast('1 millisecond' as interval))) < now())" +
            "))", nativeQuery = true)
    Mono<Boolean> hasAnInactiveUserAuthorizationCodesByUserAccountIdAndPurpose(UUID newHireAccountId, AuthorizationPurpose purpose);

    @SuppressWarnings("SqlResolve")
    @Query(value = "SELECT count( * ) " +
            "FROM new_hire_authorization_code as uac " +
            "WHERE(" +
            "(uac.new_hire_id = :newHireAccountId AND purpose = :purpose)" +
            " AND " +
            "(uac.consumed_instant is null)" +
            " AND " +
            "((uac.issued_instant + (uac.time_to_live * cast('1 millisecond' as interval))) < now())" +
            ")", nativeQuery = true)
    Mono<Long> countInactiveUserAuthorizationCodesByUserAccountIdAndPurpose(UUID newHireAccountId, AuthorizationPurpose purpose);

    @SuppressWarnings("SqlResolve")
    @Query(value = "UPDATE new_hire_authorization_code " +
            "SET consumed_instant = :consumedInstant " +
            "WHERE(" +
            "new_hire_id = :newHireAccountId" +
            " AND " +
            "purpose = :purpose" +
            " AND " +
            "consumed_instant is null" +
            ")", nativeQuery = true)
    Mono<Long> consumeAuthorizationCodes(UUID newHireAccountId, AuthorizationPurpose purpose, Instant consumedInstant);

    default Mono<Long> consumeAuthorizationCodes(UUID newHireAccountId, AuthorizationPurpose purpose) {
        return consumeAuthorizationCodes(newHireAccountId, purpose, Instant.now());
    }
}

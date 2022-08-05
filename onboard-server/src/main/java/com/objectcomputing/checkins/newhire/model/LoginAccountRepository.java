package com.objectcomputing.checkins.newhire.model;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.model.query.builder.sql.Dialect;

import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.micronaut.data.annotation.Join.Type.LEFT_FETCH;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface LoginAccountRepository extends ReactorCrudRepository<LoginAccount, UUID> {

    Mono<LoginAccount> findById(UUID id);

    @Join(value = "localUserCredentials", type = LEFT_FETCH )
    Mono<LoginAccount> findByEmailAddressWithLocalCredentials(String emailAddress);
    Mono<LoginAccount> findByEmailAddress(String emailAddress);
    Mono<Long> updateState(@NonNull @Id UUID id, @NonNull AccountState state);
}

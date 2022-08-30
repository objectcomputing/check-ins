package com.objectcomputing.checkins.newhire.model;

import com.objectcomputing.checkins.commons.AccountState;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.model.query.builder.sql.Dialect;

import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.micronaut.data.annotation.Join.Type.LEFT_FETCH;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface NewHireAccountRepository extends ReactorCrudRepository<NewHireAccountEntity, UUID> {

    Mono<NewHireAccountEntity> findById(UUID id);

    @Join(value = "newHireCredentials", type = LEFT_FETCH )
    Mono<NewHireAccountEntity> findByEmailAddress(String emailAddress);

    Mono<Long> updateState(@NonNull @Id UUID id, @NonNull AccountState state);
}

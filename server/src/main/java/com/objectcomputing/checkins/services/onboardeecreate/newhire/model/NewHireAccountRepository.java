package com.objectcomputing.checkins.services.onboardeecreate.newhire.model;

import com.objectcomputing.checkins.services.onboardeecreate.commons.AccountState;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static io.micronaut.data.annotation.Join.Type.LEFT_FETCH;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface NewHireAccountRepository extends CrudRepository<NewHireAccountEntity, UUID> {

    Optional<NewHireAccountEntity> findById(UUID id);

    @Join(value = "newHireCredentials", type = LEFT_FETCH )
    Optional<NewHireAccountEntity> findByEmailAddress(String emailAddress);

    Long updateState(@NonNull @Id UUID id, @NonNull AccountState state);
}

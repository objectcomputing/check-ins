package com.objectcomputing.checkins.services.system.model;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface SystemAccountRepository extends ReactorCrudRepository<SystemAccount, UUID> {

    @Query(value="select exists(select 1 from system_account where role = 'PlatformSuperAdministrator') as result", nativeQuery=true)
    boolean hasSuperAdministratorAccountBeenCreated();

    Mono<SystemAccount> findByIdentity(String identity);

}

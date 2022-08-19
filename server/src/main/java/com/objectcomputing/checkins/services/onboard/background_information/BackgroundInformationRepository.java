package com.objectcomputing.checkins.services.onboard.background_information;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface BackgroundInformationRepository extends ReactorCrudRepository<BackgroundInformation, UUID> {

    Flux<BackgroundInformation> findAll();

    @Query(value = "SELECT id, " +
            "stepComplete " +
            "FROM \"background_information\" mp " +
            "WHERE  (:stepComplete IS NULL OR stepComplete = :stepComplete) ",
            nativeQuery = true)
    Mono<BackgroundInformation> search(
            @Nullable String id,
            @Nullable Boolean stepComplete
    );
}

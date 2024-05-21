package com.objectcomputing.checkins.services.settings;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SettingsRepository extends CrudRepository<Setting, UUID> {

    boolean existsByName(String name);

    boolean existsByIdAndName(@NonNull UUID id, @NonNull String name);

    @NonNull
    List<Setting> findAll();

    @NonNull List<Setting> findByName(@NonNull String name);
}

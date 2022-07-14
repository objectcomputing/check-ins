package com.objectcomputing.checkins.services.settings;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SettingsRepository extends CrudRepository<Setting, UUID> {

    Optional<Setting> findById(UUID id);

    List<Setting> findByUserId(UUID userId);

    List<Setting> findAll();

}

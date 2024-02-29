package com.objectcomputing.checkins.services.skillcategory;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillCategoryRepository extends CrudRepository<SkillCategory, UUID> {

    Optional<SkillCategory> findById(@NonNull UUID id);

}

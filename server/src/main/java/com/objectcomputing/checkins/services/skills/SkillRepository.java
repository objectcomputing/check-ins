package com.objectcomputing.checkins.services.skills;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillRepository extends CrudRepository<Skill, UUID> {

    Optional<Skill> findByName(String name);

    List<Skill> findByNameIlike(String name);

    List<Skill> findByPending(boolean pending);

}

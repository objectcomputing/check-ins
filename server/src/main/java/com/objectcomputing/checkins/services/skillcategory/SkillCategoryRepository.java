package com.objectcomputing.checkins.services.skillcategory;

import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillCategoryRepository extends CrudRepository<SkillCategory, UUID> {

    @NonNull
    List<SkillCategory> findAll();

    SkillCategory findByName(String name);

    List<SkillCategory> findAllByName(String name);

}

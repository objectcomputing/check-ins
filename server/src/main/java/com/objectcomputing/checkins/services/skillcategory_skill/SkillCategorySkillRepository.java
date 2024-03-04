package com.objectcomputing.checkins.services.skillcategory_skill;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillCategorySkillRepository extends CrudRepository<SkillCategorySkill, SkillCategorySkillId> {
        List<SkillCategorySkill> findAll();

}

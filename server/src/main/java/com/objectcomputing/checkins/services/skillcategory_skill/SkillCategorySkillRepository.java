package com.objectcomputing.checkins.services.skillcategory_skill;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillCategorySkillRepository extends CrudRepository<SkillCategorySkill, SkillCategorySkillId> {
        List<SkillCategorySkill> findAll();

        List<SkillCategorySkill> findAllBySkillCategoryId(UUID categoryId);
}

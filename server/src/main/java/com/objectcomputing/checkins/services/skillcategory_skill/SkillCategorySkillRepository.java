package com.objectcomputing.checkins.services.skillcategory_skill;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillCategorySkillRepository extends CrudRepository<SkillCategorySkill, SkillCategorySkillId> {

        @NonNull
        List<SkillCategorySkill> findAll();

        @Query("SELECT s.name " +
                "FROM skillcategory_skills ss " +
                "         JOIN public.skills s on s.id = ss.skill_id " +
                "WHERE ss.skillcategory_id = :skillCategoryId")
        List<String> findSkillNamesBySkillCategoryId(String skillCategoryId);

        List<SkillCategorySkill> findAllBySkillCategoryId(UUID categoryId);
}

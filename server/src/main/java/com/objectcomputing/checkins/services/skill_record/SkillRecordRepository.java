package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.services.skillcategory_skill.SkillCategorySkillId;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillRecordRepository extends CrudRepository<SkillRecord, SkillCategorySkillId> {

    @NonNull
    List<SkillRecord> findAll();

}

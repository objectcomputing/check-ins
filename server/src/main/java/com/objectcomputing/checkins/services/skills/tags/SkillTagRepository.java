package com.objectcomputing.checkins.services.skills.tags;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillTagRepository extends CrudRepository<SkillTag, UUID> {

    @Query("SELECT * " +
            "FROM skill_tags st_ " +
            "LEFT JOIN skill_skill_tag sst_ " +
            "   ON st_.id = sst_.skill_tag_id " +
            "LEFT JOIN skills s_ " +
            "   ON sst_.skill_id = s_.id " +
            "WHERE (:name IS NULL OR st_.name LIKE :name) " +
            "AND (:skillId IS NULL OR s_.id = :skillId)")
    List<SkillTag> search(@Nullable String name, @Nullable String skillId);
}

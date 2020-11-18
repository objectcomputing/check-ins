package com.objectcomputing.checkins.services.skills.tags;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillTagRepository extends CrudRepository<SkillTag, UUID> {

    Optional<SkillTag> findByName(@NotNull String name);

    @Query("SELECT * " +
            "FROM skill_tag st_ " +
            "OUTER JOIN skill_skill_tag sst_ " +
            "   ON st_.id = sst_.skill_tag_id " +
            "OUTER JOIN skills s_ " +
            "   ON s_.id = sst.skill_id " +
            "WHERE (:name IS NULL OR st_.name = :name) " +
            "AND (:skillId IS NULL OR s_.id = :skillId)")
    List<SkillTagResponseDTO> search(String name, String skillId);
}

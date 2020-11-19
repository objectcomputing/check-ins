package com.objectcomputing.checkins.services.skills;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SkillRepository extends CrudRepository<Skill, UUID> {

    Optional<Skill> findByName(String name);

    List<Skill> findByNameIlike(String name);

    List<Skill> findByPending(boolean pending);

    @Override
    @Join(value = "tags", type = Join.Type.LEFT_FETCH)
    Optional<Skill> findById(@NonNull @NotNull UUID id);

    @NonNull
    Skill update(@NonNull @NotNull Skill updateMe);

}

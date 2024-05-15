package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface EntityTagRepository extends CrudRepository<EntityTag, UUID> {

        @Query("SELECT * " +
                "FROM entity_tags EntityTag " +
                "WHERE (:entityId  IS NULL OR EntityTag.entity_id = :entityId) " +
                "AND (:type  IS NULL OR EntityTag.type = :type) " +
                "AND (:tagId IS NULL OR EntityTag.tag_id = :tagId) " )
        Set<EntityTag> search(@Nullable String entityId, @Nullable String tagId, @Nullable EntityType type);


        @Override
        <S extends EntityTag> List<S> saveAll(@Valid @NotNull Iterable<S> tags);

        @Override
        <S extends EntityTag> S save(@Valid @NotNull @NonNull S tag);
}

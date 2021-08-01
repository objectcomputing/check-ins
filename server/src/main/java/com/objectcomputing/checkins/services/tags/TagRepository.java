package com.objectcomputing.checkins.services.tags;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.core.annotation.NonNull;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TagRepository extends CrudRepository<Tag, UUID>{

        @Query("SELECT * " +
                "FROM tags tag " +
                "WHERE (:name  IS NULL OR UPPER(tag.name) = UPPER(:name)) " )
        Set<Tag> search(@Nullable String name);

        @Override
        <S extends Tag> List<S> saveAll(@Valid @NotNull Iterable<S> tags);

        @Override
        <S extends Tag> S save(@Valid @NotNull @NonNull S tag);

        List<Tag> findByNameIlike(String name);
}

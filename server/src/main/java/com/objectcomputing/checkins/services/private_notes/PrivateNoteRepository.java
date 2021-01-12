package com.objectcomputing.checkins.services.private_notes;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PrivateNoteRepository extends CrudRepository<PrivateNote, UUID> {

}
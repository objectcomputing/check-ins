package com.objectcomputing.checkins.services.checkindocument;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CheckinDocumentRepository extends CrudRepository<CheckinDocument, UUID> {
    
    List<CheckinDocument> findByCheckinsId(@NotBlank UUID checkinsId);
}
package com.objectcomputing.checkins.services.checkindocument;

import java.util.Set;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CheckinDocumentRepository extends CrudRepository<CheckinDocument, UUID> {
    
    Set<CheckinDocument> findByCheckinsId(@NotBlank UUID checkinsId);
    Optional<CheckinDocument> findByUploadDocId(@NotBlank String uploadDocId);
    Boolean existsByCheckinsId(@NotBlank UUID checkinsId);
    Boolean existsByUploadDocId(@NotBlank String uploadDocId);
    void deleteByCheckinsId(@NotBlank UUID checkinsId);
    void deleteByUploadDocId(@NotBlank String uploadDocId);
}
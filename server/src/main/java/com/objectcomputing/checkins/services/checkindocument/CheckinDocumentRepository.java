package com.objectcomputing.checkins.services.checkindocument;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CheckinDocumentRepository extends CrudRepository<CheckinDocument, UUID> {
    
    Set<CheckinDocument> findByCheckinsId(@NotNull UUID checkinsId);
    Optional<CheckinDocument> findByUploadDocId(@NotBlank String uploadDocId);
    Boolean existsByCheckinsId(@NotNull UUID checkinsId);
    Boolean existsByUploadDocId(@NotBlank String uploadDocId);
    void deleteByCheckinsId(@NotNull UUID checkinsId);
    void deleteByUploadDocId(@NotBlank String uploadDocId);
}